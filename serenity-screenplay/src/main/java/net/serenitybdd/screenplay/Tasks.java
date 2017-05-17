package net.serenitybdd.screenplay;

//LITE:import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.StepFactory;

public class Tasks {

    //LITE:private static Pages pages = new Pages();
    private static StepFactory stepFactory = new StepFactory(/*LITE:pages*/);

    public static <T extends Performable> T instrumented(Class<T> stepClass, Object... parameters) {
        return stepFactory.getUniqueStepLibraryFor(stepClass, parameters);
    }
}
