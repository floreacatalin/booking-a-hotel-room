package com.catalin.hotelbookingapi;

import com.catalin.hotelbookingapi.data.BookedDayRepository;
import com.catalin.hotelbookingapi.data.BookingRepository;
import com.catalin.hotelbookingapi.dto.BookingSaveDTO;
import com.catalin.hotelbookingapi.dto.BookingViewDTO;
import com.catalin.hotelbookingapi.entity.BookedDay;
import com.catalin.hotelbookingapi.entity.Booking;
import com.catalin.hotelbookingapi.exception.ResourceNotFoundException;
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
public class BookingCreationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookedDayRepository bookedDayRepository;

    private MockRestController mockRestController;

    @Value("${room.max.availability}")
    private int roomMaxAvailability;

    @Value("${booking.max.length}")
    private int maxBookedDays;

    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    @PostConstruct
    private void init() {
        this.mockRestController = new MockRestController(objectMapper, mockMvc);
    }

    @Test
    void booking_GIVEN_allFieldsAreOk_WHEN_saved_THEN_correctEntityAndDtoAreCreated() throws Exception {
        BookingSaveDTO saveDTO = new BookingSaveDTO(TOMORROW, maxBookedDays);

        BookingViewDTO viewDTO = mockRestController.createBooking(saveDTO);
        assertEquals(saveDTO.getNoOfDays(), viewDTO.getNoOfDays());
        assertEquals(saveDTO.getStartDate(), viewDTO.getStartDate());
        assertNotNull(viewDTO.getCreationDate());
        assertNull(viewDTO.getLastUpdateDate());
        assertNull(viewDTO.getCancellationDate());

        checkDatabaseCountsOfBookingsAndBookedDates(1, saveDTO.getNoOfDays());
        Booking dbBooking = bookingRepository.findById(viewDTO.getId()).orElseThrow(ResourceNotFoundException::new);
        assertEquals(viewDTO.getNoOfDays(), dbBooking.getDates().size());
        assertEquals(viewDTO.getStartDate(), dbBooking.getBookedDays()
                .stream()
                .map(BookedDay::getDate)
                .sorted(LocalDate::compareTo)
                .findFirst()
                .orElse(null));
        assertEquals(viewDTO.getCreationDate(), dbBooking.getCreationDate());
        assertNull(dbBooking.getLastUpdateDate());
        assertNull(dbBooking.getCancellationDate());
    }

    @Test
    void booking_GIVEN_datesOverlapWithAnotherBooking_WHEN_saved_THEN_conflictStatusIsReturned()
            throws Exception {
        BookingSaveDTO firstBooking = new BookingSaveDTO(TOMORROW, maxBookedDays);
        mockRestController.createBooking(firstBooking);
        LocalDate conflictingStartDate = firstBooking.getStartDate()
                .plusDays(firstBooking.getNoOfDays() - 1);
        BookingSaveDTO conflictingBooking = new BookingSaveDTO(conflictingStartDate, 1);
        mockRestController.createBookingAndExpectStatus(conflictingBooking, status().isConflict());
        checkDatabaseCountsOfBookingsAndBookedDates(1, firstBooking.getNoOfDays());
    }

    @Test
    void booking_GIVEN_tooManyBookedDays_WHEN_saved_THEN_badRequestStatusIsReturned() throws Exception {
        createBookingAndExpectBadRequestStatus(TOMORROW, maxBookedDays + 1);
    }

    @Test
    void booking_GIVEN_numberOfDaysIsZero_WHEN_saved_THEN_badRequestStatusIsReturned() throws Exception {
        createBookingAndExpectBadRequestStatus(TOMORROW, 0);
    }

    @Test
    void booking_GIVEN_startDateIsNull_WHEN_saved_THEN_badRequestStatusIsReturned() throws Exception {
        createBookingAndExpectBadRequestStatus(null, maxBookedDays);
    }

    @Test
    void booking_GIVEN_startDateIsToday_WHEN_saved_THEN_badRequestStatusIsReturned() throws Exception {
        createBookingAndExpectBadRequestStatus(LocalDate.now(), maxBookedDays);
    }

    @Test
    void booking_GIVEN_lastDayOfBookingIsBeyondAvailabilityPeriod_WHEN_saved_THEN_badRequestStatusIsReturned()
            throws Exception {
        LocalDate earliestInvalidStartDate = LocalDate.now()
                .plusDays(roomMaxAvailability - maxBookedDays + 2);
        createBookingAndExpectBadRequestStatus(earliestInvalidStartDate, maxBookedDays);
    }

    private void createBookingAndExpectBadRequestStatus(LocalDate startDate, int noOfDays) throws Exception {
        BookingSaveDTO bookingSaveDTO = new BookingSaveDTO(startDate, noOfDays);
        mockRestController.createBookingAndExpectStatus(bookingSaveDTO, status().isBadRequest());
        checkDatabaseCountsOfBookingsAndBookedDates(0, 0);
    }

    private void checkDatabaseCountsOfBookingsAndBookedDates(int expectedNoOfBookings,
                                                             int expectedNoOfBookedDates) {
        assertEquals(expectedNoOfBookings, bookingRepository.count());
        assertEquals(expectedNoOfBookedDates, bookedDayRepository.count());
    }

}
