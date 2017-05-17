package net.thucydides.core.steps;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.reflection.MethodFinder;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.thucydides.core.annotations.Fields.FieldValue.UNDEFINED;
import static net.thucydides.core.util.NameConverter.humanize;

/**
 * Test steps and step groups can be described by various annotations.
 */
public final class AnnotatedStepDescription {

    private static final List<String> VALID_STEP_ANNOTATIONS = ImmutableList.of("Step", "Given", "When", "Then");

    private final ExecutedStepDescription description;

    public static AnnotatedStepDescription from(final ExecutedStepDescription description) {
        return new AnnotatedStepDescription(description);

    }

    private AnnotatedStepDescription(final ExecutedStepDescription description) {
        this.description = description;
    }

    public Method getTestMethod() {
        if (getTestClass() != null) {
            return methodCalled(withNoArguments(description.getName()), getTestClass());
        } else {
            return null;
        }
    }

    public Method getTestMethodIfPresent() {
        return findMethodCalled(withNoArguments(description.getName()), getTestClass());
    }

    private String withNoArguments(final String methodName) {
        String unqualifiedName = unqualified(methodName);
        return stripFrom(':', unqualifiedName);
    }

    private String stripFrom(char boundaryChar, String text) {
        int boundaryPosition = text.indexOf(boundaryChar);
        if (boundaryPosition > 0) {
            return text.substring(0, boundaryPosition);
        } else {
            return text;
        }
    }

    private String unqualified(String methodName) {
        return StringUtils.stripStart(methodName,"[0123456789] ");
    }

    private Class<?> getTestClass() {
        return description.getStepClass();
    }

    private Method methodCalled(final String methodName, final Class<?> testClass) {
        Method methodFound = findMethodCalled(methodName, testClass);
        if (methodFound == null) {
            throw new IllegalArgumentException("No test method called " + methodName + " was found in " + testClass);
        }
        return methodFound;
    }

    private Method findMethodCalled(final String methodName, final Class<?> testClass) {
        return MethodFinder.inClass(testClass).getMethodNamed(methodName);
    }

    public String getAnnotatedTitle() {

        Method testMethod = getTestMethod();
        Title title = testMethod.getAnnotation(Title.class);
        if (title != null) {
            return title.value();
        }
        return null;
    }

    private Optional<String> getAnnotatedStepName() {
        Optional<String> stepAnnotatedName = getNameFromStepAnnotationIn(getTestMethod());
        if (stepAnnotatedName.isPresent()) {
            return stepAnnotatedName;
        } else {
            return getCompatibleStepNameFrom(getTestMethod());
        }
    }

    public static boolean isACompatibleStep(Annotation annotation) {
        return VALID_STEP_ANNOTATIONS.contains(annotation.annotationType().getSimpleName());
    }

    private Optional<String> getCompatibleStepNameFrom(Method testMethod) {
        Annotation[] annotations = testMethod.getAnnotations();
        for (Annotation annotation : annotations) {
            if (isACompatibleStep(annotation)) {
                try {
                    String annotationType = annotation.annotationType().getSimpleName();
                    String annotatedValue = (String)annotation.getClass().getMethod("value").invoke(annotation);
                    if (StringUtils.isEmpty(annotatedValue)) {
                        return Optional.absent();
                    } else {
                        return Optional.of(annotationType + " " + StringUtils.uncapitalize(annotatedValue));
                    }

                } catch (Exception ignoredException) {}
            }
        }
        return Optional.absent();
    }

    private Optional<String> getNameFromStepAnnotationIn(final Method testMethod) {
        Step step = testMethod.getAnnotation(Step.class);

        if ((step != null) && (!StringUtils.isEmpty(step.value()))) {
            return Optional.of(injectAnnotatedFieldValuesFrom(description).into(step.value()));
        }
        return Optional.absent();
    }

    private AnnotatedFieldValuesBuilder injectAnnotatedFieldValuesFrom(ExecutedStepDescription description) {
        return new AnnotatedFieldValuesBuilder(description);
    }

    private  class AnnotatedFieldValuesBuilder {
        private final ExecutedStepDescription description;

        private AnnotatedFieldValuesBuilder(ExecutedStepDescription description) {
            this.description = description;
        }

        public String into(String stepDescription) {

            Map<String, Object> fields = description.getDisplayedFields();
            for(String field : fields.keySet()) {
                String fieldName = fieldNameFor(field);
                Object value = fields.get(field);
                if (stepDescription.contains(fieldName) && (value != UNDEFINED)) {
                    stepDescription = StringUtils.replace(stepDescription, fieldNameFor(field), stringValueFor(value));
                }
            }
            //allStepDefinitionFieldsShouldBeResolvedIn(stepDescription);
            return stepDescription;
        }

        private String stringValueFor(Object value) {

            if (value.getClass().isArray()) {
                return Joiner.on(",").join(Arrays.asList(value));
            }
            return value.toString();
        }


        private void allStepDefinitionFieldsShouldBeResolvedIn(String stepDescription) {
            Map<String, Object> fields = description.getDisplayedFields();
            for(String field : fields.keySet()) {
                if (stepDescription.contains(fieldNameFor(field))) {
                    throw new AssertionError(stepDescription +  " : " + field + " value could not be resolved");
                }
            }
        }

    }

    private String fieldNameFor(String field) {
        return "#" + field;
    }

    public String getName() {
        if (noClassIsDefined()) {
            return description.getName();
        } else if (isAGroup()) {
            return groupName();
        } else {
            return stepName();
        }
    }

    private boolean noClassIsDefined() {
        return description.getStepClass() == null;
    }

    private String groupName() {
        String annotatedGroupName = getGroupName();
        if (!StringUtils.isEmpty(annotatedGroupName)) {
            return annotatedGroupName;
        } else {
            return stepName();
        }
    }

    private String stepName() {
        String annotationTitle = getAnnotatedTitle();
        if (!StringUtils.isEmpty(annotationTitle)) {
            return annotationTitle;
        }

        Optional<String> annotatedStepName = getAnnotatedStepName();
        if (getAnnotatedStepName().isPresent() && (StringUtils.isNotEmpty(annotatedStepName.get()))) {
            return annotatedStepNameWithParameters(annotatedStepName.get());
        }

        return humanize(description.getName());
    }


    private String annotatedStepNameWithParameters(String annotatedStepTemplate) {
        String annotatedStepName = annotatedStepTemplate;

        int counter = 0;
        for(String parameter : description.getArguments()) {
            String token = "{" + counter++ + "}";
            annotatedStepName = StringUtils.replace(annotatedStepName, token, parameter);

        }
        return annotatedStepName;
    }

    public boolean isAGroup() {

        Method testMethod = getTestMethodIfPresent();
        if (testMethod != null) {
            StepGroup testGroup = testMethod.getAnnotation(StepGroup.class);
            return (testGroup != null);
        } else {
            return false;
        }
    }

    private String getGroupName() {
        Method testMethod = getTestMethodIfPresent();
        StepGroup testGroup = testMethod.getAnnotation(StepGroup.class);
        return testGroup.value();
    }

    public boolean isPending() {
        Method testMethod = getTestMethodIfPresent();
        return testMethod != null && TestStatus.of(testMethod).isPending();
    }

    public boolean isIgnored() {
        Method testMethod = getTestMethodIfPresent();
        return testMethod != null && TestStatus.of(testMethod).isIgnored();
    }

}
