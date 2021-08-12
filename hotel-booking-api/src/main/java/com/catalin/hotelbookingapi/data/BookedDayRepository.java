package com.catalin.hotelbookingapi.data;

import com.catalin.hotelbookingapi.entity.BookedDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookedDayRepository extends JpaRepository<BookedDay, Long> {

    List<BookedDay> findByDateBetween(LocalDate startDate, LocalDate endDate);

}
