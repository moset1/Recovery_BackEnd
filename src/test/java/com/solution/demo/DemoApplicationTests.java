package com.solution.demo;

import com.solution.demo.entity.AppUser;
import com.solution.demo.entity.Exercise;
import com.solution.demo.entity.Performance;
import com.solution.demo.repository.AppUserRepository;
import com.solution.demo.repository.ExerciseRepository;
import com.solution.demo.repository.PerformanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;

@SpringBootTest
class DemoApplicationTests {


    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private PerformanceRepository performanceRepository;
    @Test
    void testJpa() {
//        AppUser u1 = new AppUser();
//        u1.setName("Mose Kim");
//        u1.setPassword("1111");
//        u1.setEmail("kimmose98@naver.com");
//        appUserRepository.save(u1);

//        Exercise e1 = new Exercise();
//        e1.setExerciseName("Chest");
//        e1.setPrecautions("1. 팔을 들어올릴때 어깨가 움직이지 않게 조심하세요");
//        e1.setGuideLink("chest-guide-link");
//        e1.setVideoLink("chest-video-link");
//        exerciseRepository.save(e1);

//        Performance p1 = new Performance();
//        p1.setDate(LocalDate.now());
//        p1.setExercise(exerciseRepository.findByExerciseName("Chest"));
//        p1.setAppUser(u1);
//        p1.setExercise(e1);
//        performanceRepository.save(p1);
    }
}
