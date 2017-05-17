package net.serenitybdd.instrumentation.bytebuddy;

import net.serenitybdd.instrumentation.MethodProxy;

import java.util.concurrent.Callable;


public class ByteBuddyMethodProxy implements MethodProxy {
    private Callable<?> delegate;

    public ByteBuddyMethodProxy(Callable<?> methodProxy) {
        this.delegate = methodProxy;
    }

    @Override
    public Object invokeSuper(Object obj, Object[] args) throws Throwable {
        return delegate.call();
    }
}
