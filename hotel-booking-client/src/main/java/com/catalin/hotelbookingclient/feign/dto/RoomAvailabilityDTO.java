package com.catalin.hotelbookingclient.feign.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RoomAvailabilityDTO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private List<LocalDate> availableDates;

}
