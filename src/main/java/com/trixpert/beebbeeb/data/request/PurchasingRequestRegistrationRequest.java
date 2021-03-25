package com.trixpert.beebbeeb.data.request;

import com.trixpert.beebbeeb.data.entites.CarInstanceEntity;
import com.trixpert.beebbeeb.data.entites.CustomerEntity;
import com.trixpert.beebbeeb.data.entites.PriceEntity;
import com.trixpert.beebbeeb.data.entites.VendorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchasingRequestRegistrationRequest {

    private long priceId;

    private String status;

    private String payment_type;

    private String comment;

    private Date date;
    private long vendorId;
    private long customerId;
    private long carInstanceId;

    private boolean active;

}
