package ch.lambdaj;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.hamcrest.Matcher;

import java.util.*;

/**
 * Created by ampie on 2017/05/17.
 */
public class Lambda {
    //LITE:
    public static <T> List<T> sort(Collection<T> in, Comparator<? super T> comp){
        List<T> result = new ArrayList<>(in);
        Collections.sort(result,comp);
        return result;
    }
    public static <T>  List<T> filter(Matcher<? super T> comp, Collection<T> in){
        List<T> result = new ArrayList<>();
        for (T t : in) {
            if(comp.matches(t)){
                result.add(t);
            }
        }
        return result;
    }
    public static <A,B>  List<B> extract(Collection<A> in,Converter<A,B> converter){
        List<B> result = new ArrayList<>();
        for (A t : in) {
            result.add(converter.convert(t));
        }
        return result;
    }
    public static <T extends Comparable> Comparator<? super T> on(Class<?> stringClass) {
        if(stringClass==String.class){
            return new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            };
        }
        return new ComparableComparator();
    }
    public static <A, B> List<B> convert(A[] input, Converter<? super A, B> c) {
        return converEach(Arrays.asList(input), c, new ArrayList<B>());
    }
    public static <A, B> List<B> convert(List<? extends A> input, Converter<? super A, B> c) {
        return converEach(input, c, new ArrayList<B>());
    }
    public static <A, B> Set<B> convert(Set<? extends A> input, Converter<? super A, B> c) {
        return converEach(input, c, new HashSet<B>());
    }
    private static <A, B, C extends Collection<B>> C converEach(Collection<? extends A> input, Converter<A, B> c, C result) {
        for (A a : input) {
            result.add(c.convert(a));
        }
        return result;
    }
}
