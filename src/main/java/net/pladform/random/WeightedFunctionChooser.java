package net.pladform.random;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author Dan Barrese
 */
public class WeightedFunctionChooser<T, R> extends ArrayList<WeightedFunction<T, R>> {

    private static final Object LOCK = new Object();
    private final ObjectGenerator _objectGenerator;
    private final AtomicBoolean _probabilitiesVerified;

    public WeightedFunctionChooser(ObjectGenerator g) {
        _objectGenerator = g;
        _probabilitiesVerified = new AtomicBoolean(false);
    }

    public R doRandomFunction(T t) throws Exception {
        if (!_probabilitiesVerified.get()) {
            synchronized (LOCK) {
                if (!_probabilitiesVerified.get()) {
                    Validate.isTrue(this.stream().collect(Collectors.summingDouble(value -> value.probability)) == 1.0);
                    _probabilitiesVerified.set(true);
                }
            }
        }
        double p = _objectGenerator.randomProbability();
        double sum = 0;
        for (WeightedFunction<T, R> pFun : this) {
            if (p > sum && p <= sum + pFun.probability) {
                return pFun.function.apply(t);
            }
            sum += pFun.probability;
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean add(WeightedFunction<T, R> tCallableWithProbability) {
        _probabilitiesVerified.set(false);
        return super.add(tCallableWithProbability);
    }

    @Override
    public void add(int index, WeightedFunction<T, R> element) {
        _probabilitiesVerified.set(false);
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends WeightedFunction<T, R>> c) {
        _probabilitiesVerified.set(false);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends WeightedFunction<T, R>> c) {
        _probabilitiesVerified.set(false);
        return super.addAll(index, c);
    }

    @Override
    public WeightedFunction<T, R> set(int index, WeightedFunction<T, R> element) {
        _probabilitiesVerified.set(false);
        return super.set(index, element);
    }

    @Override
    public WeightedFunction<T, R> remove(int index) {
        _probabilitiesVerified.set(false);
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        _probabilitiesVerified.set(false);
        return super.remove(o);
    }

    @Override
    public void clear() {
        _probabilitiesVerified.set(false);
        super.clear();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        _probabilitiesVerified.set(false);
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        _probabilitiesVerified.set(false);
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        _probabilitiesVerified.set(false);
        return super.retainAll(c);
    }

    @Override
    public ListIterator<WeightedFunction<T, R>> listIterator(int index) {
        _probabilitiesVerified.set(false);
        return super.listIterator(index);
    }

    @Override
    public ListIterator<WeightedFunction<T, R>> listIterator() {
        _probabilitiesVerified.set(false);
        return super.listIterator();
    }

    @Override
    public Iterator<WeightedFunction<T, R>> iterator() {
        _probabilitiesVerified.set(false);
        return super.iterator();
    }

    @Override
    public boolean removeIf(Predicate<? super WeightedFunction<T, R>> filter) {
        _probabilitiesVerified.set(false);
        return super.removeIf(filter);
    }

    @Override
    public void replaceAll(UnaryOperator<WeightedFunction<T, R>> operator) {
        _probabilitiesVerified.set(false);
        super.replaceAll(operator);
    }

    @Override
    public Spliterator<WeightedFunction<T, R>> spliterator() {
        _probabilitiesVerified.set(false);
        return super.spliterator();
    }

}

