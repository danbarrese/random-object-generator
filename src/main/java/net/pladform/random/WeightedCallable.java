package net.pladform.random;

import java.util.concurrent.Callable;

/**
 * @author Dan Barrese
 */
public class WeightedCallable<T> {

    public double probability;
    public Callable<T> callable;

    public WeightedCallable(double probability, Callable<T> callable) {
        this.probability = probability;
        this.callable = callable;
    }

}

