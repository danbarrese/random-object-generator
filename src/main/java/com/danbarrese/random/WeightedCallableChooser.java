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
package com.danbarrese.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.Validate;

public class WeightedCallableChooser<T> extends ArrayList<WeightedCallable<T>> {

    private static final Object LOCK = new Object();
    private final ObjectGenerator objectGenerator;
    private final AtomicBoolean probabilitiesVerified;

    public WeightedCallableChooser(ObjectGenerator g) {
        objectGenerator = g;
        probabilitiesVerified = new AtomicBoolean(false);
    }

    public T doRandomOperation() throws Exception {
        if (!probabilitiesVerified.get()) {
            synchronized (LOCK) {
                if (!probabilitiesVerified.get()) {
                    Validate.isTrue(this.stream().mapToDouble(value -> value.probability).sum() == 1.0);
                    probabilitiesVerified.set(true);
                }
            }
        }
        double p = objectGenerator.randomProbability();
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
        probabilitiesVerified.set(false);
        return super.add(tCallableWithProbability);
    }

    @Override
    public void add(int index, WeightedCallable<T> element) {
        probabilitiesVerified.set(false);
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends WeightedCallable<T>> c) {
        probabilitiesVerified.set(false);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends WeightedCallable<T>> c) {
        probabilitiesVerified.set(false);
        return super.addAll(index, c);
    }

    @Override
    public WeightedCallable<T> set(int index, WeightedCallable<T> element) {
        probabilitiesVerified.set(false);
        return super.set(index, element);
    }

    @Override
    public WeightedCallable<T> remove(int index) {
        probabilitiesVerified.set(false);
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        probabilitiesVerified.set(false);
        return super.remove(o);
    }

    @Override
    public void clear() {
        probabilitiesVerified.set(false);
        super.clear();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        probabilitiesVerified.set(false);
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        probabilitiesVerified.set(false);
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        probabilitiesVerified.set(false);
        return super.retainAll(c);
    }

    @Override
    public ListIterator<WeightedCallable<T>> listIterator(int index) {
        probabilitiesVerified.set(false);
        return super.listIterator(index);
    }

    @Override
    public ListIterator<WeightedCallable<T>> listIterator() {
        probabilitiesVerified.set(false);
        return super.listIterator();
    }

    @Override
    public Iterator<WeightedCallable<T>> iterator() {
        probabilitiesVerified.set(false);
        return super.iterator();
    }

    @Override
    public boolean removeIf(Predicate<? super WeightedCallable<T>> filter) {
        probabilitiesVerified.set(false);
        return super.removeIf(filter);
    }

    @Override
    public void replaceAll(UnaryOperator<WeightedCallable<T>> operator) {
        probabilitiesVerified.set(false);
        super.replaceAll(operator);
    }

    @Override
    public Spliterator<WeightedCallable<T>> spliterator() {
        probabilitiesVerified.set(false);
        return super.spliterator();
    }

}

