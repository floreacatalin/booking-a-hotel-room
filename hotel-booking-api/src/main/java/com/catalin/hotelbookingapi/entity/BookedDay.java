package com.catalin.hotelbookingapi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

import static com.catalin.hotelbookingapi.entity.BookedDay.DATE_COLUMN_NAME;
import static com.catalin.hotelbookingapi.entity.BookedDay.DATE_UNIQUE_CONSTRAINT_NAME;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {DATE_COLUMN_NAME}, name = DATE_UNIQUE_CONSTRAINT_NAME)
})
public class BookedDay {

    public static final String DATE_UNIQUE_CONSTRAINT_NAME = "DATE_UNIQUE_CONSTRAINT";
    public static final String DATE_COLUMN_NAME = "DATE";

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = DATE_COLUMN_NAME)
    private LocalDate date;

    @ManyToOne
    private Booking booking;

}
