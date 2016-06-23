package resttest;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.Void;


/**
 * @author napattan
 *
 */
@SuppressWarnings("rawtypes")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.LOCAL_VARIABLE})
public @interface ReturnVal {
Class type() default Void.class;
String value() default "";
}
