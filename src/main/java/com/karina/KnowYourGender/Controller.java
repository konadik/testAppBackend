package com.karina.KnowYourGender;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/questionnaires")
@RequiredArgsConstructor
public class Controller {

    private final QuestionnaireRepository questionnaireRepository;
    private final AnswersRepository answersRepository;
    private final ResultService resultService;


    @GetMapping
    ResponseEntity<List<QuestionnaireEntity>> getQuestionnaires(){
        return ResponseEntity.ok(this.questionnaireRepository.findAll());
    }

    @PostMapping("/answers")
    ResponseEntity<AnswersEntity> postAnswer(@RequestBody AnswersEntity answersEntity){
       QuestionnaireEntity questionnaireEntity= questionnaireRepository.
               findById(answersEntity.getQuestionnaireId()).orElseThrow(RuntimeException::new);

       List<String> questionIds = questionnaireEntity.getQuestions().stream()
               .map(QuestionnaireEntity.QuestionEntity::getId).collect(Collectors.toList());

       boolean allAnswersMatchQuestionsById = answersEntity.getAnswers().stream()
               .map(AnswersEntity.AnswerEntity::getQuestionId)
               .allMatch(questionIds::contains);

       if(!allAnswersMatchQuestionsById) {
           throw new RuntimeException("Something is wrong with questions!");
       }

        return ResponseEntity.ok(this.answersRepository.save(answersEntity));
    }

    @GetMapping("/answers/{questionnaireId}/{deviceId}")
    ResponseEntity<AnswersEntity> getAnswersForDevice(@PathVariable String questionnaireId,@PathVariable String deviceId) {
        return ResponseEntity.ok(
                this.answersRepository
                        .findByQuestionnaireIdAndDeviceId(questionnaireId, deviceId)
                        .orElseGet(() ->
                                AnswersEntity.ofEmpty(questionnaireId, deviceId))
        );
    }

    @GetMapping("/answers/results/{answersId}")
    ResponseEntity<ResultService.Result> getResultsByAnswersId(@PathVariable String answersId) {
        return ResponseEntity.ok(
                answersRepository.findById(answersId).flatMap(answersEntity ->
                        questionnaireRepository
                                .findById(answersEntity.getQuestionnaireId())
                                .map(questionnaireEntity -> resultService.calculate(answersEntity, questionnaireEntity))
                ).orElseThrow()
        );
    }

    @GetMapping("/results/{questionnaireId}")
    ResponseEntity<List<ResultService.Result>> getAllResultsByQuestionnaireId(@PathVariable String questionnaireId) {

        QuestionnaireEntity questionnaireEntity = questionnaireRepository
                .findById(questionnaireId)
                .orElseThrow();

        return ResponseEntity.ok(
                answersRepository.findAllByQuestionnaireId(questionnaireId).stream()
                        .map(answersEntity -> resultService.calculate(answersEntity, questionnaireEntity))
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/brain-sex-scales")
    ResponseEntity<List<ResultService.BrainSexScale>> getBrainSexScales() {
        return ResponseEntity.ok(
                Arrays.stream(ResultService.BrainSexScale.values())
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/gender-to-answer-weights")
    ResponseEntity<List<ResultService.GenderToAnswerWeight>> getGenderToAnswerWeight() {
        return ResponseEntity.ok(
                Arrays.stream(ResultService.GenderToAnswerWeight.values())
                        .collect(Collectors.toList())
        );
    }
}
