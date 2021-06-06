package com.karina.KnowYourGender;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Document(collection = "QuestionnaireEntity")
@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
@Getter
public class QuestionnaireEntity {

    @Id
    private final String id;

    private final String avatar;

    private final String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    private final Instant lastModifiedAt;

    private final String description;

    private final String sexQuestion;

    private final String answerUnsure;

    private final Set<QuestionEntity> questions;

    @EqualsAndHashCode
    @RequiredArgsConstructor
    @Getter
    public static class QuestionEntity{

        @EqualsAndHashCode.Include
         private final String id;
         private final String question;
         private final String answerA;
         private final String answerB;
         private final String answerC;
    }


}

