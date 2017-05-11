package net.thucydides.core.model;


import java.util.List;

public class CucumberScenarioOutlineStepDescriber {
    public static String describeStep(int stepIndex, TestOutcome scenarioOutlineOutcome) {
        List<DataTableRow> dataSet = scenarioOutlineOutcome.getDataTable().getRows();
        String description = scenarioOutlineOutcome.getTestSteps().get(0).getChildren().get(stepIndex).getDescription();
        List<String> headers = scenarioOutlineOutcome.getDataTable().getHeaders();
        for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
            if (occursInAllExamplesOfStep(dataSet, scenarioOutlineOutcome, stepIndex, columnIndex)) {
                description = description.replaceAll(dataSet.get(0).getStringValues().get(columnIndex), "<" + headers.get(columnIndex) + ">");
            }

        }
        return description;
    }

    private static boolean occursInAllExamplesOfStep(List<DataTableRow> dataSet, TestOutcome outline, int stepIndex, int columnIndex) {
        for (int rowIndex = 0; rowIndex < dataSet.size(); rowIndex++) {
            List<String> stringValues = dataSet.get(rowIndex).getStringValues();
            String stepDescriptionForRow = outline.getTestSteps().get(rowIndex).getChildren().get(stepIndex).getDescription();
            String columnValueForRow = stringValues.get(columnIndex);
            if (!stepDescriptionForRow.contains(columnValueForRow)) {
                return false;
            }
        }
        return true;
    }
}
