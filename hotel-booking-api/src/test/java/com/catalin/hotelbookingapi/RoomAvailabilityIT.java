package com.catalin.hotelbookingapi;

import com.catalin.hotelbookingapi.dto.BookingSaveDTO;
import com.catalin.hotelbookingapi.util.MockRestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Ordering;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RoomAvailabilityIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestController mockRestController;

    @Value("${room.max.availability}")
    private int roomMaxAvailability;

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    @PostConstruct
    private void init() {
        this.mockRestController = new MockRestController(objectMapper, mockMvc);
    }

    @Test
    void availability_GIVEN_nothingIsBooked_WHEN_retrieval_THEN_maximumAvailabilityPeriodIsReturned()
            throws Exception {
        List<LocalDate> availableDates = mockRestController.retrieveRoomAvailableDates();
        assertEquals(roomMaxAvailability, availableDates.size());
        assert (Ordering.natural().isOrdered(availableDates));
        assertEquals(TOMORROW, availableDates.get(0));
        assertEquals(TODAY.plusDays(roomMaxAvailability),
                availableDates.get(roomMaxAvailability - 1));
    }

    @Test
    void availability_GIVEN_lastDayIsBooked_WHEN_retrieval_THEN_maximumAvailabilityPeriodWithoutLastDayIsReturned()
            throws Exception {
        mockRestController.createBooking(
                new BookingSaveDTO(TODAY.plusDays(roomMaxAvailability), 1));
        List<LocalDate> availableDates = mockRestController.retrieveRoomAvailableDates();
        assertEquals(roomMaxAvailability - 1, availableDates.size());
        assert (Ordering.natural().isOrdered(availableDates));
        assertEquals(TOMORROW, availableDates.get(0));
        assertEquals(TODAY.plusDays(roomMaxAvailability - 1),
                availableDates.get(roomMaxAvailability - 2));
    }

    @Test
    void availability_GIVEN_firstDayIsBooked_WHEN_retrieval_THEN_maximumAvailabilityPeriodWithoutFirstDayIsReturned()
            throws Exception {
        mockRestController.createBooking(new BookingSaveDTO(TOMORROW, 1));
        List<LocalDate> availableDates = mockRestController.retrieveRoomAvailableDates();
        assertEquals(roomMaxAvailability - 1, availableDates.size());
        assert (Ordering.natural().isOrdered(availableDates));
        assertEquals(TOMORROW.plusDays(1), availableDates.get(0));
        assertEquals(TODAY.plusDays(roomMaxAvailability),
                availableDates.get(roomMaxAvailability - 2));
    }


    @Test
    void availability_GIVEN_middleDayIsBooked_WHEN_retrieval_THEN_maximumAvailabilityPeriodWithoutMiddleDayIsReturned()
            throws Exception {
        int middleAvailabilityIndex = (roomMaxAvailability - 1) / 2 + 1;
        LocalDate middleDate = TODAY.plusDays(middleAvailabilityIndex);
        mockRestController.createBooking(new BookingSaveDTO(middleDate, 1));
        List<LocalDate> availableDates = mockRestController.retrieveRoomAvailableDates();
        assertEquals(roomMaxAvailability - 1, availableDates.size());
        assert (Ordering.natural().isOrdered(availableDates));
        assertEquals(TOMORROW, availableDates.get(0));
        assertEquals(TODAY.plusDays(roomMaxAvailability),
                availableDates.get(roomMaxAvailability - 2));
        assert (!availableDates.contains(middleDate));
    }
}
