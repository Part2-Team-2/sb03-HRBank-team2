package org.yebigun.hrbank.domain.changelog.dto.data;

import java.util.List;

public record CursorPageResponseChangeLogDto(
    List<ChangeLogDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    int totalElements,
    Boolean hasNext
) {

}
