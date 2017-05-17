package net.thucydides.core.webdriver.capabilities;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.hamcrest.Matchers.startsWith;

public class AddCustomCapabilities {
    private final String prefix;
    private EnvironmentVariables environmentVariables;

    public AddCustomCapabilities(String prefix) {
        this.prefix = prefix;
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    public static AddCustomCapabilities startingWith(String prefix) {
        return new AddCustomCapabilities(prefix);
    }

    public AddCustomCapabilities from(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        return this;
    }

    public void to(DesiredCapabilities capabilities) {
        List<String> propertiesWithPrefix = filter(having(on(String.class), startsWith(prefix)),
                environmentVariables.getKeys());

        for(String propertyKey : propertiesWithPrefix) {
            String preparedPropertyKey = getPreparedPropertyKey(propertyKey);
            String propertyValue = environmentVariables.getProperty(propertyKey);
            if (isNotEmpty(propertyValue)) {
                capabilities.setCapability(preparedPropertyKey, propertyValue);
                capabilities.setCapability(propertyKey, propertyValue);
            }
        }
    }

    private String getPreparedPropertyKey(String propertyKey) {
        String shortenedPropertyKey = propertyKey.replace(prefix + ".","");
        if (shortenedPropertyKey.equals("os.version")) {
            return "os_version";
        } else if (shortenedPropertyKey.equals("browser.version")) {
            return "browser_version";
        }
        return shortenedPropertyKey;
    }


}
