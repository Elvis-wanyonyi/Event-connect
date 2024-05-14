package com.wolfcode.ticketservice.advice;

import com.wolfcode.ticketservice.exception.TicketServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException exception) {
        Map<String, String> errormap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errormap.put(fieldError.getField(), fieldError.getDefaultMessage());

        });
        return errormap;
    }


    @ExceptionHandler(TicketServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleTicketException(TicketServiceException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", exception.getMessage());

        return errorMap;
    }
}
