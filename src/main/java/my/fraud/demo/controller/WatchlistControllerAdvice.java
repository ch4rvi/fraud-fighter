package my.fraud.demo.controller;

import my.fraud.demo.model.WatchlistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class WatchlistControllerAdvice {


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(httpStatus)
                .body(Map.of(
                        "error", e.getMessage(),
                        "status", String.valueOf(httpStatus.value())
                ));
    }

    @ExceptionHandler(WatchlistException.class)
    public ResponseEntity<Map<String,String>> handleWatchlistException(WatchlistException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(httpStatus)
                .body(Map.of(
                        "error", e.getMessage(),
                        "status", String.valueOf(httpStatus.value())
                ));
    }
}
