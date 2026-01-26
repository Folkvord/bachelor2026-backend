package no.bachelor26.Annotation;

import java.lang.reflect.Field;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// FieldMatchValidator er validatoren til annotasjonen FieldMatch

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object>{

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintValidation){
        this.firstFieldName = constraintValidation.first();
        this.secondFieldName = constraintValidation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context){

        try{
            Field firstField = value.getClass().getDeclaredField(firstFieldName);
            Field secondField = value.getClass().getDeclaredField(secondFieldName);
            firstField.setAccessible(true);
            secondField.setAccessible(true);

            String firstValue = (String) firstField.get(value);
            String secondValue = (String) secondField.get(value);

            return firstValue != null && firstValue.equals(secondValue);

        } catch(NoSuchFieldException | IllegalAccessException e) {
            return false;
        }

    }

}
