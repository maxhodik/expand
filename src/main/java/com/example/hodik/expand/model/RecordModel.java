package com.example.hodik.expand.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class RecordModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long itemCode;// "11111",
    private LocalDate entryDate; // "03-01-2023",
    private String itemName;// "Test Inventory 1",
    private long itemQuantity; //"20",
    private String status; //"Paid"
}
