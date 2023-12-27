package com.example.hodik.expand.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordDto {


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate entryDate; // "03-01-2023",

    @NotNull(message = "Should not be empty")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Should be without fractional part")
    @Range(message = "Should be more then 0")
    private long itemCode;// "11111",

    @NotEmpty(message = "Should not be empty")
    private String itemName;// "Test Inventory 1",

    @NotNull(message = "Should not be empty")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Should be without fractional part")
    @Range(message = "Should be more then 0")
    private long itemQuantity; //"20",

    @NotEmpty(message = "Should not be empty")
    private String status; //"Paid"

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("entryDate", entryDate)
                .append("itemCode", itemCode)
                .append("itemName", itemName)
                .append("itemQuantity", itemQuantity)
                .append("status", status)
                .toString();
    }
}