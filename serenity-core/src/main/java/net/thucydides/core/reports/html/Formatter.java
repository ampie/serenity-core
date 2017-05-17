package net.thucydides.core.reports.html;

import com.google.common.collect.Lists;
import net.thucydides.core.issues.IssueTracking;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.join;

//LITE: import static ch.lambdaj.Lambda.join;

//LITE: massively scaled down from original

/**
 * Format text for HTML reports.
 * In particular, this integrates JIRA links into the generated reports.
 */
public class Formatter {

    private final static String ISSUE_NUMBER_REGEXP = "#([A-Z][A-Z0-9-_]*)?-?\\d+";
    private final static Pattern shortIssueNumberPattern = Pattern.compile(ISSUE_NUMBER_REGEXP);
    private final static String FULL_ISSUE_NUMBER_REGEXP = "([A-Z][A-Z0-9-_]*)-\\d+";
    private final static Pattern fullIssueNumberPattern = Pattern.compile(FULL_ISSUE_NUMBER_REGEXP);
    private final static String ISSUE_LINK_FORMAT = "<a target=\"_blank\" href=\"{0}\">{1}</a>";
    private static final String ELIPSE = "&hellip;";
    private static final String ASCIIDOC = "asciidoc";
    private static final String MARKDOWN = "markdown";
    private static final String TEXT = "";
    private static final String NEW_LINE = "\n";

    private final static String NEWLINE_CHAR = "\u2424";
    private final static String NEWLINE = "\u0085";
    private final static String LINE_SEPARATOR = "\u2028";
    private final static String PARAGRAPH_SEPARATOR = "\u2029";
    private static final CharSequenceTranslator ESCAPE_SPECIAL_CHARS = new AggregateTranslator(
            new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE()),
            new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE())
    );

    private final static Logger LOGGER = LoggerFactory.getLogger(Formatter.class);
    private final IssueTracking issueTracking;

    public Formatter(IssueTracking issueTracking) {
        this.issueTracking = issueTracking;
    }


    public String addLinks(final String value) {
        if (issueTracking == null) {
            return value;
        }
        String formattedValue = value;
        if (issueTracking.getIssueTrackerUrl() != null) {
            formattedValue = insertFullIssueTrackingUrls(value);
        }
        if (issueTracking.getShortenedIssueTrackerUrl() != null) {
            formattedValue = insertShortenedIssueTrackingUrls(formattedValue);
        }
        return formattedValue;
    }

    public static List<String> shortenedIssuesIn(String value) {
        IssueExtractor extractor = new IssueExtractor(value);
        return extractor.getShortenedIssues();
    }
    public static List<String> fullIssuesIn(String value) {
        IssueExtractor extractor = new IssueExtractor(value);
        return extractor.getFullIssues();
    }

    private String insertShortenedIssueTrackingUrls(String value) {
        String issueUrlFormat = issueTracking.getShortenedIssueTrackerUrl();
        List<String> issues = sortByDecreasingSize(shortenedIssuesIn(value));
        String formattedValue = replaceWithTokens(value, issues);
        int i = 0;
        for (String issue : issues) {
            String issueUrl = MessageFormat.format(issueUrlFormat, stripLeadingHashFrom(issue));
            String issueLink = MessageFormat.format(ISSUE_LINK_FORMAT, issueUrl, issue);
            String token = "%%%" + i++ + "%%%";
            formattedValue = formattedValue.replaceAll(token, issueLink);
        }
        return formattedValue;
    }

    private List<String> inOrderOfDecreasingLength(List<String> issues) {
        List<String> sortedIssues = Lists.newArrayList(issues);
        Collections.sort(sortedIssues, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });
        return sortedIssues;
    }

    private String stripLeadingHashFrom(final String issue) {
        return issue.substring(1);
    }

    private String replaceWithTokens(String value, List<String> issues) {
        List<String> sortedIssues = inOrderOfDecreasingLength(issues);
        for(int i = 0; i < sortedIssues.size(); i++) {
            value = value.replaceAll(sortedIssues.get(i), "%%%" + i  + "%%%");
        }
        return value;
    }

    private String insertFullIssueTrackingUrls(String value) {
        String issueUrlFormat = issueTracking.getIssueTrackerUrl();
        List<String> issues = sortByDecreasingSize(fullIssuesIn(value));
        String formattedValue = replaceWithTokens(value, issues);
        int i = 0;
        for (String issue : issues) {
            String issueUrl = MessageFormat.format(issueUrlFormat, issue);
            String issueLink = MessageFormat.format(ISSUE_LINK_FORMAT, issueUrl, issue);
            String token = "%%%" + i++ + "%%%";
            formattedValue = formattedValue.replaceAll(token, issueLink);
        }
        return formattedValue;
    }

    private List<String> sortByDecreasingSize(List<String> issues) {
        List<String> sortedIssues = Lists.newArrayList(issues);
        Collections.sort(sortedIssues, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return Integer.valueOf(-a.length()).compareTo(Integer.valueOf(b.length()));
            }
        });
        return sortedIssues;
    }


    public String stripQualifications(String title) {
        if (title == null) {
            return "";
        }
        if (title.contains("[")) {
            return title.substring(0, title.lastIndexOf("[")).trim();
        } else {
            return title;
        }
    }

    static class IssueExtractor {
        private String workingCopy;

        IssueExtractor(String initialValue) {
            this.workingCopy = initialValue;
        }


        public List<String> getShortenedIssues() {
            Matcher matcher = shortIssueNumberPattern.matcher(workingCopy);

            ArrayList<String> issues = Lists.newArrayList();
            while (matcher.find()) {
                String issue = matcher.group();
                issues.add(issue);
                workingCopy = workingCopy.replaceFirst(issue, "");
            }

            return issues;
        }

        public List<String> getFullIssues() {
            Matcher unhashedMatcher = fullIssueNumberPattern.matcher(workingCopy);

            ArrayList<String> issues = Lists.newArrayList();
            while (unhashedMatcher.find()) {
                String issue = unhashedMatcher.group();
                issues.add(issue);
                workingCopy = workingCopy.replaceFirst(issue, "");
            }

            return issues;
        }

    }

    public static List<String> issuesIn(final String value) {

        IssueExtractor extractor = new IssueExtractor(value);

        List<String> issuesWithHash = extractor.getShortenedIssues();
        List<String> allIssues = extractor.getFullIssues();
        allIssues.addAll(issuesWithHash);

        return allIssues;
    }

    public String htmlAttributeCompatible(Object fieldValue) {
        if (fieldValue == null) {
            return "";
        }

        return concatLines(ESCAPE_SPECIAL_CHARS.translate(stringFormOf(fieldValue)
                .replaceAll("<", "(")
                .replaceAll(">", ")")
                .replaceAll("\"", "'")));
    }

    private static String concatLines(String message) {
        return concatLines(message, " ");
    }

    private static String concatLines(String message, String replace) {
        String[] lines = message.replaceAll("\\r", "").split("\\n");
        return join(lines, replace);
    }

    private static String stringFormOf(Object fieldValue) {
        if (Iterable.class.isAssignableFrom(fieldValue.getClass())) {
            return "[" + join((Iterable) fieldValue) + "]";
        } else {
            return fieldValue.toString();
        }
    }
}
