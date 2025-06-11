package org.yebigun.hrbank.domain.department.exception;

import org.yebigun.hrbank.global.exception.UnsupportedException;

public class DepartmentHasEmployeesException extends UnsupportedException {

    public DepartmentHasEmployeesException(String message) {
        super(message);
    }
}
