package ml;

import java.util.List;

/**
 * @author Kanat Bekt
 * @date 9/1/2013
 */
public abstract class UnsupervisedLearner {

    public abstract void train(Matrix data);

    public abstract Matrix outputTemplate();

    public abstract List<Double> transform(List<Double> in);

    public abstract List<Double> untransform(List<Double> in);
}
