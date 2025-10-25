package br.com.alura.AluraFake.util.validator;

import br.com.alura.AluraFake.task.dto.NewTaskMultipleChoiceDTO;
import br.com.alura.AluraFake.task.dto.NewTaskOptionDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

import static br.com.alura.AluraFake.util.ValidationUtils.*;
import static org.springframework.util.ObjectUtils.isEmpty;

public class MultipleChoiceValidator implements ConstraintValidator<ValidMultipleChoiceTask, NewTaskMultipleChoiceDTO> {

    @Override
    public boolean isValid(NewTaskMultipleChoiceDTO dto, ConstraintValidatorContext context) {
        if (isEmpty(dto)) return true;

        List<NewTaskOptionDTO> options = dto.getOptions();
        String statement = dto.getStatement() != null ? dto.getStatement().trim().toLowerCase() : "";

        // Reset padrão para permitir mensagens múltiplas
        context.disableDefaultConstraintViolation();

        boolean valid = true;

        // Deve haver exatamente uma opção correta
        valid = hasNecessaryQuestions(2, context, options, valid);

        // As alternativas não podem ser repetidas
        valid = hasRepeatedTasks(context, options, valid);

        // As alternativas não podem ser iguais ao enunciado
        valid = hasTaskEqualsStatement(context, options, statement, valid);

        return valid;
    }




}
