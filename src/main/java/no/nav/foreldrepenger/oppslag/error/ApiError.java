package no.nav.foreldrepenger.oppslag.error;

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.oppslag.util.MDCUtil.callId;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableList;

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiError {

    private final HttpStatus status;
    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timestamp;
    @JsonFormat(with = WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
    private final List<String> messages;
    private final String uuid;

    ApiError(HttpStatus status, Throwable t) {
        this(status, t, emptyList());
    }

    ApiError(HttpStatus status, Throwable t, List<Object> objects) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.messages = messages(t, objects);
        this.uuid = callId();
    }

    public String getUuid() {
        return uuid;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getMessages() {
        return messages;
    }

    private static String getRootCauseMessage(Throwable e) {
        return getMostSpecificCause(e).getMessage();
    }

    private static ImmutableList<String> messages(Throwable t, List<Object> objects) {
        var builder = new ImmutableList.Builder<String>();
        String msg = getRootCauseMessage(t);
        if (msg != null) {
            builder.add(msg);
        }
        return builder
                .addAll(objects.stream()
                        .filter(s -> s != null)
                        .map(Object::toString)
                        .collect(toList()))
                .build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[status=" + status + ", timestamp=" + timestamp + ", messages=" + messages
                + ", uuid=" + uuid + "]";
    }
}