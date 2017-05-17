package net.thucydides.core.webdriver;

import net.thucydides.core.pages.Pages;
import org.apache.commons.lang3.NotImplementedException;

//LITE:VASTLY REDUCED
public class ThucydidesWebDriverSupport {
    public static void useDefaultDriver(String driverName) {

    }

    public static void initialize() {
    }
    public static Pages getPages(){
        throw new NotImplementedException("getPages does not work on Android");
    }
}
