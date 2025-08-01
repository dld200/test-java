package org.example.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {

    private Long id;

    private String title;

    private String dsl;

    private Date createTime;

    private Date updateTime;
}