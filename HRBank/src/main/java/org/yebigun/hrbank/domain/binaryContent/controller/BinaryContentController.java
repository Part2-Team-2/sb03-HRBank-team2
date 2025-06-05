package org.yebigun.hrbank.domain.binaryContent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yebigun.hrbank.domain.binaryContent.service.BinaryContentService;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.controller
 * FileName     : BinaryContentController
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/binaryContent")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;




}
