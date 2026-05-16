package my.fraud.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import my.fraud.demo.model.DecisionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class DecisionControllerAdvice {

        @ExceptionHandler(DecisionException.class)
        public void returnMyException(DecisionException e, HttpServletResponse httpResponse) throws Exception {
                httpResponse.sendError(HttpStatus.BAD_REQUEST.value());
                httpResponse.getOutputStream().write(e.getMessage().getBytes());
        }

}
