package crmcontainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * definiert einen Setter als DI
 * 
 * @author bernd ledig
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Inject {
	
	/**
	 * optionaler Name des zu Injecten Objects 
	 * @return
	 */
	String value() default "";
	
	/**
	 * Parameter, welcher definiert, dass diese Injection optional ist
	 * @return
	 */
	boolean optional() default false;
}
