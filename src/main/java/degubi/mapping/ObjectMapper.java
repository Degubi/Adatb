package degubi.mapping;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

public final class ObjectMapper<T> {
    private static final HashMap<Class<?>, ObjectMapper<?>> MAPPER_CACHE = new HashMap<>();

    final Constructor<T> constructor;
    final Class<?>[] parameterTypes;
    public final String[] parameterFieldNames;
    public final String[] dbFieldNames;
    public final String tableName;
    public final String listAllQuery;
    public final String primaryKeyFieldName;
    public final Field primaryKeyField;
    public final boolean isPrimaryKeyAutoincremented;
    final Method valuesCreator;

    @SuppressWarnings("unchecked")
    public ObjectMapper(Class<T> type) {
        var constructor = Arrays.stream(type.getDeclaredConstructors())
                                .filter(k -> k.isAnnotationPresent(MappingConstructor.class))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Unable to find mapping constructor in " + type));

        var parameters = constructor.getParameters();
        var parameterCount = parameters.length;
        var parameterTypes = new Class[parameterCount];
        var parameterFieldNames = new String[parameterCount];
        var dbFieldNames = new String[parameterCount];
        var foreignKeys = new ArrayList<ForeignKeyInfo>();

        var optionalTableNameAnnotation = type.getDeclaredAnnotation(MappingTable.class);
        var optionalTableName = optionalTableNameAnnotation != null ? optionalTableNameAnnotation.value() : null;

        for(var i = 0; i < parameterCount; ++i) {
            var parameter = parameters[i];
            var mappingParamAnnotation = parameter.getDeclaredAnnotation(MappingParameter.class);
            var mappingField = mappingParamAnnotation.value();
            var parameterType = parameter.getType();
            var localKey = mappingParamAnnotation.localKey();
            var foreignKey = mappingParamAnnotation.foreignKey();
            var isForeignKey = !localKey.isEmpty() && !foreignKey.isEmpty();

            parameterTypes[i] = parameterType;
            parameterFieldNames[i] = mappingField;
            dbFieldNames[i] = isForeignKey ? localKey : mappingField;

            if(isForeignKey) {
                foreignKeys.add(new ForeignKeyInfo(localKey, foreignKey, parameterType.getAnnotation(MappingTable.class).value()));
            }
        }

        var primaryKeyField = optionalTableName == null
                             ? null
                             : Arrays.stream(type.getDeclaredFields())
                                     .filter(k -> k.isAnnotationPresent(MappingPrimaryKey.class))
                                     .findFirst()
                                     .orElseThrow(() -> new IllegalStateException("Unable to find primary key field in " + type));

        this.parameterTypes = parameterTypes;
        this.parameterFieldNames = parameterFieldNames;
        this.dbFieldNames = dbFieldNames;
        this.constructor = (Constructor<T>) constructor;
        this.tableName = optionalTableName;
        this.listAllQuery = optionalTableName != null ? generateListAllQuery(optionalTableName, foreignKeys) : null;
        this.primaryKeyFieldName = primaryKeyField == null ? null : primaryKeyField.getAnnotation(MappingPrimaryKey.class).value();
        this.primaryKeyField = primaryKeyField;
        this.isPrimaryKeyAutoincremented = primaryKeyField == null ? false : primaryKeyField.getAnnotation(MappingPrimaryKey.class).autoIncrement();
        this.valuesCreator = Arrays.stream(type.getDeclaredMethods())
                                   .filter(k -> k.isAnnotationPresent(MappingValuesCreator.class))
                                   .filter(k -> k.getParameterCount() == 0)
                                   .filter(k -> k.getReturnType() == Map.class)
                                   .findFirst()
                                   .orElse(null);
    }


    @SuppressWarnings("unchecked")
    public static<T> ObjectMapper<T> createMapper(Class<T> type) {
        return (ObjectMapper<T>) MAPPER_CACHE.computeIfAbsent(type, ObjectMapper::new);
    }

    private static String generateListAllQuery(String tableName, ArrayList<ForeignKeyInfo> foreignKeys) {
        var hasForeignObjects = !foreignKeys.isEmpty();
        var foreignSelects = foreignKeys.stream()
                                        .map(k -> k.foreignTable + ".*")
                                        .collect(Collectors.joining(", "));

        var joins = foreignKeys.stream()
                               .map(k -> "INNER JOIN " + k.foreignTable + " ON " + tableName + '.' + k.localKey + " = " + k.foreignTable + "." + k.foreignKey)
                               .collect(Collectors.joining(" "));

        var baseSelect = hasForeignObjects ? "SELECT " + tableName + ".*, " + foreignSelects
                                           : "SELECT *";

        return baseSelect + " FROM " + tableName + (hasForeignObjects ? (' ' + joins) : "");
    }

    public static<T> String generateInsertQuery(T obj) {
        @SuppressWarnings("unchecked")
        var mapper = ObjectMapper.createMapper((Class<T>) obj.getClass());
        var valuesMap = ObjectMapper.createFieldValuesMap(mapper, obj);
        var fieldNames = mapper.parameterFieldNames;
        var valuesString = IntStream.range(0, fieldNames.length)
                                    .mapToObj(i -> mapper.isPrimaryKeyAutoincremented && fieldNames[i].equals(mapper.primaryKeyFieldName) ? "NULL" : formatValueForSQL(valuesMap.get(fieldNames[i])))
                                    .collect(Collectors.joining(", "));

        return "INSERT INTO " + mapper.tableName +
                " (" + String.join(", ", mapper.dbFieldNames) +
                ") VALUES (" + valuesString + ")";
    }

    @SuppressWarnings("unchecked")
    public static<T> String generateUpdateQuery(T oldObj, T newObj) {
        var oldMapper = ObjectMapper.createMapper((Class<T>) oldObj.getClass());
        var newMapper = ObjectMapper.createMapper((Class<T>) newObj.getClass());
        var primaryKeyFieldName = newMapper.primaryKeyFieldName;
        var parameterFieldNames = newMapper.parameterFieldNames;
        var dbFieldNames = newMapper.dbFieldNames;

        var oldValues = ObjectMapper.createFieldValuesMap(oldMapper, oldObj);
        var newValues = ObjectMapper.createFieldValuesMap(newMapper, newObj);
        var setPartString = IntStream.range(0, parameterFieldNames.length)
                                  .filter(i -> !parameterFieldNames[i].equals(primaryKeyFieldName))
                                  .mapToObj(i -> dbFieldNames[i] + " = " + formatValueForSQL(newValues.get(parameterFieldNames[i])))
                                  .collect(Collectors.joining(", "));

        return "UPDATE " + newMapper.tableName +
               " SET " + setPartString +
               " WHERE " + primaryKeyFieldName + " = " + formatValueForSQL(oldValues.get(primaryKeyFieldName));
    }

    public static<T> String generateDeleteQuery(T obj) {
        var mapper = ObjectMapper.createMapper(obj.getClass());
        var keyField = mapper.primaryKeyField;

        try {
            var keyValue = keyField.get(obj);
            var sqlKeyValue = keyField.getType() == String.class ? "'" + keyValue + "'" : keyValue;

            return "DELETE FROM " + mapper.tableName + " WHERE " + mapper.primaryKeyFieldName + " = " + sqlKeyValue;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to generate delete query!");
        }
    }

    public static<T> T createInstance(ResultSet resultSet, ObjectMapper<T> mapper) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        return createInstanceInternal(resultSet, "", mapper);
    }

    @SuppressWarnings("unchecked")
    public static<T> Map<String, Object> createFieldValuesMap(ObjectMapper<T> mapper, T obj) {
        try {
            return (Map<String, Object>) mapper.valuesCreator.invoke(obj);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to create value mappings for: " + obj.getClass());
        }
    }

    public static String formatValueForSQL(Object obj) {
        var type = obj.getClass();

        return type == String.class ? "'" + obj + "'" :
               type == Boolean.class ? (((Boolean) obj).booleanValue() ? "1" : "0") :
               type == Integer.class || type == Long.class ? String.valueOf(obj) : getPrimaryKeyFromNestedObject(type, obj);
    }

    private static String getPrimaryKeyFromNestedObject(Class<?> type, Object obj) {
        var mapper = ObjectMapper.createMapper(type);

        try {
            return formatValueForSQL(mapper.primaryKeyField.get(obj));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalStateException("Wut");
        }
    }

    private static<T> T createInstanceInternal(ResultSet resultSet, String fieldPrefix, ObjectMapper<T> mapper) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        var parameterFieldNames = mapper.parameterFieldNames;
        var parameterFieldTypes = mapper.parameterTypes;
        var parameterCount = parameterFieldNames.length;
        var resultParameters = new Object[parameterCount];

        for(var i = 0; i < parameterCount; ++i) {
            var fieldName = fieldPrefix + parameterFieldNames[i];
            var parameterType = parameterFieldTypes[i];

            if(isSQLType(parameterType)) {
                resultParameters[i] = resultSet.getObject(fieldName);
            }else{
                var nestedMapper = createMapper(parameterType);
                var tablePrefix = nestedMapper.tableName + '.';

                resultParameters[i] = isColumnPresent(resultSet, tablePrefix + nestedMapper.parameterFieldNames[0])
                                    ? createInstanceInternal(resultSet, tablePrefix, nestedMapper)
                                    : null;
            }
        }

        return mapper.constructor.newInstance(resultParameters);
    }

    private static boolean isColumnPresent(ResultSet resultSet, String column) {
        try {
            resultSet.findColumn(column);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static boolean isSQLType(Class<?> type) {
        return type == int.class || type == String.class || type == long.class || type == boolean.class;
    }


    private static final class ForeignKeyInfo {

        public final String localKey;
        public final String foreignKey;
        public final String foreignTable;

        public ForeignKeyInfo(String localKey, String foreignKey, String foreignTable) {
            this.localKey = localKey;
            this.foreignKey = foreignKey;
            this.foreignTable = foreignTable;
        }
    }
}