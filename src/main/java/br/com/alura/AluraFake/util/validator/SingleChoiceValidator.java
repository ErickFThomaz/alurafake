package br.com.alura.AluraFake.util.validator;

import br.com.alura.AluraFake.task.dto.NewTaskOptionDTO;
import br.com.alura.AluraFake.task.dto.NewTaskSingleChoiceDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SingleChoiceValidator implements ConstraintValidator<ValidSingleChoiceTask, NewTaskSingleChoiceDTO> {

    @Override
    public boolean isValid(NewTaskSingleChoiceDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getOptions() == null) return true; // Evita NPE, deixa @NotNull cuidar

        List<NewTaskOptionDTO> options = dto.getOptions();
        String statement = dto.getStatement() != null ? dto.getStatement().trim().toLowerCase() : "";

        // Reset padrão para permitir mensagens múltiplas
        context.disableDefaultConstraintViolation();

        boolean valid = true;

        // Deve haver exatamente uma opção correta
        long correctCount = options.stream().filter(NewTaskOptionDTO::isCorrect).count();
        if (correctCount != 1) {
            context.buildConstraintViolationWithTemplate(
                    "A questão deve ter exatamente 1 alternativa correta, mas possui " + correctCount
            ).addPropertyNode("options").addConstraintViolation();
            valid = false;
        }

        // As alternativas não podem ser repetidas
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

        // As alternativas não podem ser iguais ao enunciado
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
