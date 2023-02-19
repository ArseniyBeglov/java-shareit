package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDtoOutput create(long userId, BookingDtoInput bookingDto) {
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new TimeException("Item is not availibal in this time");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + userId + " is not found"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + bookingDto.getItemId() + " is not found"));

        Booking booking = bookingMapper.fromInputDto(bookingDto, item, user);
        if (booking.getItem().getOwner().getId() == userId) {
            throw new AccessException("Owner can't booking item");
        }
        if (!item.getAvailable()) {
            throw new AvailabilityException("Item is is not available");
        }

        booking.setStatus(Status.WAITING);

        Booking newBooking = bookingRepository.save(booking);
        return bookingMapper.toOutputDto(newBooking);
    }

    @Override
    @Transactional
    public BookingDtoOutput updateStatusOfBooking(long sharerId, long id, boolean approved) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Booking with this id is not found")
        );
        userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " is not found"));

        if (booking.getItem().getOwner().getId() != sharerId) {
            throw new AccessException("Not owner of this item");
        }

        Status status;
        if (approved) {
            status = Status.APPROVED;
        } else {
            status = Status.REJECTED;
        }
        if (booking.getStatus() == status) {
            throw new IllegalArgumentException("new status is equals old status");
        }
        booking.setStatus(status);

        bookingRepository.save(booking);
        return bookingMapper.toOutputDto(booking);
    }

    @Override
    public BookingDtoOutput getById(long userId, long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Booking with this id not found")
        );
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + userId + " not found"));

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.toOutputDto(booking);
        } else {
            throw new AccessException("Not owner or booker of this item");
        }
    }

    @Override
    public List<BookingDtoOutput> getAllByUser(long userId, State state, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with this id is not found");
        }
        if (size <= 0) {
            throw new ValidateException("size is not positive");
        }
        if (from < 0) {
            throw new ValidateException("from is not positive");
        }
        return sortByState(state, userId, "user", from, size).stream().map(bookingMapper::toOutputDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOutput> getAllByOwner(long ownerId, State state, int from, int size) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User with this id is not found");
        }
        if (size <= 0) {
            throw new ValidateException("size is not positive");
        }
        if (from < 0) {
            throw new ValidateException("from is not positive");
        }
        return sortByState(state, ownerId, "owner", from, size).stream().map(bookingMapper::toOutputDto)
                .collect(Collectors.toList());
    }

    private List<Booking> sortByState(State state, long id, String person, int from, int size) {
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, sort);
        if (person.equals("owner")) {
            switch (state) {
                case ALL:
                    bookings = bookingRepository.findBookingsByItem_Owner_Id(id, pageable);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findBookingsByItem_Owner_IdAndStartBeforeAndEndAfter(id,
                            pageable, LocalDateTime.now(), LocalDateTime.now());
                    break;
                case PAST:
                    bookings = bookingRepository.findBookingsByItem_Owner_IdAndEndBefore(id,
                            pageable, LocalDateTime.now());
                    break;
                case FUTURE:
                    bookings = bookingRepository.findBookingsByItem_Owner_IdAndStartAfter(id,
                            pageable, LocalDateTime.now());
                    break;
                case WAITING:
                    bookings = bookingRepository.findBookingsByItem_Owner_IdAndStatus(id,
                            pageable, Status.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findBookingsByItem_Owner_IdAndStatus(id,
                            pageable, Status.REJECTED);
                    break;
                default:
                    throw new UnknownStateException();
            }
        } else {
            switch (state) {
                case ALL:
                    bookings = bookingRepository.findBookingsByBooker_Id(id, pageable);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findBookingsByBooker_IdAndStartBeforeAndEndAfter(id,
                            pageable, LocalDateTime.now(), LocalDateTime.now());
                    break;
                case PAST:
                    bookings = bookingRepository.findBookingsByBooker_IdAndEndBefore(id,
                            pageable, LocalDateTime.now());
                    break;
                case FUTURE:
                    bookings = bookingRepository.findBookingsByBooker_IdAndStartAfter(id,
                            pageable, LocalDateTime.now());
                    break;
                case WAITING:
                    bookings = bookingRepository.findBookingsByBooker_IdAndStatus(id,
                            pageable, Status.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findBookingsByBooker_IdAndStatus(id,
                            pageable, Status.REJECTED);
                    break;
                default:
                    throw new UnknownStateException();
            }
        }
        return bookings;
    }
}