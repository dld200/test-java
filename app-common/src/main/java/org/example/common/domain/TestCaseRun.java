package org.example.common.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long testCaseId;

    private String testCaseName;

    private String input;

    private String output;

    private String status;

    @OneToMany(mappedBy = "testCaseRun", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestStepRun> steps;

    private Date startTime;

    private Date endTime;
}
