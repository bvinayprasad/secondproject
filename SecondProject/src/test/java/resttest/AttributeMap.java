
package resttest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author napattan
 *
 */
@SuppressWarnings("rawtypes")
@Retention(RetentionPolicy.RUNTIME)
@Target ({ElementType.FIELD})
public @interface AttributeMap {
	Class type() default Object.class;
	String name() default "";
	String value() default "";
}