package com.karina.KnowYourGender;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Service
public class ResultService {

    public Result calculate(AnswersEntity answersEntity, QuestionnaireEntity questionnaireEntity){
        GenderToAnswerWeight weight = GenderToAnswerWeight.getWeights(answersEntity.getSex());
        int score = calculateScore(answersEntity.getAnswers(), weight);

        int totalCountOfQuestions = questionnaireEntity.getQuestions().size();
        int countOfAnsweredQuestions = answersEntity.getAnswers().size();

        return new Result(score, totalCountOfQuestions, countOfAnsweredQuestions);
    }

    private int calculateScore(Set<AnswersEntity.AnswerEntity> answers, GenderToAnswerWeight weight) {

        int answerACount = countAnswers(answers, AnswersEntity.AnswerEntity.Answer.A);
        int answerBCount = countAnswers(answers, AnswersEntity.AnswerEntity.Answer.B);
        int answerCCount = countAnswers(answers, AnswersEntity.AnswerEntity.Answer.C);
        int answerUnsureCount = countUnsureAnswers(answers);

        int answerAScore = answerACount * weight.answerAWeight;
        int answerBScore = answerBCount * weight.answerBWeight;
        int answerCScore = answerCCount * weight.answerCWeight;
        int answerUnsureScore = answerUnsureCount * weight.answerUnsureWeight;

        return answerAScore + answerBScore + answerCScore + answerUnsureScore;
    }

    private int countAnswers(Set<AnswersEntity.AnswerEntity> answers, @NotNull AnswersEntity.AnswerEntity.Answer currentAnswer) {
        return (int) answers.stream().map(AnswersEntity.AnswerEntity::getAnswer).filter(currentAnswer::equals).count();
    }

    private int countUnsureAnswers(Set<AnswersEntity.AnswerEntity> answers) {
        return (int) answers.stream().map(AnswersEntity.AnswerEntity::isUnsure).filter(unsure -> unsure).count();
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @RequiredArgsConstructor
    @Getter
    enum GenderToAnswerWeight {
        MaleWeights(15, 5, -5, 5, AnswersEntity.Sex.Male),
        FemaleWeights(10, 5, -5, 5, AnswersEntity.Sex.Female);

        private final int answerAWeight;
        private final int answerBWeight;
        private final int answerCWeight;
        private final int answerUnsureWeight;
        private final AnswersEntity.Sex sex;

        static GenderToAnswerWeight getWeights(AnswersEntity.Sex sex) {
            return Arrays.stream(values()).filter(genderToAnswerWeight -> genderToAnswerWeight.sex.equals(sex))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Sex does not exist"));
        }
    }

@Getter
    static class Result {
        private final int score;
        private final BrainSexScale brainSexScale;
        private final int totalCountOfQuestions;
        private final int countOfAnsweredQuestions;

        public Result(int score, int totalCountOfQuestions, int countOfAnsweredQuestions) {
            this.score = score;
            this.brainSexScale = BrainSexScale.fromScore(score);
            this.totalCountOfQuestions = totalCountOfQuestions;
            this.countOfAnsweredQuestions = countOfAnsweredQuestions;
        }
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @RequiredArgsConstructor
    @Getter
    enum BrainSexScale {
        VeryWoman(300, Integer.MAX_VALUE,"Mózg bardzo kobiecy"),
        Woman(180, 299,"Mózg kobiecy"),
        Common(150, 179,"Strefa wspólna"),
        Man(0, 149, "Mózg męski"),
        VeryMan(Integer.MIN_VALUE, -1, "Mózg bardzo męski");

        private final int min;
        private final int max;
        private final String description;

        public static BrainSexScale fromScore(int score) {
            return Arrays.stream(values()).filter(brainSexScale ->
                score >= brainSexScale.getMin() && score <= brainSexScale.getMax()
            ).findFirst().orElseThrow(() -> new IllegalArgumentException("Min or Max values are wrong!"));
        }
    }
}
