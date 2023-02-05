package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput create(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @Valid @RequestBody BookingDtoInput bookingDto) {
        log.debug("Request POST to /bookings");
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput updateStatus(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                         @PathVariable  long bookingId,
                                         @RequestParam boolean approved) {
        log.debug("Request PATCH to /bookings/{}", bookingId);
        return bookingService.updateStatusOfBooking(sharerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long bookingId) {
        log.debug("Request GET to /bookings/{}", bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOutput> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "ALL") State state) {
        log.debug("Request GET to /bookings");
        return bookingService.getAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                @RequestParam(defaultValue = "ALL") State state) {
        log.debug("Request GET to /bookings/owner");
        return bookingService.getAllByOwner(ownerId, state);
    }
}