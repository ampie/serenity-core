package net.thucydides.core.model.failures;

import net.serenitybdd.core.exceptions.NoException;
//LITE: import net.thucydides.core.webdriver.WebdriverAssertionError;

public class RootCause {

    public static Class<? extends Throwable> ofException(Throwable testFailureCause) {
        if (testFailureCause == null) { return NoException.class; }

        Class<? extends Throwable> failureCauseClass = testFailureCause.getClass();

        if(/*LITE: WebdriverAssertionError.class.isAssignableFrom(failureCauseClass) && */(testFailureCause.getCause() != null)) {
            failureCauseClass = testFailureCause.getCause().getClass();
        }
        return failureCauseClass;
    }

}
