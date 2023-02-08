package ru.practicum.shareit.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.shareit.common.StateMapper;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final StateMapper stateMapper;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stateMapper);
    }
}
