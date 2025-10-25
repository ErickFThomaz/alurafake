package br.com.alura.AluraFake.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MultipleChoiceValidator.class)
@Documented
public @interface ValidMultipleChoiceTask {
    String message() default "Configuração inválida das alternativas";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
