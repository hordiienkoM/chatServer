package com.hordiienko.myChat.config;

import com.hordiienko.myChat.security.ExceptionMessageHandlerAdvice;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;


@Configuration
public class RSConfig {
    @Bean
    public RSocketStrategies getRSocketStrategies() {
        return RSocketStrategies.builder()
                .encoder(new Jackson2JsonEncoder())
                .decoder(new Jackson2JsonDecoder())
                .routeMatcher(new PathPatternRouteMatcher())
                .build();
    }

    @Bean
    public RSocketMessageHandler messageHandler(RSocketStrategies strategies, Validator validator, ApplicationContext context) {
        RSocketMessageHandler messageHandler = new RSocketMessageHandler();
        messageHandler.getArgumentResolverConfigurer().addCustomResolver(new AuthenticationPrincipalArgumentResolver());
        messageHandler.setRSocketStrategies(strategies);
        messageHandler.setValidator(validator);
        ControllerAdviceBean.findAnnotatedBeans(context)
                .forEach(bean -> messageHandler.registerMessagingAdvice(new ExceptionMessageHandlerAdvice(bean)));
        return messageHandler;
    }
}
