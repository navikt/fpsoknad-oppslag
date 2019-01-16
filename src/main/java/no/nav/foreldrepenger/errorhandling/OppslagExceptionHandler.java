package no.nav.foreldrepenger.errorhandling;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.List;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.security.oidc.exceptions.OIDCTokenValidatorException;
import no.nav.security.spring.oidc.validation.interceptor.OIDCUnauthorizedException;

@ControllerAdvice
public class OppslagExceptionHandler extends ResponseEntityExceptionHandler {

    @Inject
    TokenUtil tokenHelper;

    private static final Logger LOG = LoggerFactory.getLogger(OppslagExceptionHandler.class);

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<Object> handleHttpStatusCodeException(HttpStatusCodeException e, WebRequest request) {
        if (e.getStatusCode().equals(UNAUTHORIZED) || e.getStatusCode().equals(FORBIDDEN)) {
            return logAndRespond(e.getStatusCode(), e, request, tokenHelper.getExp());
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

    @ExceptionHandler(IncompleteRequestException.class)
    public ResponseEntity<Object> handleIncompleteRequestException(IncompleteRequestException e, WebRequest req) {
        return logAndRespond(BAD_REQUEST, e, req);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e, WebRequest req) {
        return logAndRespond(NOT_FOUND, e, req);
    }

    @ExceptionHandler(OIDCUnauthorizedException.class)
    public ResponseEntity<Object> handleOIDCUnauthorizedException(OIDCUnauthorizedException e, WebRequest req) {
        return logAndRespond(UNAUTHORIZED, e, req);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException e, WebRequest req) {
        return logAndRespond(UNAUTHORIZED, e, req, "Token utløper " + e.getExpiryDate());
    }

    @ExceptionHandler(OIDCTokenValidatorException.class)
    public ResponseEntity<Object> handleUnauthenticatedOIDCException(OIDCTokenValidatorException e, WebRequest req) {
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
        if (!CollectionUtils.isEmpty(messages)) {
            LOG.warn("{}", messages, e);
        }
        return handleExceptionInternal(e,
                new ApiError(status, e, messages),
                new HttpHeaders(), status, req);
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

}
