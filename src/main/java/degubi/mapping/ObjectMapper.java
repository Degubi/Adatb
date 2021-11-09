package degubi.mapping;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

public final class ObjectMapper {
    private static final HashMap<Class<?>, MappingReflectionResult<?>> MAPPER_CACHE = new HashMap<>();


    public static<T> T createInstance(ResultSet resultSet, Class<T> type) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        return createInstanceInternal(resultSet, "", type);
    }

    private static<T> T createInstanceInternal(ResultSet resultSet, String fieldPrefix, Class<T> type) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        var mapper = createMapper(type);
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
                                    ? createInstanceInternal(resultSet, tablePrefix, parameterType)
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

    @SuppressWarnings("unchecked")
    private static<T> MappingReflectionResult<T> createMapper(Class<T> type) {
        return (MappingReflectionResult<T>) MAPPER_CACHE.computeIfAbsent(type, MappingReflectionResult::new);
    }

    private ObjectMapper() {}
}