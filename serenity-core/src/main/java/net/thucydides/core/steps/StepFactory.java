package net.thucydides.core.steps;

//SITE:massively scaled down

import com.google.common.collect.ImmutableList;
import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.core.exceptions.StepInitialisationException;
import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.thucydides.core.guice.Injectors;
import net.serenitybdd.instrumentation.InstrumentedObjectFactory;
import net.serenitybdd.instrumentation.Interceptor;
import net.serenitybdd.instrumentation.bytebuddy.ByteBuddyInstrumentedObjectFactory;
import net.thucydides.core.steps.di.DependencyInjectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Produces an instance of a set of requirement steps for use in the acceptance tests.
 * Requirement steps navigate through pages using a WebDriver driver.
 */
public class StepFactory {
    private InstrumentedObjectFactory instrumentedObjectFactory = new ByteBuddyInstrumentedObjectFactory();
    private final Map<Class<?>, Object> index = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(StepFactory.class);
    private DependencyInjectorService dependencyInjectorService;

    public StepFactory() {
        this.dependencyInjectorService = Injectors.getInjector().getInstance(DependencyInjectorService.class);
    }

    public <T> T getUniqueStepLibraryFor(Class<T> scenarioStepsClass, Object ... parameters) {
        return instantiateUniqueStepLibraryFor(scenarioStepsClass, parameters);
    }
    public <T> T getStepLibraryFor(final Class<T> scenarioStepsClass) {
        if (isStepLibraryInstantiatedFor(scenarioStepsClass)) {
            return getManagedStepLibraryFor(scenarioStepsClass);
        } else {
            return getNewStepLibraryFor(scenarioStepsClass);
        }
    }
    private boolean isStepLibraryInstantiatedFor(Class<?> scenarioStepsClass) {
        return index.containsKey(scenarioStepsClass);
    }
    private <T> T getManagedStepLibraryFor(Class<T> scenarioStepsClass) {
        return (T) index.get(scenarioStepsClass);
    }
    public <T> T getNewStepLibraryFor(final Class<T> scenarioStepsClass) {
        try {
            return instantiateNewStepLibraryFor(scenarioStepsClass);
        } catch (RuntimeException stepCreationFailed) {
            throw new StepInitialisationException("Failed to create step library for " + scenarioStepsClass.getSimpleName() + ":" + stepCreationFailed.getMessage(), stepCreationFailed);
        }
    }
    public <T> T instantiateNewStepLibraryFor(Class<T> scenarioStepsClass) {
        StepInterceptor stepInterceptor = new StepInterceptor(scenarioStepsClass);
        return instantiateNewStepLibraryFor(scenarioStepsClass, stepInterceptor);
    }
    public <T> T instantiateNewStepLibraryFor(Class<T> scenarioStepsClass,
                                              Interceptor stepInterceptor) {
        T steps = instrumentedObjectFactory.create(scenarioStepsClass, stepInterceptor);

        indexStepLibrary(scenarioStepsClass, steps);

        instantiateAnyNestedStepLibrariesIn(steps, scenarioStepsClass);

        injectOtherDependenciesInto(steps);

        return steps;
    }
    private <T> void indexStepLibrary(Class<T> scenarioStepsClass, T steps) {
        index.put(scenarioStepsClass, steps);
    }


    private <T> T instantiateUniqueStepLibraryFor(Class<T> scenarioStepsClass, Object... parameters) {
        StepInterceptor stepInterceptor = new StepInterceptor(scenarioStepsClass);
        T steps = instrumentedObjectFactory.create(scenarioStepsClass, stepInterceptor, parameters);

        instantiateAnyNestedStepLibrariesIn(steps, scenarioStepsClass);

        injectOtherDependenciesInto(steps);

        return steps;
    }
    private <T> void instantiateAnyNestedStepLibrariesIn(final T steps,
                                                         final Class<T> scenarioStepsClass) {
        StepAnnotations.injectNestedScenarioStepsInto(steps, this, scenarioStepsClass);
    }
    private <T> void injectOtherDependenciesInto(T steps) {
        List<DependencyInjector> dependencyInjectors = dependencyInjectorService.findDependencyInjectors();
        dependencyInjectors.addAll(getDefaultDependencyInjectors());

        for (DependencyInjector dependencyInjector : dependencyInjectors) {
            dependencyInjector.injectDependenciesInto(steps);
        }
    }
    private ImmutableList<? extends DependencyInjector> getDefaultDependencyInjectors() {
        return ImmutableList.of(new EnvironmentDependencyInjector());
    }
}