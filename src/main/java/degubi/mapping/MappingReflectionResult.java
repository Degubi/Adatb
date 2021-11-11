package degubi.mapping;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public final class MappingReflectionResult<T> {

    public final Constructor<T> constructor;
    public final Class<?>[] parameterTypes;
    public final String[] parameterFieldNames;
    public final String tableName;
    public final String listAllQuery;
    public final String primaryKeyFieldName;
    public final Field primaryKeyField;

    @SuppressWarnings("unchecked")
    public MappingReflectionResult(Class<T> type) {
        var constructor = Arrays.stream(type.getDeclaredConstructors())
                                .filter(k -> k.isAnnotationPresent(MappingConstructor.class))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Unable to find mapping constructor in " + type));

        var parameters = constructor.getParameters();
        var parameterCount = parameters.length;
        var parameterTypes = new Class[parameterCount];
        var parameterFieldNames = new String[parameterCount];
        var foreignKeys = new ArrayList<ForeignKeyInfo>();

        var optionalTableNameAnnotation = type.getDeclaredAnnotation(MappingTable.class);
        var optionalTableName = optionalTableNameAnnotation != null ? optionalTableNameAnnotation.value() : null;

        for(var i = 0; i < parameterCount; ++i) {
            var parameter = parameters[i];
            var mappingParamAnnotation = parameter.getDeclaredAnnotation(MappingParameter.class);
            var mappingField = mappingParamAnnotation.value();
            var parameterType = parameter.getType();

            parameterTypes[i] = parameterType;
            parameterFieldNames[i] = mappingField;

            var localKey = mappingParamAnnotation.localKey();
            var foreignKey = mappingParamAnnotation.foreignKey();

            if(!localKey.isEmpty() && !foreignKey.isEmpty()) {
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
        this.constructor = (Constructor<T>) constructor;
        this.tableName = optionalTableName;
        this.listAllQuery = optionalTableName != null ? generateListAllQuery(optionalTableName, foreignKeys) : null;
        this.primaryKeyFieldName = primaryKeyField == null ? null : primaryKeyField.getAnnotation(MappingPrimaryKey.class).value();
        this.primaryKeyField = primaryKeyField;
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