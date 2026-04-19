package com.ecom.user.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Address {
    private String id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipcode;
}
