package com.projedata.autoflex.features.shared;

import java.util.List;

/**
 * A generic DTO class for paginated responses. This class encapsulates the common pagination metadata (total items, total pages, current page) along with the actual data list of type T.
 * @param <T> the type of the data items in the paginated response
 */
public record PaginatedDto<T>(
    List<T> data,
    Integer totalItems,
    Integer totalPages,
    Integer currentPage
) {}
