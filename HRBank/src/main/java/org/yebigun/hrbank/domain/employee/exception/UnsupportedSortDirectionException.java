package org.yebigun.hrbank.domain.employee.exception;

import org.yebigun.hrbank.global.exception.UnsupportedException;

public class UnsupportedSortDirectionException extends UnsupportedException {

    public UnsupportedSortDirectionException(String message) {
        super(message);
    }
}
