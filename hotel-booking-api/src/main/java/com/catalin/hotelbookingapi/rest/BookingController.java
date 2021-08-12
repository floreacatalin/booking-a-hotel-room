package com.catalin.hotelbookingapi.rest;

import com.catalin.hotelbookingapi.dto.BookingSaveDTO;
import com.catalin.hotelbookingapi.dto.BookingViewDTO;
import com.catalin.hotelbookingapi.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingViewDTO createBooking(@RequestBody @Valid BookingSaveDTO bookingSaveDTO) {

        return bookingService.createBooking(bookingSaveDTO);
    }

    @PutMapping("/{bookingId}")
    public BookingViewDTO updateBooking(@RequestBody @Valid BookingSaveDTO bookingSaveDTO,
                                        @PathVariable Long bookingId) {

        return bookingService.updateBooking(bookingSaveDTO, bookingId);
    }

    @PutMapping("/cancel/{bookingId}")
    public BookingViewDTO cancelBooking(@PathVariable Long bookingId) {

        return bookingService.cancelBooking(bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingViewDTO findBookingById(@PathVariable Long bookingId) {

        return bookingService.findBookingViewDtoById(bookingId);
    }

}
