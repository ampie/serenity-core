package net.thucydides.core.steps;

import net.serenitybdd.instrumentation.MethodProxy;

/**
 * Created by john on 9/08/2015.
 */
public class BaseMethodRunner {
    public Object invokeMethod(final Object obj, final Object[] args, final MethodProxy proxy) throws Throwable {
        return proxy.invokeSuper(obj, args);
    }

}
