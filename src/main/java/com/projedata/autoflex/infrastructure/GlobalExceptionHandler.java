package com.projedata.autoflex.infrastructure;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ServerExceptionMapper
    public RestResponse<ErrorResponseDto> mapException(IllegalArgumentException ex) {
        return RestResponse.status(
            Response.Status.BAD_REQUEST,
            new ErrorResponseDto(400, "Bad Request", ex.getMessage())
        );
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponseDto> mapException(IllegalStateException ex) {
        return RestResponse.status(
            Response.Status.CONFLICT,
            new ErrorResponseDto(409, "Conflict", ex.getMessage())
        );
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponseDto> mapException(NotFoundException ex) {
        return RestResponse.status(
            Response.Status.NOT_FOUND,
            new ErrorResponseDto(404, "Not Found", "The requested resource was not found.")
        );
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponseDto> mapException(Exception ex) {
        log.error("Unhandled exception caught by GlobalExceptionHandler", ex);
        
        return RestResponse.status(
            Response.Status.INTERNAL_SERVER_ERROR,
            new ErrorResponseDto(500, "Internal Server Error", "An unexpected error occurred on the server.")
        );
    }
}