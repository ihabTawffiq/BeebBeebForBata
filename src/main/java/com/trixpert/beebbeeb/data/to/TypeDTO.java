package com.trixpert.beebbeeb.data.to;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeDTO {
    private Long id;
    private String name;
    private String logoUrl;
    private String description;
    private boolean active ;
}
