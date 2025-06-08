package org.yebigun.hrbank.global.dto;

import java.util.List;

public record CursorPageResponse<T>(
    List<T> content,
    String nextCursor,
    int size,
    boolean hasNext
) {

}
