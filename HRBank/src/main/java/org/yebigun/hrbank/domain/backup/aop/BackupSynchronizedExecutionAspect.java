package org.yebigun.hrbank.domain.backup.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.yebigun.hrbank.global.exception.CustomBackupSynchronizedException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.aop
 * FileName     : BackupLoggingAspect
 * Author       : dounguk
 * Date         : 2025. 6. 9.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class BackupSynchronizedExecutionAspect {

    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Around("@annotation(SynchronizedExecution)")
    public Object synchronizedException(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().getName();
        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());


        boolean locked = lock.tryLock();

        if(!locked) {
            throw new CustomBackupSynchronizedException("이미 진행중인 백업이 있음");
        }

        try {
            return joinPoint.proceed();
        } finally {
            lock.unlock();
            lockMap.remove(key, lock);
        }
    }
}
