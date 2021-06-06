package com.karina.KnowYourGender;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionnaireRepository extends MongoRepository<QuestionnaireEntity, String> {
}
