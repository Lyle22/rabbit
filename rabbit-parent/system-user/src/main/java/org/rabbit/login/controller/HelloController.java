package org.rabbit.login.controller;

import org.rabbit.SystemAuthConfiguration;
import org.rabbit.common.Result;
import org.rabbit.mail.models.UserParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@PreAuthorize(value = SystemAuthConfiguration.ROLE_USER)
@RequestMapping("${API_VERSION}/hello")
public class HelloController {

    @GetMapping
    public Result<Object> get() {
        return Result.ok("hello every one ");
    }

    @PostMapping("user")
    public ResponseEntity<Object> check(@Valid @RequestBody UserParam userParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                log.error("Invalid Parameter : object - {},field - {},errorMessage - {}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body("invalid parameter");
        }
        return ResponseEntity.ok("success");
    }

}
