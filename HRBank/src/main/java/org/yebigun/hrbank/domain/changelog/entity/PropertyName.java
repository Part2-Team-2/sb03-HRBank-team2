package org.yebigun.hrbank.domain.changelog.entity;

import lombok.Getter;

// 
@Getter
public enum PropertyName {
    NAME("이름"),
    EMAIL("이메일"),
    DEPARTMENT("부서명"),
    POSITION("직함"),
    HIRE_DATE("입사일"),
    EMPLOYEE_NUMBER("사번"),
    STATUS("상태");

    private final String label;

    PropertyName(String label) {
        this.label = label;
    }


}
