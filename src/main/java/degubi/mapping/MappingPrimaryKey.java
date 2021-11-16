package degubi.mapping;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

@Retention(RUNTIME)
@Target(FIELD)
public @interface MappingPrimaryKey {
    String value();
    boolean autoIncrement();
}