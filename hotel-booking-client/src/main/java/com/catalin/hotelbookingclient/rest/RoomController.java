package com.catalin.hotelbookingclient.rest;

import com.catalin.hotelbookingclient.feign.BookingService;
import com.catalin.hotelbookingclient.feign.dto.RoomAvailabilityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final BookingService bookingService;


    @GetMapping("/available-dates")
    public RoomAvailabilityDTO getAvailableDates() {
        return bookingService.retrieveAvailableDates();
    }

}
