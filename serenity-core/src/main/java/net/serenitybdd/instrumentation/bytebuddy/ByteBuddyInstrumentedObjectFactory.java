package net.serenitybdd.instrumentation.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.serenitybdd.instrumentation.InstrumentedObjectFactory;
import net.serenitybdd.instrumentation.Interceptor;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;

public class ByteBuddyInstrumentedObjectFactory implements InstrumentedObjectFactory {

    @Override
    public <T> T create(Class<T> type, Interceptor interceptor, Object... parameters) {
        Class<? extends T> instrumentedType = new ByteBuddy()
                .subclass(type, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_PUBLIC)
                .method(isPublic()).intercept(MethodDelegation.to(new ByteBuddyInterceptor(interceptor)))
                .make()
                .load(Thread.currentThread().getContextClassLoader())
                .getLoaded();
        Constructor constructorToUse = findMatchingConstructor(instrumentedType, parameters);
        try {
            return (T) constructorToUse.newInstance(parameters);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Constructor findMatchingConstructor(Class<T> type, Object[] parameters) {
        Map<Class<?>, Class<?>> primiteTypeMap =new HashMap<>();
        primiteTypeMap.put(byte.class, Byte.class);
        primiteTypeMap.put(short.class, Short.class);
        primiteTypeMap.put(int.class, Integer.class);
        primiteTypeMap.put(long.class, Long.class);
        primiteTypeMap.put(float.class, Float.class);
        primiteTypeMap.put(double.class, Double.class);
        primiteTypeMap.put(char.class, Character.class);

        outer:for (Constructor<?> constructor : type.getConstructors()) {
            if (parameters.length == constructor.getParameterTypes().length) {
                for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                    Class<?> parameterType = constructor.getParameterTypes()[i];
                    if(parameterType.isPrimitive()){
                        if(parameters[i]==null){
                            continue outer;
                        }
                        parameterType = primiteTypeMap.get(parameterType);
                    }
                    if (parameters[i]!=null && !parameterType.isInstance(parameters[i])) {
                        continue outer;
                    }
                }
                return constructor;
            }
        }
        return null;
    }
}
