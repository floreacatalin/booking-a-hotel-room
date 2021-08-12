package com.catalin.hotelbookingapi;

import com.catalin.hotelbookingapi.data.BookedDayRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingCancellationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookedDayRepository bookedDayRepository;

    private MockRestController mockRestController;

    @Value("${booking.max.length}")
    private int maxBookedDays;

    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    @PostConstruct
    private void init() {
        this.mockRestController = new MockRestController(objectMapper, mockMvc);
    }

    @Test
    void booking_GIVEN_bookingIsNotAlreadyCanceled_WHEN_cancelling_THEN_bookingIsCancelledSuccessfully()
            throws Exception {
        BookingSaveDTO saveDTO = new BookingSaveDTO(TOMORROW, maxBookedDays);
        BookingViewDTO viewBeforeCancellation = mockRestController.createBooking(saveDTO);
        BookingViewDTO viewAfterCancellation = mockRestController.cancelBooking(viewBeforeCancellation.getId());
        assertEquals(0, viewAfterCancellation.getNoOfDays());
        assertNull(viewAfterCancellation.getStartDate());
        assertNotNull(viewAfterCancellation.getCancellationDate());
        assertEquals(viewBeforeCancellation.getCreationDate(), viewAfterCancellation.getCreationDate());
        assertNull(viewAfterCancellation.getLastUpdateDate());
        assertEquals(1, bookingRepository.count());
        assertEquals(0, bookedDayRepository.count());
    }

    @Test
    void booking_GIVEN_bookingIsAlreadyCanceled_WHEN_cancelling_THEN_preconditionFailedStatusIsReturned()
            throws Exception {
        BookingSaveDTO saveDTO = new BookingSaveDTO(TOMORROW, maxBookedDays);
        BookingViewDTO viewDTO = mockRestController.createBooking(saveDTO);
        mockRestController.cancelBooking(viewDTO.getId());
        mockRestController.cancelBookingAndExpectStatus(viewDTO.getId(), status().isPreconditionFailed());
    }
}
