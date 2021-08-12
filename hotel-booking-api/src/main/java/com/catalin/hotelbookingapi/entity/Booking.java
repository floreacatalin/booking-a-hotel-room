package com.catalin.hotelbookingapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    private LocalDateTime lastUpdateDate;

    private LocalDateTime cancellationDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "booking_id")
    private List<BookedDay> bookedDays = new ArrayList<>();
    
    @Version
    private long version;

    @Transient
    public boolean isActive() {
        return this.cancellationDate == null;
    }

    @Transient
    public List<LocalDate> getDates() {
        return this.bookedDays
                .stream()
                .map(BookedDay::getDate)
                .collect(Collectors.toList());
    }

    public void addDate(LocalDate date) {
        BookedDay bookedDay = new BookedDay();
        bookedDay.setBooking(this);
        bookedDay.setDate(date);
        bookedDays.add(bookedDay);
    }

}
