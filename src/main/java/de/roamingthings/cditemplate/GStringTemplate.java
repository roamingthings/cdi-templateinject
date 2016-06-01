package de.roamingthings.cditemplate;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author alxs
 * @version 2016/05/18
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, PARAMETER, METHOD})
public @interface GStringTemplate {
    @Nonbinding String resourcePath() default "";
    @Nonbinding String value() default "";
}
