package br.com.alura.AluraFake.util.validator;

import br.com.alura.AluraFake.task.dto.NewTaskOptionDTO;
import br.com.alura.AluraFake.task.dto.NewTaskSingleChoiceDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

import static br.com.alura.AluraFake.util.ValidationUtils.*;
import static org.springframework.util.ObjectUtils.isEmpty;

public class SingleChoiceValidator implements ConstraintValidator<ValidSingleChoiceTask, NewTaskSingleChoiceDTO> {

    @Override
    public boolean isValid(NewTaskSingleChoiceDTO dto, ConstraintValidatorContext context) {
        if (isEmpty(dto)) return true; // Evita NPE, deixa @NotNull cuidar

        List<NewTaskOptionDTO> options = dto.getOptions();
        String statement = dto.getStatement() != null ? dto.getStatement().trim().toLowerCase() : "";

        // Reset padrão para permitir mensagens múltiplas
        context.disableDefaultConstraintViolation();

        boolean valid = true;

        // Deve haver exatamente uma opção correta
        valid = hasNecessaryQuestions(1, context, options, valid);

        valid = hasRepeatedTasks(context, options, valid);

        // As alternativas não podem ser iguais ao enunciado
        valid = hasTaskEqualsStatement(context, options, statement, valid);

        return valid;
    }
}
