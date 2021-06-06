package com.karina.KnowYourGender;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswersRepository extends MongoRepository<AnswersEntity, String> {
    List<AnswersEntity> findByDeviceId(String deviceId);
    Optional<AnswersEntity> findByQuestionnaireIdAndDeviceId(String questionnaireId, String deviceId);
    List<AnswersEntity> findAllByQuestionnaireId(String questionnaireId);
}
