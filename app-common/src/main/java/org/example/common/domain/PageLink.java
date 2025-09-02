package org.example.common.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "page_link")
public class PageLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String from;

    private String to;

    //一般是按钮
    private String trigger;

    //边的条件
    private String guard;

    //意图
    private String intent;

    private Date createTime;

    private Date updateTime;
}