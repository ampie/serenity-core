package net.thucydides.core.statistics.service;

import com.google.common.collect.ImmutableSet;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.CoreTagProvider;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

import static net.thucydides.core.ThucydidesSystemProperty.THUCYDIDES_ADD_CONTEXT_TAG;

//import net.thucydides.core.guice.Injectors;

/**
 * Allows tags to be added via the injected.tag system property.
 * They will be added to all of the test outcomes for a particular test run.
 */
public class ContextTagProvider implements TagProvider, CoreTagProvider {

    EnvironmentVariables environmentVariables;

    public ContextTagProvider() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public ContextTagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public Set<TestTag> getTagsFor(final TestOutcome testOutcome) {

        if (StringUtils.isEmpty(testOutcome.getContext())) { return new HashSet<>(); }

        if (!THUCYDIDES_ADD_CONTEXT_TAG.booleanFrom(environmentVariables,true)) {
            return new HashSet<>();
        }

        return ImmutableSet.of(TestTag.withName(testOutcome.getContext()).andType("context"));
    }
}
