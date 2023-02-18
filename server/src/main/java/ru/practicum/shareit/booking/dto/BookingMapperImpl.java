package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoIdAndName;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoIdAndName;

@Component
public class BookingMapperImpl implements BookingMapper {
    @Override
    public BookingDtoOutput toOutputDto(Booking booking) {
        return new BookingDtoOutput(booking.getId(), booking.getStart(), booking.getEnd(),
                new ItemDtoIdAndName(booking.getItem().getId(), booking.getItem().getName()),
                new UserDtoIdAndName(booking.getBooker().getId(), booking.getBooker().getName()),
                booking.getStatus());
    }

    @Override
    public Booking fromInputDto(BookingDtoInput bookingDtoInput, Item item, User user) {
        return new Booking(null, bookingDtoInput.getStart(), bookingDtoInput.getEnd(),
                item, user, Status.WAITING);
    }

    @Override
    public BookingIdAndBookerId toDtoOnlyIdAndBookerId(Booking booking) {
        return new BookingIdAndBookerId(booking.getId(), booking.getBooker().getId());
    }
}
