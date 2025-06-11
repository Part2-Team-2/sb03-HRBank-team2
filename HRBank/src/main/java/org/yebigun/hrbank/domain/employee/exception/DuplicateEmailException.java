package org.yebigun.hrbank.domain.employee.exception;

import org.yebigun.hrbank.global.exception.DuplicateException;

public class DuplicateEmailException extends DuplicateException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
