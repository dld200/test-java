package org.example.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "test_record")
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long testCaseId;

    private String testCaseName;

    private String status; // PASSED, FAILED, RUNNING, PENDING

    private Date startTime;

    private Date endTime;

    private long duration;

    private String errorMessage;

//    @Transient
//    private List<Statement> statements;

    private Date createTime;

    private Date updateTime;

}