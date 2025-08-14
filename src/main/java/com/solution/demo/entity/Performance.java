package com.solution.demo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceId;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private LocalDate date;
    private String score;

    public Performance(AppUser appUser, Exercise exercise, String score) {
        this.appUser = appUser;
        this.exercise = exercise;
        this.score = score;
        this.date = LocalDate.now();
    }
}
