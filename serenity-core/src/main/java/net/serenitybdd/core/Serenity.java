package net.serenitybdd.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.serenitybdd.core.sessions.TestSessionVariables;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.*;
import net.thucydides.core.steps.di.DependencyInjectorService;
import net.thucydides.core.steps.stepdata.StepData;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;

import java.io.File;
import java.util.Arrays;
import java.util.List;

//SITE: Vastly simplified
public class Serenity {
    private static final ThreadLocal<TestSessionVariables> testSessionThreadLocal = new ThreadLocal<TestSessionVariables>();
    private static final ThreadLocal<StepListener> stepListenerThreadLocal = new ThreadLocal<StepListener>();
    private static boolean throwExceptionsImmediately=false;

    public static boolean shouldThrowErrorsImmediately() {
        return false;
    }

    public static Object initialize(Object p) {
        StepEventBus.getEventBus().registerListener(new BaseStepListener(new File("target/site/serenity")));
        return p;
    }
    public static void done() {
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        boolean restartBrowserIfNecessary = false;//LITE:!configuredIn(environmentVariables).restartBrowserForANew(NEVER);

        done(restartBrowserIfNecessary);
    }
    /**
     * Initialize Serenity-related fields in the specified object.
     * This includes managed WebDriver instances,
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static SerenityConfigurer initializeWithNoStepListener(final Object testCase) {
//        setupWebDriverFactory();
//        setupWebdriverManager();

        ThucydidesWebDriverSupport.initialize();
//        ThucydidesWebDriverSupport.initializeFieldsIn(testCase);

//        injectDriverInto(testCase);
//        injectAnnotatedPagesObjectInto(testCase);
        injectScenarioStepsInto(testCase);
        injectDependenciesInto(testCase);

        return new SerenityConfigurer();
    }
    public static class SerenityConfigurer {
        public SerenityConfigurer throwExceptionsImmediately() {
            Serenity.throwExceptionsImmediately();
            return this;
        }
    }

    private static void throwExceptionsImmediately() {
        throwExceptionsImmediately = true;
    }


    public static void done(boolean closeAllDrivers) {
//LITE:        if (closeAllDrivers && getWebdriverManager() != null) {
//            getWebdriverManager().closeAllDrivers();
//        }
        notifyTestFinished();
        resetDependencyInjectors();
    }
    public static SessionMap<Object, Object> getCurrentSession() {

        if (testSessionThreadLocal.get() == null) {
            testSessionThreadLocal.set(new TestSessionVariables());
        }
        return testSessionThreadLocal.get();
    }
    public static void initializeTestSession() {
        getCurrentSession().clear();
    }
    private static void notifyTestFinished() {
        for (StepListener listener : stepListeners()) {
            listener.testRunFinished();
        }
    }
    private static void resetDependencyInjectors() {
        for(DependencyInjector dependencyInjector : getDependencyInjectors()) {
            dependencyInjector.reset();
        }
    }
    private static List<DependencyInjector> getDependencyInjectors() {
        List<DependencyInjector> dependencyInjectors = getDependencyInjectorService().findDependencyInjectors();
        dependencyInjectors.addAll(getDefaultDependencyInjectors());
        return dependencyInjectors;
    }
    private static DependencyInjectorService getDependencyInjectorService() {
        return Injectors.getInjector().getInstance(DependencyInjectorService.class);
    }

    private static List<DependencyInjector> getDefaultDependencyInjectors() {
        return Arrays.<DependencyInjector>asList(new EnvironmentDependencyInjector());
    }
    private static List<StepListener> stepListeners() {
        if (getStepListener() == null) {
            return Lists.newArrayList();
        }
        return Lists.newArrayList(getStepListener());
    }

    public static StepListener getStepListener() {
        return stepListenerThreadLocal.get();
    }



    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static void injectScenarioStepsInto(final Object testCase) {
        StepAnnotations.injectScenarioStepsInto(testCase, getStepFactory());
    }
    public static StepFactory getStepFactory() {
        return StepData.getDefaultStepFactory();
    }
    private static void injectDependenciesInto(Object testCase) {
        for(DependencyInjector dependencyInjector : getDependencyInjectors()) {
            dependencyInjector.injectDependenciesInto(testCase);
        }
    }
}
