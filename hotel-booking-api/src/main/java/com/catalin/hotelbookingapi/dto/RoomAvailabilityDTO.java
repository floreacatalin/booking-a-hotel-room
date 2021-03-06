package com.catalin.hotelbookingapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityDTO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private List<LocalDate> availableDates;

}
