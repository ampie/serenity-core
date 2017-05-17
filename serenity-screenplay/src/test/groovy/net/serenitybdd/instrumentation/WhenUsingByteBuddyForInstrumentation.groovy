package net.serenitybdd.instrumentation

import net.serenitybdd.instrumentation.bytebuddy.ByteBuddyInstrumentedObjectFactory
import spock.lang.Specification

import java.lang.reflect.Method

class WhenUsingByteBuddyForInstrumentation extends Specification{

    def "should work"() {
        given:
        def factory = new ByteBuddyInstrumentedObjectFactory();
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
