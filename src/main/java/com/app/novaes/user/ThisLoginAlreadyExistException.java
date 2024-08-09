package com.app.novaes.user;

import java.net.http.HttpResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.annotation.HttpMethodConstraint;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class ThisLoginAlreadyExistException extends RuntimeException{

}
