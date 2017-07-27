/*
 * Copyright 2016 Dan Barrese
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grepcurl.random;

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

public class WeightedCallableChooser<T> extends ArrayList<WeightedCallable<T>> {

    private static final Object LOCK = new Object();
    private ObjectGenerator _objectGenerator;
    private AtomicBoolean _probabilitiesVerified;

    public WeightedCallableChooser(ObjectGenerator g) {
        _objectGenerator = g;
        _probabilitiesVerified = new AtomicBoolean(false);
    }

    public T doRandomOperation() throws Exception {
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
        _probabilitiesVerified.set(false);
        return super.add(tCallableWithProbability);
    }

    @Override
    public void add(int index, WeightedCallable<T> element) {
        _probabilitiesVerified.set(false);
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends WeightedCallable<T>> c) {
        _probabilitiesVerified.set(false);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends WeightedCallable<T>> c) {
        _probabilitiesVerified.set(false);
        return super.addAll(index, c);
    }

    @Override
    public WeightedCallable<T> set(int index, WeightedCallable<T> element) {
        _probabilitiesVerified.set(false);
        return super.set(index, element);
    }

    @Override
    public WeightedCallable<T> remove(int index) {
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
    public ListIterator<WeightedCallable<T>> listIterator(int index) {
        _probabilitiesVerified.set(false);
        return super.listIterator(index);
    }

    @Override
    public ListIterator<WeightedCallable<T>> listIterator() {
        _probabilitiesVerified.set(false);
        return super.listIterator();
    }

    @Override
    public Iterator<WeightedCallable<T>> iterator() {
        _probabilitiesVerified.set(false);
        return super.iterator();
    }

    @Override
    public boolean removeIf(Predicate<? super WeightedCallable<T>> filter) {
        _probabilitiesVerified.set(false);
        return super.removeIf(filter);
    }

    @Override
    public void replaceAll(UnaryOperator<WeightedCallable<T>> operator) {
        _probabilitiesVerified.set(false);
        super.replaceAll(operator);
    }

    @Override
    public Spliterator<WeightedCallable<T>> spliterator() {
        _probabilitiesVerified.set(false);
        return super.spliterator();
    }

}

