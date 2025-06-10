package org.yebigun.hrbank.domain.backup.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.aop
 * FileName     : SynchronizedExecution
 * Author       : dounguk
 * Date         : 2025. 6. 9.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SynchronizedExecution {
}
