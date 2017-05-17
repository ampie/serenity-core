package net.serenitybdd.instrumentation.cglib;

import net.serenitybdd.instrumentation.InstrumentedObjectFactory;
import net.serenitybdd.instrumentation.Interceptor;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class CGLIBInstrumentedObjectFactory implements InstrumentedObjectFactory {

    @Override
    public <T> T create(Class<T> type, Interceptor interceptor, Object... parameters) {
        Enhancer e = new Enhancer();
        e.setSuperclass(type);
        e.setCallback(new CGLIBInterceptor(interceptor));
        Constructor constructorToUse = findMatchingConstructor(type, parameters);
        return (T) e.create(constructorToUse.getParameterTypes(),parameters);
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
                    Class<?> parameterType = constructor.getParameterTypes()[0];
                    if(parameterType.isPrimitive()){
                        parameterType = primiteTypeMap.get(parameterType);
                    }
                    if (!parameterType.isInstance(parameters[i])) {
                        continue outer;
                    }
                }
                return constructor;
            }
        }
        return null;
    }
}
