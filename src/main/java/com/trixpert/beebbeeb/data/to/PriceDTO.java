package com.trixpert.beebbeeb.data.to;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceDTO {
    private Long id;
    private LocalDate date;
    private String price;
    private boolean active;
}
