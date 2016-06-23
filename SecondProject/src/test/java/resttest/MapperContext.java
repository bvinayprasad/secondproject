/**
 * 
 */
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
@Target(ElementType.TYPE)
public @interface MapperContext {
Class[] names();
Class[] depends() default Object.class;
}