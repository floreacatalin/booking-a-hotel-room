package com.catalin.hotelbookingclient.feign;


import com.catalin.hotelbookingclient.feign.dto.RoomAvailabilityDTO;
import com.catalin.hotelbookingclient.feign.dto.BookingSaveDTO;
import com.catalin.hotelbookingclient.feign.dto.BookingViewDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@FeignClient(name = "${booking-api.name}")
public interface BookingService {

    @PostMapping("/bookings")
    BookingViewDTO createBooking(@RequestBody @Valid BookingSaveDTO bookingSaveDTO);

    @PutMapping("/bookings/{bookingId}")
    BookingViewDTO updateBooking(@RequestBody @Valid BookingSaveDTO bookingSaveDTO,
                                 @PathVariable Long bookingId);

    @PutMapping("/bookings/cancel/{bookingId}")
    BookingViewDTO cancelBooking(@PathVariable Long bookingId);

    @GetMapping("/bookings/{bookingId}")
    BookingViewDTO findBookingById(@PathVariable Long bookingId);

    @GetMapping("/room/available-dates")
    RoomAvailabilityDTO retrieveAvailableDates();

}
