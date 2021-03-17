package com.trixpert.beebbeeb.data.to;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelDTO {
    private Long id ;
    private BrandDTO brand ;
    private String name ;
    private String year ;
    private boolean active ;
}
