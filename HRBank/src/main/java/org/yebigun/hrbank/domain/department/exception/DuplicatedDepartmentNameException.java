package org.yebigun.hrbank.domain.department.exception;

import org.yebigun.hrbank.global.exception.DuplicateException;

public class DuplicatedDepartmentNameException extends DuplicateException {

    public DuplicatedDepartmentNameException(String message) {
        super(message);
    }
}
