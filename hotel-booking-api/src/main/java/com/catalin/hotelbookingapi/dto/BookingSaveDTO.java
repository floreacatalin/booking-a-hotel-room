package com.catalin.hotelbookingapi.dto;

import com.catalin.hotelbookingapi.validator.AfterToday;
import com.catalin.hotelbookingapi.validator.NotAboveMaxNoOfBookedDays;
import com.catalin.hotelbookingapi.validator.WithinTheAvailabilityPeriod;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@WithinTheAvailabilityPeriod
@NoArgsConstructor
public class BookingSaveDTO {

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @AfterToday
    private LocalDate startDate;

    @NotAboveMaxNoOfBookedDays
    private int noOfDays;

    private long version;

    public BookingSaveDTO(LocalDate startDate, int noOfDays) {
        this.startDate = startDate;
        this.noOfDays = noOfDays;
    }

    @JsonIgnore
    public List<LocalDate> getDates() {
        return IntStream.range(0, noOfDays)
                .mapToObj(i -> startDate.plusDays(i)).collect(Collectors.toList());
    }

}
