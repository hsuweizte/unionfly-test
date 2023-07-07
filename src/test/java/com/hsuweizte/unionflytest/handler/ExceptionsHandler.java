package com.hsuweizte.unionflytest.handler;

import com.hsuweizte.unionflytest.exception.TokenErrorExeption;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionsHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler(TokenErrorExeption.class)
    @ResponseBody
    public ResponseEntity<?> tokenNotSame(RuntimeException e) throws JSONException {
        String msg = e.getMessage();
        if (msg == null || "".equals(msg)) {
            msg = "服務器出錯";
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "tokenError");
        jsonObject.put("message", msg);

        ResponseEntity result = new ResponseEntity<Object>(jsonObject, HttpStatus.FORBIDDEN);
        logger.warn("Return ------ {}", result);
        return result;
    }

}
