package net.thucydides.core.steps;

//LITE:import com.google.inject.Key;
import net.thucydides.core.guice.Injectors;
//LITE:import net.thucydides.core.logging.ThucydidesLogging;
//LITE:import net.thucydides.core.pages.Pages;
//LITE:import net.thucydides.core.statistics.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Listeners {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listeners.class);

    public static BaseStepListenerBuilder getBaseStepListener() {
        return new BaseStepListenerBuilder();    
    }

    public static class BaseStepListenerBuilder {
       //LITE: private Pages pages;

        public BaseStepListenerBuilder and() {
            return this;
        }
        
//LITE:        public BaseStepListenerBuilder withPages(Pages pages) {
//            this.pages = pages;
//            return this;
//        }

        public BaseStepListener withOutputDirectory(File outputDirectory) {
//LITE:            if (pages != null) {
//LITE:                return new BaseStepListener(outputDirectory, pages);
//LITE:            } else {
                return new BaseStepListener(outputDirectory);
//LITE:            }
        }
    }

    public static StepListener getLoggingListener() {
        return Injectors.getInjector().getInstance(ConsoleLoggingListener.class);
//LITE:        return Injectors.getInjector().getInstance(Key.get(StepListener.class, ThucydidesLogging.class));
    }

    public static StepListener getStatisticsListener() {
//LITE:        try {
//            return Injectors.getInjector().getInstance(Key.get(StepListener.class, Statistics.class));
//        } catch (Throwable statisticsListenerException) {
//            LOGGER.error("Failed to create the statistics listener - possible database configuration issue", statisticsListenerException);
//        }
        return null;
    }
}
