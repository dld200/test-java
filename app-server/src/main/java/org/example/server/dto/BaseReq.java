package org.example.server.dto;

import lombok.Data;

@Data
public class BaseReq {
    private int page;

    private int size;

    private String name;
}
