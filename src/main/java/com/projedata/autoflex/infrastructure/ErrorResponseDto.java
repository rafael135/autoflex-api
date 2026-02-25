package com.projedata.autoflex.infrastructure;

public record ErrorResponseDto(
    int status,
    String error,
    String message
) {
    
}
