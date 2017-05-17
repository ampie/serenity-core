package ch.lambdaj.function.convert;

/**
 * Created by ampie on 2017/05/16.
 */
public interface Converter<A, B> {
    B convert(A from);
}
