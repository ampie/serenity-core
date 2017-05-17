package net.serenitybdd.instrumentation.cglib

import net.serenitybdd.instrumentation.Interceptor
import net.serenitybdd.instrumentation.MethodProxy
import spock.lang.Specification

import java.lang.reflect.Method


class WhenUsingCGLIBForInstrumentation extends Specification{

    def "should work"() {
        given:
        def factory = new CGLIBInstrumentedObjectFactory();
        when:

        def proxy = factory.create(Date.class, new Interceptor() {
            @Override
            Object intercept(Object delegate, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                if(method.getName().equals("toString")){
                    return "Intercepted";
                }else{
                    return methodProxy.invokeSuper(delegate, args)
                }
            }
        },new Long(999999))
        then:
        proxy.toString() == "Intercepted"
        proxy.getTime() == 999999
    }
}
