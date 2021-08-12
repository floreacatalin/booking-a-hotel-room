package com.catalin.hotelbookingapi;

import com.catalin.hotelbookingapi.data.BookingRepository;
import com.catalin.hotelbookingapi.dto.BookingSaveDTO;
import com.catalin.hotelbookingapi.dto.BookingViewDTO;
import com.catalin.hotelbookingapi.util.MockRestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingRetrievalIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Value("${booking.max.length}")
    private int maxBookedDays;

    private MockRestController mockRestController;

    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    @PostConstruct
    private void init() {
        this.mockRestController = new MockRestController(objectMapper, mockMvc);
    }

    @Test
    void booking_GIVEN_resourceDoesNotExist_WHEN_findById_THEN_resourceNotFoundStatusIsReturned() throws Exception {
        mockRestController.findBookingAndExpectStatus(1L, status().isNotFound());
    }

    @Test
    void booking_GIVEN_resourceExists_WHEN_findById_THEN_resourceIsReturnedCorrectly() throws Exception {
        BookingSaveDTO saveDTO = new BookingSaveDTO(TOMORROW, maxBookedDays);
        BookingViewDTO viewAfterCreateDTO = mockRestController.createBooking(saveDTO);
        BookingViewDTO viewAfterFindDTO = mockRestController.findBooking(viewAfterCreateDTO.getId());
        assertEquals(viewAfterCreateDTO, viewAfterFindDTO);

    }

}
