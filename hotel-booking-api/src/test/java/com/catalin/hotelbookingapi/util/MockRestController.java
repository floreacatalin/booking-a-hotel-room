package com.catalin.hotelbookingapi.util;

import com.catalin.hotelbookingapi.dto.BookingSaveDTO;
import com.catalin.hotelbookingapi.dto.BookingViewDTO;
import com.catalin.hotelbookingapi.dto.RoomAvailabilityDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AllArgsConstructor
public class MockRestController {

    private ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    public BookingViewDTO updateBooking(BookingSaveDTO saveDTO, Long id) throws Exception {
        MvcResult mvcResult = updateBookingAndExpectStatus(saveDTO, id, status().isOk());
        return extractBookingViewDTO(mvcResult);
    }

    public BookingViewDTO cancelBooking(Long id) throws Exception {
        MvcResult mvcResult = cancelBookingAndExpectStatus(id, status().isOk());

        return extractBookingViewDTO(mvcResult);
    }

    public BookingViewDTO findBooking(Long id) throws Exception {
        MvcResult mvcResult = findBookingAndExpectStatus(id, status().isOk());

        return extractBookingViewDTO(mvcResult);
    }

    public BookingViewDTO createBooking(BookingSaveDTO bookingSaveDTO) throws Exception {
        MvcResult mvcResult = createBookingAndExpectStatus(bookingSaveDTO, status().isOk());

        return extractBookingViewDTO(mvcResult);
    }

    public List<LocalDate> retrieveRoomAvailableDates() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/room/available-dates"))
                .andExpect(status().isOk())
                .andReturn();
        RoomAvailabilityDTO dtoResult = extractDto(mvcResult, RoomAvailabilityDTO.class);

        return dtoResult.getAvailableDates();
    }

    public MvcResult findBookingAndExpectStatus(Long id, ResultMatcher expectedStatus) throws Exception {
        return mockMvc.perform(get("/bookings/" + id))
                .andExpect(expectedStatus)
                .andReturn();
    }

    public MvcResult updateBookingAndExpectStatus(BookingSaveDTO saveDTO, Long id,
                                                  ResultMatcher expectedStatus) throws Exception {
        return mockMvc.perform(put("/bookings/" + id)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(saveDTO)))
                .andExpect(expectedStatus)
                .andReturn();
    }

    public MvcResult createBookingAndExpectStatus(BookingSaveDTO saveDTO,
                                                  ResultMatcher expectedStatus) throws Exception {
        return mockMvc.perform(post("/bookings")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(saveDTO)))
                .andExpect(expectedStatus)
                .andReturn();
    }

    public MvcResult cancelBookingAndExpectStatus(Long id, ResultMatcher expectedStatus) throws Exception {
        MvcResult mvcResult = mockMvc.perform(put("/bookings/cancel/" + id))
                .andExpect(status().isOk())
                .andReturn();

        return mvcResult;
    }

    private BookingViewDTO extractBookingViewDTO(MvcResult mvcResult) throws Exception {
        return extractDto(mvcResult, BookingViewDTO.class);
    }

    private <T> T extractDto(MvcResult mvcResult, Class<T> dtoClass) throws Exception {
        return objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), dtoClass);
    }


}
