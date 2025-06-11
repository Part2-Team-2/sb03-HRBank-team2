package org.yebigun.hrbank.domain.department.exception;

import org.yebigun.hrbank.global.exception.NotFoundException;

public class NotFoundDepartmentException extends NotFoundException {

    public NotFoundDepartmentException(String message) {
        super(message);
    }
}
