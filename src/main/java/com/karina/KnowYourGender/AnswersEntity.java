package com.karina.KnowYourGender;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.emptySet;

@Builder
@Document(collection = "AnswersEntity")
@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
@Getter
public class AnswersEntity {

    @Id
    private final String id;
    private final String deviceId;
    private final String questionnaireId;
    private final Sex sex;
    private final Set<AnswerEntity> answers;


    @EqualsAndHashCode
    @RequiredArgsConstructor
    @Getter
    public static class AnswerEntity{
        @EqualsAndHashCode.Include
        private final String questionId;
        @EqualsAndHashCode.Exclude
        private final Answer answer;
        private final boolean isUnsure;

        public enum Answer{
            A,B,C,;
        }

    }
    public enum Sex{
        Male, Female
    }

    public static AnswersEntity ofEmpty(String questionnaireId, String deviceId) {
        return AnswersEntity.builder()
                .id(UUID.randomUUID().toString())
                .questionnaireId(questionnaireId)
                .deviceId(deviceId)
                .answers(emptySet())
                .build();

    }
}
