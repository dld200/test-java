package org.example.common.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Data
@Builder
@DynamicUpdate
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "asset")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    // 分组抢占模式
    private String groupName;

    // 使用者
    private String userName;

    //唯一
    private String uuid;

    @Column(columnDefinition = "json")
    private String info;

    private Date createTime;

    private Date updateTime;
}
