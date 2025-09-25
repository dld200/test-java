package org.example.mobile.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Device {

    private String name;

    // ios android
    private String platform;

    private Boolean simulator;

    private String udid;

    private String status;

    private String os;
}
