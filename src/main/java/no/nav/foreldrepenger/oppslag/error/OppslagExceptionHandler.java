package no.nav.foreldrepenger.oppslag.error;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import no.nav.foreldrepenger.boot.conditionals.EnvUtil;
import no.nav.foreldrepenger.oppslag.util.StringUtil;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;

@ControllerAdvice
public class OppslagExceptionHandler extends ResponseEntityExceptionHandler implements EnvironmentAware {

    @Inject
    TokenUtil tokenUtil;

    private Environment env;

    private static final Logger LOG = LoggerFactory.getLogger(OppslagExceptionHandler.class);

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<Object> handleHttpStatusCodeException(HttpStatusCodeException e, WebRequest request) {
        if (e.getStatusCode().equals(UNAUTHORIZED) || e.getStatusCode().equals(FORBIDDEN)) {
            return logAndRespond(e.getStatusCode(), e, request);
        }
        return logAndRespond(e.getStatusCode(), e, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        return logAndRespond(UNPROCESSABLE_ENTITY, e, request, validationErrors(e));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidationException(ConstraintViolationException e, WebRequest req) {
        return logAndRespond(UNPROCESSABLE_ENTITY, e, req);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e, WebRequest req) {
        return logAndRespond(NOT_FOUND, e, req);
    }

    @ExceptionHandler(JwtTokenUnauthorizedException.class)
    public ResponseEntity<Object> handleJwtUnauthorizedException(JwtTokenUnauthorizedException e, WebRequest req) {
        return logAndRespond(UNAUTHORIZED, e, req);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException e, WebRequest req) {
        return logAndRespond(UNAUTHORIZED, e, req, "Token utløper " + e.getExpiryDate());
    }

    @ExceptionHandler(JwtTokenValidatorException.class)
    public ResponseEntity<Object> handleUnauthenticatedOIDCException(JwtTokenValidatorException e, WebRequest req) {
        return logAndRespond(FORBIDDEN, e, req, "Token utløper " + e.getExpiryDate());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Object> handleExpiredToken(TokenExpiredException e, WebRequest req) {
        return logAndRespond(FORBIDDEN, e, req, e.getExpiryDate());
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<Object> handleUnauthenticatedException(UnauthenticatedException e, WebRequest req) {
        return logAndRespond(FORBIDDEN, e, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> catchAll(Exception e, WebRequest req) {
        return logAndRespond(INTERNAL_SERVER_ERROR, e, req);
    }

    private ResponseEntity<Object> logAndRespond(HttpStatus status, Exception e, WebRequest req, Object... messages) {
        return logAndRespond(status, e, req, asList(messages));
    }

    private ResponseEntity<Object> logAndRespond(HttpStatus status, Exception e, WebRequest req,
            List<Object> messages) {

        ApiError apiError = new ApiError(status, e, messages);
        LOG.warn("({}) {} {} ({}, {})", subject(), status, apiError.getMessages(), status.value(),
                tokenUtil.getExpiryDate(), e);
        if (EnvUtil.isDev(env)) {
            LOG.warn("Token {}", tokenUtil.getToken());
        }
        return handleExceptionInternal(e, apiError, new HttpHeaders(), status, req);
    }

    private String subject() {
        return Optional.ofNullable(tokenUtil.getSubject())
                .map(StringUtil::mask)
                .orElse("Uautentisert");
    }

    private static List<String> validationErrors(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors()
                .stream()
                .map(OppslagExceptionHandler::errorMessage)
                .collect(toList());
    }

    private static String errorMessage(FieldError error) {
        return error.getField() + " " + error.getDefaultMessage();
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;

    }

}
