package com.github.mikhailstepanov88.ignite_meetup.matcher;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.util.annotation.NonNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class UserMatcher {
    /**
     * Check that the request matched to the create operation.
     *
     * @param request request for check.
     * @return the request matched to the create operation or not.
     */
    public boolean matchCreate(@NonNull ServerRequest request) {
        return POST("/users")
                .and(accept(APPLICATION_JSON_UTF8))
                .and(contentType(APPLICATION_JSON_UTF8))
                .test(request);
    }

    /**
     * Check that the request matched to the read all operation.
     *
     * @param request request for check.
     * @return the request matched to the read all operation or not.
     */
    public boolean matchReadAll(@NonNull ServerRequest request) {
        return GET("/users")
                .and(accept(APPLICATION_JSON_UTF8))
                .and(contentType(APPLICATION_JSON_UTF8))
                .test(request);
    }

    /**
     * Check that the request matched to the read by identifier operation.
     *
     * @param request request for check.
     * @return the request matched to the read by identifier operation or not.
     */
    public boolean matchReadById(@NonNull ServerRequest request) {
        return GET("/users/{userId}")
                .and(accept(APPLICATION_JSON_UTF8))
                .and(contentType(APPLICATION_JSON_UTF8))
                .test(request);
    }

    /**
     * Check that the request matched to the update operation.
     *
     * @param request request for check.
     * @return the request matched to the update operation or not.
     */
    public boolean matchUpdate(@NonNull ServerRequest request) {
        return PUT("/users/{userId}")
                .and(accept(APPLICATION_JSON_UTF8))
                .and(contentType(APPLICATION_JSON_UTF8))
                .test(request);
    }

    /**
     * Check that the request matched to the delete operation.
     *
     * @param request request for check.
     * @return the request matched to the delete operation or not.
     */
    public boolean matchDelete(@NonNull ServerRequest request) {
        return DELETE("/users/{userId}")
                .and(accept(APPLICATION_JSON_UTF8))
                .and(contentType(APPLICATION_JSON_UTF8))
                .test(request);
    }
}