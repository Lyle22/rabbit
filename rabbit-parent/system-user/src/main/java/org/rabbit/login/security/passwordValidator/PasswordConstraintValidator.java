package org.rabbit.login.security.passwordValidator;

import org.passay.AllowedRegexRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * PasswordConstraintValidator
 *
 * @author nine rabbit
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private static String passwordAllowedRegexRule;

    @Value("${password.regexp}")
    private void setPasswordAllowedRegexRule(String passwordAllowedRegexRule) {
        PasswordConstraintValidator.passwordAllowedRegexRule = passwordAllowedRegexRule;
    }

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        PasswordValidator validator = new PasswordValidator();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(
                new AllowedRegexRule(PasswordConstraintValidator.passwordAllowedRegexRule)
        );

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        String.join(",", validator.getMessages(result)))
                .addConstraintViolation();
        return false;
    }
}
