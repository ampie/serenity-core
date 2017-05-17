package net.thucydides.core.statistics.service;


import com.google.common.collect.ImmutableSet;
import net.thucydides.core.requirements.PackageRequirementsTagProvider;
import net.thucydides.core.steps.StepEventBus;

//LITE: import net.thucydides.core.requirements.FileSystemRequirementsTagProvider;


public class JUnitTagProviderStrategy implements TagProviderStrategy {

    @Override
    public boolean canHandleTestSource(String testType) {
        return StepEventBus.TEST_SOURCE_JUNIT.equals(testType);
    }

    @Override
    public Iterable<? extends TagProvider> getTagProviders() {
        return ImmutableSet.of(
                new PackageRequirementsTagProvider(),
                new AnnotationBasedTagProvider(),
//LITE:TODO                new FileSystemRequirementsTagProvider(),
                new FeatureStoryTagProvider(),
                new InjectedTagProvider(),
                new ContextTagProvider()
        );
    }

    @Override
    public boolean hasHighPriority() {
        return false;
    }
}
