package org.yebigun.hrbank.domain.employee.exception;

import org.yebigun.hrbank.global.exception.NotFoundException;

public class NotFoundEmployeeException extends NotFoundException {

    public NotFoundEmployeeException(String message) {
        super(message);
    }
}
