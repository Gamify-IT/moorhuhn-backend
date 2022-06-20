package com.moorhuhnservice.moorhuhnservice.BaseClasses;


import javax.persistence.*;
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false)
    private String level;
    @Column(nullable = false)
    private String question;
    @Column(nullable = false)
    private String rightAnswer;
    @Column(nullable = false)
    private String wrongAnswerOne;
    @Column(nullable = false)
    private String wrongAnswerTwo;
    @Column(nullable = false)
    private String wrongAnswerThree;
    @Column(nullable = false)
    private String wrongAnswerFour;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question(String level, String question, String rightAnswer, String wrongAnswerOne, String wrongAnswerTwo, String wrongAnswerThree, String wrongAnswerFour) {
        this.level = level;
        this.question = question;
        this.rightAnswer = rightAnswer;
        this.wrongAnswerOne = wrongAnswerOne;
        this.wrongAnswerTwo = wrongAnswerTwo;
        this.wrongAnswerThree = wrongAnswerThree;
        this.wrongAnswerFour = wrongAnswerFour;
    }

    public Question() {

    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public String getWrongAnswerOne() {
        return wrongAnswerOne;
    }

    public void setWrongAnswerOne(String wrongAnswerOne) {
        this.wrongAnswerOne = wrongAnswerOne;
    }

    public String getWrongAnswerTwo() {
        return wrongAnswerTwo;
    }

    public void setWrongAnswerTwo(String wrongAnswerTwo) {
        this.wrongAnswerTwo = wrongAnswerTwo;
    }

    public String getWrongAnswerThree() {
        return wrongAnswerThree;
    }

    public void setWrongAnswerThree(String wrongAnswerThree) {
        this.wrongAnswerThree = wrongAnswerThree;
    }

    public String getWrongAnswerFour() {
        return wrongAnswerFour;
    }

    public void setWrongAnswerFour(String wrongAnswerFour) {
        this.wrongAnswerFour = wrongAnswerFour;
    }
}