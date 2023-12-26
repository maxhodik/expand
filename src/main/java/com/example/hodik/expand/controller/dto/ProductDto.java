package com.example.hodik.expand.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductDto {

    @NotBlank(message = "Should not be empty")
    private String table;
    private List<RecordDto> records;

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("table", table)
                .toString();
    }
}