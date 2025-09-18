package org.example.common.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String url;
    
    private String method;
    
    private String headers;

    private String contentType;
    
    private String request;
    
    private String response;

    private long duration;

    private Date createTime;

    private Date updateTime;
}