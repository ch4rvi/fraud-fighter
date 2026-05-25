package my.fraud.demo.controller;

import my.fraud.demo.model.DecisionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class DecisionControllerAdvice {

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<Map<String, String>> returnNullRequestException(HttpMessageNotReadableException e) {
                HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
                return ResponseEntity
                        .status(httpStatus)
                        .body(Map.of(
                                "error", "K vydání rozhodnutí chybí předmět posouzení!",
                                "status", String.valueOf(httpStatus.value())
                        ));

        }

        @ExceptionHandler(DecisionException.class)
        public ResponseEntity<Map<String,String>> returnMyException(DecisionException e) {
                HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
                return ResponseEntity
                        .status(httpStatus)
                        .body(Map.of(
                                "error", e.getMessage(),
                                "status", String.valueOf(httpStatus.value())
                        ));
        }


}
