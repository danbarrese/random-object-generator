package net.pladform.random;

import java.util.function.Function;

/**
 * @author Dan Barrese
 */
public class WeightedFunction<T, R> {

    public final double probability;
    public final Function<T, R> function;

    public WeightedFunction(double probability, Function<T, R> function) {
        this.probability = probability;
        this.function = function;
    }

}

