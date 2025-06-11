package org.yebigun.hrbank.domain.employee.exception;

import org.yebigun.hrbank.global.exception.UnsupportedException;

public class UnsupportedGroupByException extends UnsupportedException {

    public UnsupportedGroupByException(String message) {
        super(message);
    }
}
