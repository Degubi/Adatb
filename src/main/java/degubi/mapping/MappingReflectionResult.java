package degubi.mapping;

import java.lang.reflect.*;

final class MappingReflectionResult<T> {

    public final Constructor<T> constructor;
    public final Class<?>[] parameterTypes;
    public final String[] parameterFieldNames;
    public final String tableName;

    public MappingReflectionResult(Class<T> type) {
        var constructor = findMappingConstructor(type);
        var parameters = constructor.getParameters();
        var parameterCount = parameters.length;
        var parameterTypes = new Class[parameterCount];
        var parameterFieldNames = new String[parameterCount];
        var optionalTableNameAnnotation = type.getDeclaredAnnotation(MappingTable.class);

        for(var i = 0; i < parameterCount; ++i) {
            var parameter = parameters[i];

            parameterTypes[i] = parameter.getType();
            parameterFieldNames[i] = parameter.getDeclaredAnnotation(MappingParameter.class).value();
        }

        this.parameterTypes = parameterTypes;
        this.parameterFieldNames = parameterFieldNames;
        this.constructor = constructor;
        this.tableName = optionalTableNameAnnotation != null ? optionalTableNameAnnotation.value() : null;
    }

    @SuppressWarnings("unchecked")
    private static<T> Constructor<T> findMappingConstructor(Class<T> type) {
        var constructors = type.getDeclaredConstructors();

        for(var constructor : constructors) {
            if(constructor.isAnnotationPresent(MappingConstructor.class)) {
                return (Constructor<T>) constructor;
            }
        }

        throw new IllegalStateException("Unable to find mapping constructor in " + type);
    }
}