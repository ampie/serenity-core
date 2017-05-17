package net.serenitybdd.instrumentation.cglib;

import net.serenitybdd.instrumentation.Interceptor;
import net.serenitybdd.instrumentation.MethodProxy;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Method;


public class CGLIBInterceptor implements MethodInterceptor {
    private Interceptor delegate;

    public CGLIBInterceptor(Interceptor interceptor) {
        this.delegate=interceptor;
    }

    @Override
    public Object intercept(Object target, Method method, Object[] args, net.sf.cglib.proxy.MethodProxy methodProxy) throws Throwable {
        return this.delegate.intercept(target,method,args, new CGLIBMethodProxy(methodProxy));
    }
}
