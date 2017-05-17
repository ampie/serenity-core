package net.serenitybdd.screenplay;

import org.junit.runners.model.InitializationError;

public class SerenityRunner extends net.serenitybdd.junit.runners.SerenityRunner {
    public SerenityRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void generateReports() {
        try {
            super.generateReports();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
