package degubi.mapping;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface MappingParameter {
    String value();
    String localKey() default "";
    String foreignKey() default "";
}