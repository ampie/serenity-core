package net.thucydides.core.guice;


import com.google.inject.Injector;
import com.google.inject.Provider;
import net.serenitybdd.core.time.InternalSystemClock;
import net.serenitybdd.core.time.SystemClock;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.SystemVariableBasedBatchManager;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.reports.ExecutorServiceProvider;
import net.thucydides.core.reports.MultithreadExecutorServiceProvider;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.reports.json.gson.GsonJSONConverter;
import net.thucydides.core.statistics.service.ClasspathTagProviderService;
import net.thucydides.core.statistics.service.JUnitTagProviderStrategy;
import net.thucydides.core.statistics.service.TagProviderService;
import net.thucydides.core.statistics.service.TagProviderStrategy;
import net.thucydides.core.steps.ConsoleLoggingListener;
import net.thucydides.core.steps.ConsoleStepListener;
import net.thucydides.core.steps.di.ClasspathDependencyInjectorService;
import net.thucydides.core.steps.di.DependencyInjectorService;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;

import java.util.HashMap;
import java.util.Map;

public class Injectors {
    static Map<Class,Object> instances = new HashMap<>();



    private static Injector injector=new Injector() {
        @Override
        public <T> Provider<T> getProvider(final Class<T> type) {
            return new Provider<T>() {
                @Override
                public T get() {
                    return getInstance(type);
                }
            };
        }

        @Override
        public <T> T getInstance(Class<T> type) {
            return (T)instances.get(type);
        }
    };

    public static Injector getInjector() {
        return injector;
    }

    static {
        try {
            SystemEnvironmentVariables value = new SystemEnvironmentVariables();
            instances.put(EnvironmentVariables.class, value);
            instances.put(DependencyInjectorService.class, new ClasspathDependencyInjectorService());
            instances.put(Configuration.class,new SystemPropertiesConfiguration());
            instances.put(Formatter.class, new Formatter(new SystemPropertiesIssueTracking()));
            instances.put(SystemClock.class, new InternalSystemClock());
            instances.put(TagProviderStrategy.class, new JUnitTagProviderStrategy());
            instances.put(TagProviderService.class, new ClasspathTagProviderService());
            instances.put(BatchManager.class, new SystemVariableBasedBatchManager(value));

            instances.put(ConsoleLoggingListener.class, new ConsoleLoggingListener(value));
            instances.put(ExecutorServiceProvider.class,new MultithreadExecutorServiceProvider(value));
            instances.put(JSONConverter.class, new GsonJSONConverter(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }}
