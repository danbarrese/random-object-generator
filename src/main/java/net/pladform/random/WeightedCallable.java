package net.pladform.random;

import java.util.concurrent.Callable;

/**
 * @author Dan Barrese
 */
public class WeightedCallable<T> {

    public final double probability;
    public final Callable<T> callable;

    public WeightedCallable(double probability, Callable<T> callable) {
        this.probability = probability;
        this.callable = callable;
    }

}

