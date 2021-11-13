package degubi.mapping;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

public final class ObjectMapperUtils {
    private static final HashMap<Class<?>, ObjectMapper<?>> MAPPER_CACHE = new HashMap<>();


    @SuppressWarnings("unchecked")
    public static<T> ObjectMapper<T> createMapper(Class<T> type) {
        return (ObjectMapper<T>) MAPPER_CACHE.computeIfAbsent(type, ObjectMapper::new);
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

    private ObjectMapperUtils() {}
}