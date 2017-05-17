package net.serenitybdd.instrumentation.cglib;

import net.serenitybdd.instrumentation.MethodProxy;

public class CGLIBMethodProxy implements MethodProxy {
    private net.sf.cglib.proxy.MethodProxy delegate;

    public CGLIBMethodProxy(net.sf.cglib.proxy.MethodProxy methodProxy) {
        this.delegate = methodProxy;
    }

    @Override
    public Object invokeSuper(Object obj, Object[] args) throws Throwable{
        return delegate.invokeSuper(obj, args);
    }
}
