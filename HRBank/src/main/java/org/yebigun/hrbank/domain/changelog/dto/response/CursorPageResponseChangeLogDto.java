package org.yebigun.hrbank.domain.changelog.dto.response;

import java.util.List;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogDto;

public record CursorPageResponseChangeLogDto(
    List<ChangeLogDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    Boolean hasNext
) {

}
