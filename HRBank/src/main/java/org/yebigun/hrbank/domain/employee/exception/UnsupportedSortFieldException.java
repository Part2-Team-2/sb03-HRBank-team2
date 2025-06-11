package org.yebigun.hrbank.domain.employee.exception;

import org.yebigun.hrbank.global.exception.UnsupportedException;

public class UnsupportedSortFieldException extends UnsupportedException {

    public UnsupportedSortFieldException(String message) {
        super(message);
    }
}
