package net.thucydides.core.steps;

import com.google.common.base.Optional;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;

import java.util.List;

//LITE:
public interface BaseStepListenerContract extends StepListener {
    void setEventBus(StepEventBus stepEventBus);
    StepEventBus getEventBus();

    boolean testSuiteRunning();

    void currentStepIsAPrecondition();

    StepMutator updateCurrentStepTitle(String stepTitle);

    void cancelPreviousTest();

    void lastTestPassedAfterRetries(int remainingTries, List<String> failureMessages, TestFailureCause testFailureCause);

    void addIssuesToCurrentStory(List<String> issues);

    void setBackgroundDescription(String description);

    void setBackgroundTitle(String title);

    void addDescriptionToCurrentTest(String description);

    void addIssuesToCurrentTest(List<String> issues);

    void addTagsToCurrentTest(List<TestTag> tags);

    void addTagsToCurrentStory(List<TestTag> tags);

    boolean currentTestOutcomeIsDataDriven();

    void takeScreenshot();

    void exceptionExpected(Class<? extends Throwable> expected);

    Optional<TestResult> resultSoFar();

    StepMerger mergeLast(int i);

    void updateOverallResults();
    
    Optional<TestResult> getForcedResult();
    
    void setAllStepsTo(TestResult result);

    boolean aStepHasFailed();

    boolean aStepHasFailedInTheCurrentExample();

    int getRunningStepCount();

    
    class StepMutator {

        private final BaseStepListenerContract baseStepListener;

        public StepMutator(BaseStepListenerContract baseStepListener) {
            this.baseStepListener = baseStepListener;
        }

        public void asAPrecondition() {
            //LITE:baseStepListener.getCurrentStep().setPrecondition(true);
        }

    }

     class StepMerger {

        final int maxStepsToMerge;

        public StepMerger(int maxStepsToMerge) {
            this.maxStepsToMerge = maxStepsToMerge;
        }

        public void steps() {

            //LITE:getCurrentTestOutcome().mergeMostRecentSteps(maxStepsToMerge);
        }

    }
}
