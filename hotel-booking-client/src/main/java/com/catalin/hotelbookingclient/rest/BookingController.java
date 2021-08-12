package com.catalin.hotelbookingclient.rest;

import com.catalin.hotelbookingclient.feign.BookingService;
import com.catalin.hotelbookingclient.feign.dto.BookingSaveDTO;
import com.catalin.hotelbookingclient.feign.dto.BookingViewDTO;
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
    BookingViewDTO updateBooking(@RequestBody @Valid BookingSaveDTO bookingSaveDTO,
                                 @PathVariable Long bookingId) {
        return bookingService.updateBooking(bookingSaveDTO, bookingId);
    }

    @PutMapping("/cancel/{bookingId}")
    BookingViewDTO cancelBooking(@PathVariable Long bookingId) {
        return bookingService.cancelBooking(bookingId);
    }

    @GetMapping("/{bookingId}")
    BookingViewDTO findBookingById(@PathVariable Long bookingId) {
        return bookingService.findBookingById(bookingId);
    }

}
