package net.serenitybdd.junit.runners;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
//LITE:import com.google.inject.Module;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.webdriver.Configuration;
//LITE:import net.thucydides.core.webdriver.WebDriverFactory;
//LITE:import net.thucydides.core.webdriver.WebdriverManager;
import org.junit.runners.model.InitializationError;

import java.util.List;

/**
 * User: YamStranger
 * Date: 3/9/16
 * Time: 4:28 PM
 */
abstract class QualifiedTestsRunner extends SerenityRunner {
    private String qualifier;
    private Object test;

    public QualifiedTestsRunner(Class<?> klass, String qualifier, Object test) throws InitializationError {
        super(klass);
        this.qualifier = qualifier;
        this.test = test;
    }

//LITE:    public QualifiedTestsRunner(Class<?> klass, Module module, String qualifier, Object test) throws InitializationError {
//        super(klass, module);
//        this.qualifier = qualifier;
//        this.test = test;
//    }
//
//    public QualifiedTestsRunner(Class<?> klass, Injector injector, String qualifier, Object test) throws InitializationError {
//        super(klass, injector);
//        this.qualifier = qualifier;
//        this.test = test;
//    }
//
//    public QualifiedTestsRunner(Class<?> klass, WebDriverFactory webDriverFactory, String qualifier, Object test) throws InitializationError {
//        super(klass, webDriverFactory);
//        this.qualifier = qualifier;
//        this.test = test;
//    }
//
//    public QualifiedTestsRunner(Class<?> klass, WebDriverFactory webDriverFactory, Configuration configuration, String qualifier, Object test) throws InitializationError {
//        super(klass, webDriverFactory, configuration);
//        this.qualifier = qualifier;
//        this.test = test;
//    }
//
//    public QualifiedTestsRunner(Class<?> klass, WebDriverFactory webDriverFactory, Configuration configuration, BatchManager batchManager, String qualifier, Object test) throws InitializationError {
//        super(klass, webDriverFactory, configuration, batchManager);
//        this.qualifier = qualifier;
//        this.test = test;
//    }
//
//    public QualifiedTestsRunner(Class<?> klass, BatchManager batchManager, String qualifier, Object test) throws InitializationError {
//        super(klass, batchManager);
//        this.qualifier = qualifier;
//        this.test = test;
//    }
//
//    public QualifiedTestsRunner(Class<?> klass, WebdriverManager webDriverManager, Configuration configuration, BatchManager batchManager, String qualifier, Object test) throws InitializationError {
//        super(klass, webDriverManager, configuration, batchManager);
//        this.qualifier = qualifier;
//        this.test = test;
//    }
//
//    public QualifiedTestsRunner(final Class<?> type,
//                                final Configuration configuration,
//                                final WebDriverFactory webDriverFactory,
//                                final BatchManager batchManager) throws InitializationError {
//        super(type, webDriverFactory, configuration, batchManager);
//    }
//
    @Override
    public final Object createTest() throws Exception {
        this.test = initializeTest();
        return this.test;
    }

    abstract protected Object initializeTest() throws Exception;

    @Override
    public final void useQualifier(final String qualifier) {
        this.qualifier = qualifier;
        super.useQualifier(qualifier);
    }

    @Override
    public final List<TestOutcome> getTestOutcomes() {
        return enhance(qualified(super.getTestOutcomes()));
    }

    public List<TestOutcome> enhance(List<TestOutcome> outcomes) {
        return outcomes;
    }

    private List<TestOutcome> qualified(List<TestOutcome> testOutcomes) {
        List<TestOutcome> qualifiedOutcomes = Lists.newArrayList();

        if (this.test != null) {
            useQualifier(QualifierFinder.forTestCase(test).getQualifier());
        }

        for (TestOutcome outcome : testOutcomes) {
            qualifiedOutcomes.add(outcome.withQualifier(qualifier));
        }
        return qualifiedOutcomes;
    }
}
