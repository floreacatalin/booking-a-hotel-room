package com.catalin.hotelbookingapi.service;

import com.catalin.hotelbookingapi.data.BookedDayRepository;
import com.catalin.hotelbookingapi.data.BookingRepository;
import com.catalin.hotelbookingapi.dto.RoomAvailabilityDTO;
import com.catalin.hotelbookingapi.dto.BookingSaveDTO;
import com.catalin.hotelbookingapi.dto.BookingViewDTO;
import com.catalin.hotelbookingapi.entity.BookedDay;
import com.catalin.hotelbookingapi.entity.Booking;
import com.catalin.hotelbookingapi.exception.BookedDatesConflictException;
import com.catalin.hotelbookingapi.exception.OperationOnCancelledBookingException;
import com.catalin.hotelbookingapi.exception.OutdatedResourceException;
import com.catalin.hotelbookingapi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookedDayRepository bookedDayRepository;

    @Value("${room.max.availability}")
    private int roomMaxAvailability;

    public BookingViewDTO createBooking(BookingSaveDTO bookingSaveDTO) {
        Booking booking = new Booking();
        booking.setCreationDate(LocalDateTime.now());
        bookingSaveDTO.getDates().forEach(booking::addDate);
        Booking savedBooking = saveBookingAndHandlePotentialDatesConflict(booking);

        return BookingViewDTO.from(savedBooking);
    }


    public BookingViewDTO updateBooking(BookingSaveDTO bookingSaveDTO, Long bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking.getVersion() != bookingSaveDTO.getVersion()) {
            throw new OutdatedResourceException();
        }
        ensureBookingIsActive(booking);
        booking.setLastUpdateDate(LocalDateTime.now());
        List<LocalDate> newDates = bookingSaveDTO.getDates();
        booking.getBookedDays().removeIf(bookedDay -> !newDates.contains(bookedDay.getDate()));
        newDates.stream()
                .filter(newDate -> !booking.getDates().contains(newDate))
                .forEach(booking::addDate);
        Booking savedBooking = saveBookingAndHandlePotentialDatesConflict(booking);

        return BookingViewDTO.from(savedBooking);
    }


    public BookingViewDTO cancelBooking(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        ensureBookingIsActive(booking);
        booking.setCancellationDate(LocalDateTime.now());
        booking.getBookedDays().clear();

        return BookingViewDTO.from(bookingRepository.save(booking));
    }

    public BookingViewDTO findBookingViewDtoById(Long id) {
        return BookingViewDTO.from(findBookingById(id));
    }

    public RoomAvailabilityDTO retrieveAvailableDates() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(roomMaxAvailability);
        List<LocalDate> availableDates = startDate.datesUntil(endDate).collect(Collectors.toList());
        List<BookedDay> bookedDays = bookedDayRepository.findByDateBetween(startDate, endDate);
        bookedDays.forEach(bookedDay -> availableDates.remove(bookedDay.getDate()));

        return new RoomAvailabilityDTO(availableDates);
    }

    private Booking saveBookingAndHandlePotentialDatesConflict(Booking booking) {
        try {
            return bookingRepository.save(booking);
        } catch (DataIntegrityViolationException constraintException) {
            String constraintExceptionMsg = constraintException.getMessage().toUpperCase();
            if (constraintExceptionMsg.contains(BookedDay.DATE_UNIQUE_CONSTRAINT_NAME.toUpperCase())) {
                throw new BookedDatesConflictException();
            }
            throw constraintException;
        }
    }

    private void ensureBookingIsActive(Booking booking) {
        if (!booking.isActive()) throw new OperationOnCancelledBookingException();
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }
}
