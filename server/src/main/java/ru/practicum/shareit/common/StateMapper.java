package ru.practicum.shareit.common;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.State;

@Component
public class StateMapper implements Converter<String, State> {

    @Override
    public State convert(String source) {
        try {
            return State.valueOf(source.toUpperCase());
        } catch (Exception e) {
            return State.UNSUPPORTED_STATUS;
        }
    }
}