package com.moorhuhnservice.moorhuhnservice.repositories;

import com.moorhuhnservice.moorhuhnservice.BaseClasses.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>{
    List<Question> findAllByConfiguration(String configuration);
    Question deleteByQuestion(String question);
    Question findByQuestion(String question);
}
