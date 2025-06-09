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
            throw new CustomBackupSynchronizedException("다른 백업 작업이 진행 중입니다. 완료 후 다시 시도해주세요.");
        }

        try {
            return joinPoint.proceed();
        } finally {
            lock.unlock();
            lockMap.remove(key, lock);
        }
    }
}
