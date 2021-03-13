package com.asyncworking.exceptions;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorDto {
    private String message;
    private List<String> details;
}
