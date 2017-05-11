package net.thucydides.core.reports.adaptors;

import net.thucydides.core.model.TestOutcome;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * An additional interface in cases where a TestOutcomeAdaptor needs to copy supporting
 * resources to the reporting directory, and that can hold a source context from which
 * resources were imported
 */
public interface ExtendedTestOutcomeAdaptor extends TestOutcomeAdaptor {
    void setSourceContext(String sourceContext);
    void setScenarioStatus(String scenarioStatus);
    void copySupportingResourcesTo(List<TestOutcome> outcomes, File targetDirectory) throws IOException;
}
