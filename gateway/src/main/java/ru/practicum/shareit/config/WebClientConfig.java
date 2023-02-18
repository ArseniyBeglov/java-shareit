package ru.practicum.shareit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.user.UserClient;

@Configuration
public class WebClientConfig {

    @Value("${api-prefix-1}")
    String apiPrefix1;

    @Value("${api-prefix-2}")
    String apiPrefix2;

    @Value("${api-prefix-3}")
    String apiPrefix3;

    @Value("${api-prefix-4}")
    String apiPrefix4;


    @Value("${shareit-server.url}")
    String serverUrl;

    @Bean
    public BookingClient bookingClient(RestTemplateBuilder restTemplateBuilder) {
        var restTemplate = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + apiPrefix1))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build();
        return new BookingClient(restTemplate);
    }

    @Bean
    public ItemClient itemClient(RestTemplateBuilder restTemplateBuilder) {
        var restTemplate = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + apiPrefix2))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build();
        return new ItemClient(restTemplate);
    }

    @Bean
    public ItemRequestClient itemRequestClient(RestTemplateBuilder restTemplateBuilder) {
        var restTemplate = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + apiPrefix3))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build();
        return new ItemRequestClient(restTemplate);
    }

    @Bean
    public UserClient userClient(RestTemplateBuilder restTemplateBuilder) {
        var restTemplate = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + apiPrefix4))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build();
        return new UserClient(restTemplate);
    }
}
