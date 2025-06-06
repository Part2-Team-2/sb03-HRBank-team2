package org.yebigun.hrbank.domain.binaryContent.storage;

import org.springframework.stereotype.Component;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.domain.employee.entity.Employee;

import java.util.List;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.storage
 * FileName     : FileGenerator
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
@Component
public class FileGenerator {
    String[] column = {"ID", "직원번호", "이름", "이메일", "부서", "직급", "입사일", "상태"};

    public void generateLog() {

    }

    public void generateCsv(List<Employee> employees) {
        // 1. binaryContent 객체 생성
        // 파일 생성
        // 2. column 추가
        // 3. employees 추가
        // 완료되면 return

    }


}
