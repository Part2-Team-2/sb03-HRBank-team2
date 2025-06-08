package org.yebigun.hrbank.global.dto;

import java.util.List;

public record CursorPageResponse<T>(
    List<T> content,
    Long nextIdAfter,
    String nextCursor,
    int size,
    long totalElements,
    boolean hasNext
) {

}
