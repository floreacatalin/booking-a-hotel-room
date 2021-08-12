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
public class BookingUpdateIT {

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
    void booking_GIVEN_newFieldsAreValid_WHEN_updated_THEN_bookingIsCorrectlyUpdated() throws Exception {
        BookingViewDTO originalBooking = createOriginalBooking();
        BookingSaveDTO updateDTO = new BookingSaveDTO(
                originalBooking.getStartDate().plusDays(1),
                originalBooking.getNoOfDays() - 1);
        BookingViewDTO bookingAfterUpdate = mockRestController
                .updateBooking(updateDTO, originalBooking.getId());
        assertEquals(originalBooking.getId(), bookingAfterUpdate.getId());
        assertEquals(updateDTO.getStartDate(), bookingAfterUpdate.getStartDate());
        assertEquals(updateDTO.getNoOfDays(), bookingAfterUpdate.getNoOfDays());
        assertEquals(originalBooking.getVersion() + 1, bookingAfterUpdate.getVersion());
        assertNotNull(bookingAfterUpdate.getCreationDate());
        assertNotNull(bookingAfterUpdate.getLastUpdateDate());
        assertNull(bookingAfterUpdate.getCancellationDate());
        assertEquals(1, bookingRepository.count());
        assertEquals(bookingAfterUpdate.getNoOfDays(), bookedDayRepository.count());
    }

    @Test
    void booking_GIVEN_newDatesOverlapWithAnotherBooking_WHEN_updated_THEN_conflictStatusIsReturned()
            throws Exception {
        BookingViewDTO originalBooking = createOriginalBooking();
        LocalDate otherStartDate = originalBooking.getStartDate().plusDays(originalBooking.getNoOfDays());
        mockRestController.createBooking(new BookingSaveDTO(otherStartDate, 1));
        BookingSaveDTO conflictingBooking = new BookingSaveDTO(
                originalBooking.getStartDate().plusDays(1),
                originalBooking.getNoOfDays());
        mockRestController.updateBookingAndExpectStatus(
                conflictingBooking, originalBooking.getId(), status().isConflict());
        BookingViewDTO bookingAfterConflict = mockRestController.findBooking(originalBooking.getId());
        assertEquals(originalBooking.getNoOfDays(), bookingAfterConflict.getNoOfDays());
        assertEquals(originalBooking.getVersion(), bookingAfterConflict.getVersion());
        assertEquals(originalBooking.getStartDate(), bookingAfterConflict.getStartDate());
        assertEquals(originalBooking.getLastUpdateDate(), bookingAfterConflict.getLastUpdateDate());
    }

    @Test
    void booking_GIVEN_bookingIsCanceled_WHEN_updated_THEN_preconditionFailedStatusIsReturned()
            throws Exception {
        BookingViewDTO originalBooking = createOriginalBooking();
        BookingViewDTO canceledBooking = mockRestController.cancelBooking(originalBooking.getId());
        BookingSaveDTO updateDTO = new BookingSaveDTO(TOMORROW, 1);
        updateDTO.setVersion(canceledBooking.getVersion());
        mockRestController.updateBookingAndExpectStatus(
                updateDTO, originalBooking.getId(), status().isPreconditionFailed());
        BookingViewDTO bookingAfterUpdate = mockRestController.findBooking(originalBooking.getId());
        assertEquals(originalBooking.getVersion() + 1, bookingAfterUpdate.getVersion());
        assertNull(bookingAfterUpdate.getStartDate());
        assertEquals(0, bookingAfterUpdate.getNoOfDays());
        assertEquals(originalBooking.getCreationDate(), bookingAfterUpdate.getCreationDate());
        assertNotNull(bookingAfterUpdate.getCancellationDate());
        assertEquals(originalBooking.getLastUpdateDate(), bookingAfterUpdate.getLastUpdateDate());
    }

    @Test
    void booking_GIVEN_versionIsOutdated_WHEN_updated_THEN_preconditionFailedStatusIsReturned()
            throws Exception {
        BookingViewDTO originalBooking = createOriginalBooking();
        BookingSaveDTO updateDTO = new BookingSaveDTO(
                originalBooking.getStartDate().plusDays(1),
                originalBooking.getNoOfDays());
        mockRestController.updateBooking(updateDTO, originalBooking.getId());
        updateDTO.setStartDate(updateDTO.getStartDate().plusDays(1));
        updateDTO.setVersion(0);
        mockRestController.updateBookingAndExpectStatus(
                updateDTO, originalBooking.getId(), status().isConflict());
    }

    @Test
    void booking_GIVEN_tooManyBookedDays_WHEN_updated_THEN_badRequestStatusIsReturned() throws Exception {
        updateOriginalBookingAndExpectBadRequestStatus(TOMORROW, maxBookedDays + 1);
    }

    @Test
    void booking_GIVEN_numberOfDaysIsZero_WHEN_updated_THEN_badRequestStatusIsReturned() throws Exception {
        updateOriginalBookingAndExpectBadRequestStatus(TOMORROW, 0);
    }

    @Test
    void booking_GIVEN_startDateIsNull_WHEN_updated_THEN_badRequestStatusIsReturned() throws Exception {
        updateOriginalBookingAndExpectBadRequestStatus(null, maxBookedDays + 1);
    }

    @Test
    void booking_GIVEN_startDateIsToday_WHEN_updated_THEN_badRequestStatusIsReturned() throws Exception {
        updateOriginalBookingAndExpectBadRequestStatus(LocalDate.now(), maxBookedDays);
    }

    @Test
    void booking_GIVEN_lastDayOfBookingIsBeyondAvailabilityPeriod_WHEN_updated_THEN_badRequestStatusIsReturned()
            throws Exception {
        LocalDate earliestInvalidStartDate = LocalDate.now().plusDays(roomMaxAvailability - maxBookedDays + 2);
        updateOriginalBookingAndExpectBadRequestStatus(earliestInvalidStartDate, maxBookedDays);
    }

    private void updateOriginalBookingAndExpectBadRequestStatus(LocalDate newStartDate,
                                                                int newNoOfDays) throws Exception {
        BookingViewDTO originalBooking = createOriginalBooking();
        BookingSaveDTO updateDTO = new BookingSaveDTO(newStartDate, newNoOfDays);
        mockRestController.updateBookingAndExpectStatus(
                updateDTO, originalBooking.getId(), status().isBadRequest());
        BookingViewDTO bookingAfterUpdate = mockRestController.findBooking(originalBooking.getId());
        assertEquals(originalBooking, bookingAfterUpdate);
    }

    private BookingViewDTO createOriginalBooking() throws Exception {
        return mockRestController.createBooking(new BookingSaveDTO(TOMORROW, maxBookedDays));
    }

}
