package com.moorhuhnservice.moorhuhnservice.controller;

import com.moorhuhnservice.moorhuhnservice.BaseClasses.Question;
import com.moorhuhnservice.moorhuhnservice.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MoorhuhnController {

    @Autowired
    QuestionRepository questionRepository;

    @PostMapping("/save-all-questions")
    public List<Question> saveAllQuestions(@RequestBody List<Question> questions) {
        System.out.println("try to save all "+questions.size()+" questions: ");
        return questions;
    }

    @PostMapping("/save-first-test-question")
    public Question saveFirstTestQuestion() {
        System.out.println("try to save a static question");
        Question questionTest = new Question("xyz", "Frage1","AntwortRichtig","AntwortFalsch1","AntwortFalsch2","AntwortFalsch3","AntwortFalsch4");
        questionRepository.save(questionTest);
        return questionTest;
    }

    @PostMapping("/save-second-sest-question")
    public Question saveSecondTestQuestion() {
        System.out.println("try to save a static question");
        Question questionTest = new Question("xyz", "Frage2","AntwortRichtig","AntwortFalsch1","AntwortFalsch2","AntwortFalsch3","AntwortFalsch4");
        questionRepository.save(questionTest);
        return questionTest;
    }

    @GetMapping("/get-all-questions")
    public List<Question> getAllQuestions() {
        System.out.println("try to get all questions");
        return (List<Question>) questionRepository.findAll();
    }
}