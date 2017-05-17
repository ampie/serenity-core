package net.thucydides.junit.listeners;

//LITE:import com.google.inject.Key;
import net.serenitybdd.junit.runners.ParameterizedJUnitStepListener;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.DataTable;
//LITE:import net.thucydides.core.pages.Pages;
import net.thucydides.core.statistics.AtomicTestCount;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.Listeners;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.util.EnvironmentVariables;
//LITE:import net.thucydides.junit.guice.JUnitInjectors;

import java.io.File;

public class JUnitStepListenerBuilder {
    private final File outputDirectory;
    //LITE:private final Pages pageFactory;
    private final int parameterSetNumber;
    private final DataTable parametersTable;
    private final Class<?> testClass;

//LITE:    public JUnitStepListenerBuilder(File outputDirectory) {
//        this(outputDirectory, null, -1, null);
//    }

    public JUnitStepListenerBuilder(File outputDirectory
                                    /*LITE:Pages pageFactory*/) {
        this(outputDirectory, /*pageFactory,*/ -1, null);
    }

    public JUnitStepListenerBuilder(File outputDirectory,
                                    /*LITE:Pages pageFactory*/
                                    int parameterSetNumber) {
        this(outputDirectory, /*pageFactory,*/ parameterSetNumber, null);
    }

    public JUnitStepListenerBuilder(File outputDirectory,
                                    /*LITE:Pages pageFactory*/
                                    int parameterSetNumber,
                                    DataTable parametersTable) {
        this(outputDirectory, /*pageFactory,*/ parameterSetNumber, parametersTable, null);
    }

    public JUnitStepListenerBuilder(File outputDirectory,
                                    /*LITE:Pages pageFactory*/
                                    int parameterSetNumber,
                                    DataTable parametersTable,
                                    Class<?> testClass) {
        this.outputDirectory = outputDirectory;
//LITE:        this.pageFactory = pageFactory;
        this.parameterSetNumber = parameterSetNumber;
        this.parametersTable = parametersTable;
        this.testClass = testClass;
    }

    public JUnitStepListenerBuilder and() {
        return this;
    }

//LITE:    public JUnitStepListenerBuilder withPageFactory(/*LITE:Pages pageFactory*/) {
//        return new JUnitStepListenerBuilder(outputDirectory, pageFactory);
//    }

    public JUnitStepListenerBuilder withParameterSetNumber(int parameterSetNumber) {
        return new JUnitStepListenerBuilder(outputDirectory, /*pageFactory,*/ parameterSetNumber);
    }

    public JUnitStepListenerBuilder withParametersTable(DataTable parametersTable) {
        return new JUnitStepListenerBuilder(outputDirectory, /*pageFactory,*/ parameterSetNumber, parametersTable);
    }

    public JUnitStepListenerBuilder withTestClass(Class<?> testClass) {
        return new JUnitStepListenerBuilder(outputDirectory, /*pageFactory,*/ parameterSetNumber, parametersTable, testClass);
    }

    public JUnitStepListener build() {
        if (parameterSetNumber >= 0) {
            return newParameterizedJUnitStepListener();
        } else {
            return newStandardJunitStepListener();
        }
    }

    private BaseStepListener buildBaseStepListener() {
//LITE        if (pageFactory != null) {
//            return Listeners.getBaseStepListener()
//                    .withPages(pageFactory)
//                    .and().withOutputDirectory(outputDirectory);
//        } else {
            return Listeners.getBaseStepListener()
                    .withOutputDirectory(outputDirectory);
//LITE:        }
    }

    private JUnitStepListener newParameterizedJUnitStepListener() {
        return new ParameterizedJUnitStepListener(parameterSetNumber,
                parametersTable,
                testClass,
                buildBaseStepListener(),
                Listeners.getLoggingListener(),
                newTestCountListener());
//                Listeners.getStatisticsListener());
    }

    private StepListener newTestCountListener() {
        return new TestCountListener(Injectors.getInjector().getInstance(EnvironmentVariables.class), new AtomicTestCount());
        //LITE:return JUnitInjectors.getInjector().getInstance(Key.get(StepListener.class, TestCounter.class));
    }

    private JUnitStepListener newStandardJunitStepListener() {
        return new JUnitStepListener(testClass,
                buildBaseStepListener(),
                Listeners.getLoggingListener(),
                newTestCountListener());
//                Listeners.getStatisticsListener());
    }

}
