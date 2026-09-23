package br.com.leperber.prazoflow.validation;

import br.com.leperber.prazoflow.util.DiaUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DiaUtilValidator implements ConstraintValidator<DiaUtilValido, LocalDate> {

    @Override
    public boolean isValid(LocalDate valor, ConstraintValidatorContext context) {
        if (valor == null) {
            return true;
        }
        return DiaUtil.ehDiaUtil(valor);
    }
}