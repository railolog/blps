package ru.ifmo.puls;

import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class LimitOffsetPageRequest extends PageRequest {

    private final int offset;

    private LimitOffsetPageRequest(int page, int limit, int offset, Sort sort) {
        super(page, limit, sort);
        this.offset = offset;
    }

    public static LimitOffsetPageRequest of(int limit, int offset) {
        return LimitOffsetPageRequest.of(limit, offset, Sort.unsorted());
    }

    public static LimitOffsetPageRequest of(int limit, int offset, @NonNull Sort sort) {
        int page = limit == 0 ? 0 : offset / limit;
        return new LimitOffsetPageRequest(
                page,
                limit,
                offset,
                sort
        );
    }

    @Override
    public long getOffset() {
        return this.offset;
    }
}