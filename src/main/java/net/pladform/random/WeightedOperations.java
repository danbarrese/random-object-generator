package net.pladform.random;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author Dan Barrese
 */
public class WeightedOperations<T> extends ArrayList<WeightedCallable<T>> {
    
    private ObjectGenerator _objectGenerator;
    private boolean _probabilitiesVerified;

    public WeightedOperations(ObjectGenerator g) {
        _objectGenerator = g;
        _probabilitiesVerified = false;
    }

    public T doRandomOperation() throws Exception {
        if (!_probabilitiesVerified) {
            Validate.isTrue(this.stream().collect(Collectors.summingDouble(value -> value.probability)) == 1.0);
            _probabilitiesVerified = true;
        }
        double p = _objectGenerator.randomProbability();
        double sum = 0;
        for (WeightedCallable<T> c : this) {
            if (p > sum && p <= sum + c.probability) {
                return c.callable.call();
            }
            sum += c.probability;
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean add(WeightedCallable<T> tCallableWithProbability) {
        _probabilitiesVerified = false;
        return super.add(tCallableWithProbability);
    }

    @Override
    public void add(int index, WeightedCallable<T> element) {
        _probabilitiesVerified = false;
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends WeightedCallable<T>> c) {
        _probabilitiesVerified = false;
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends WeightedCallable<T>> c) {
        _probabilitiesVerified = false;
        return super.addAll(index, c);
    }

    @Override
    public WeightedCallable<T> set(int index, WeightedCallable<T> element) {
        _probabilitiesVerified = false;
        return super.set(index, element);
    }

    @Override
    public WeightedCallable<T> remove(int index) {
        _probabilitiesVerified = false;
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        _probabilitiesVerified = false;
        return super.remove(o);
    }

    @Override
    public void clear() {
        _probabilitiesVerified = false;
        super.clear();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        _probabilitiesVerified = false;
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        _probabilitiesVerified = false;
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        _probabilitiesVerified = false;
        return super.retainAll(c);
    }

    @Override
    public ListIterator<WeightedCallable<T>> listIterator(int index) {
        _probabilitiesVerified = false;
        return super.listIterator(index);
    }

    @Override
    public ListIterator<WeightedCallable<T>> listIterator() {
        _probabilitiesVerified = false;
        return super.listIterator();
    }

    @Override
    public Iterator<WeightedCallable<T>> iterator() {
        _probabilitiesVerified = false;
        return super.iterator();
    }

    @Override
    public boolean removeIf(Predicate<? super WeightedCallable<T>> filter) {
        _probabilitiesVerified = false;
        return super.removeIf(filter);
    }

    @Override
    public void replaceAll(UnaryOperator<WeightedCallable<T>> operator) {
        _probabilitiesVerified = false;
        super.replaceAll(operator);
    }

    @Override
    public Spliterator<WeightedCallable<T>> spliterator() {
        _probabilitiesVerified = false;
        return super.spliterator();
    }

}

