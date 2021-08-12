package com.catalin.hotelbookingclient.feign.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingSaveDTO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    private int noOfDays;

    private Long version;

}
