package org.example.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "statement")
public class Statement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long testRecordId;

    private String statement;

    //只对应一条
    private Long screenshotId;

    //其实是多条
//    private Long networkId;

    @Transient
    private List<Transaction> transactions;

    private String status; // PASSED, FAILED, SKIPPED

    private long duration;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private Date updateTime;
}