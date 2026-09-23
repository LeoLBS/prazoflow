package br.com.leperber.prazoflow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DiaUtilValidator.class)
public @interface DiaUtilValido {

    String message() default "O prazo deve cair em um dia útil (segunda a sexta)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}