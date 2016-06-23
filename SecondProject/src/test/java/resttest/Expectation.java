/**
 * 
 */
package resttest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author napattan
 *
 */
@SuppressWarnings("rawtypes")
@Retention(RetentionPolicy.RUNTIME)
@Target ({ElementType.METHOD})
@Repeatable(Expectations.class)
public @interface Expectation {
	Class type();
	String method();
	int times() default 1;
	Class[] paramTypes() default NoParam.class;
	ReturnVal expect();
}