package com.catalin.hotelbookingapi.dto;

import com.catalin.hotelbookingapi.entity.BookedDay;
import com.catalin.hotelbookingapi.entity.Booking;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingViewDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    private int noOfDays;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdateDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancellationDate;

    private Long version;

    public static BookingViewDTO from(Booking booking) {
        BookingViewDTO bookingViewDTO = new BookingViewDTO();
        BeanUtils.copyProperties(booking, bookingViewDTO, "startDate", "noOfDays");
        LocalDate startDate = booking.getBookedDays()
                .stream()
                .map(BookedDay::getDate)
                .sorted(LocalDate::compareTo)
                .findFirst()
                .orElse(null);
        bookingViewDTO.setStartDate(startDate);
        bookingViewDTO.setNoOfDays(booking.getBookedDays().size());

        return bookingViewDTO;
    }
}
