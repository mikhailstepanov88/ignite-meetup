package com.github.mikhailstepanov88.ignite_meetup.config;

import com.github.mikhailstepanov88.ignite_meetup.handler.Handler;
import com.github.mikhailstepanov88.ignite_meetup.matcher.Matcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.util.annotation.NonNull;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RoutingConfig {
    /**
     * Get function for routing.
     *
     * @param matcher matcher of all operations.
     * @param handler handler of all operations.
     * @return function for routing.
     */
    @Bean
    @NonNull
    public RouterFunction<ServerResponse> routerFunction(@NonNull Matcher matcher,
                                                         @NonNull Handler handler) {
        return route(matcher::matchCreateUser, handler::handleCreateUser)
                .andRoute(matcher::matchUpdateUser, handler::handleUpdateUser)
                .andRoute(matcher::matchDeleteUser, handler::handleDeleteUser)
                .andRoute(matcher::matchReadUserById, handler::handleReadUserById);
    }
}