package net.serenitybdd.instrumentation.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.*;
import net.serenitybdd.instrumentation.Interceptor;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;


public class ByteBuddyInterceptor {
    private Interceptor delegate;

    public ByteBuddyInterceptor(Interceptor interceptor) {
        this.delegate=interceptor;
    }

    @RuntimeType
    public Object intercept(@This Object target, @Origin(cache = true) Method method, @AllArguments Object[] args, @SuperCall Callable<?> methodProxy) throws Throwable {
        return this.delegate.intercept(target,method,args, new ByteBuddyMethodProxy(methodProxy));
    }
}
