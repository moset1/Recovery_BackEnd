package com.solution.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exerciseId;

    private String exerciseName;

    @Column(columnDefinition = "TEXT")
    private String precautions;

    private String videoLink;
    private String guideLink;
}
