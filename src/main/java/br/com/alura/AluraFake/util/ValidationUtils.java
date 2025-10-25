package br.com.alura.AluraFake.util;

import br.com.alura.AluraFake.task.dto.NewTaskOptionDTO;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ValidationUtils {

    public static boolean hasNecessaryQuestions(long necessaryCorrectQuestions, ConstraintValidatorContext context, List<NewTaskOptionDTO> options, boolean valid) {
        long correctCount = options.stream().filter(NewTaskOptionDTO::isCorrect).count();
        if (correctCount != necessaryCorrectQuestions) {
            context.buildConstraintViolationWithTemplate(
                    "A questão deve ter pelo menos %s alternativas corretas ou mais, mas possui %s".formatted(necessaryCorrectQuestions, correctCount)
            ).addPropertyNode("options").addConstraintViolation();
            valid = false;
        }
        return valid;
    }

    public static boolean hasRepeatedTasks(ConstraintValidatorContext context, List<NewTaskOptionDTO> options, boolean valid) {
        Set<String> unique = new HashSet<>();
        for (NewTaskOptionDTO opt : options) {
            String normalized = opt.getOption().trim().toLowerCase();
            if (!unique.add(normalized)) {
                context.buildConstraintViolationWithTemplate(
                        "As alternativas não podem ser repetidas: '" + opt.getOption() + "'"
                ).addPropertyNode("options").addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }

    public static boolean hasTaskEqualsStatement(ConstraintValidatorContext context, List<NewTaskOptionDTO> options, String statement, boolean valid) {
        for (NewTaskOptionDTO opt : options) {
            if (opt.getOption().trim().equalsIgnoreCase(statement)) {
                context.buildConstraintViolationWithTemplate(
                        "A alternativa '" + opt.getOption() + "' não pode ser igual ao enunciado da questão"
                ).addPropertyNode("options").addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }

}
