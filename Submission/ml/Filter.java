package ml;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author Kanat Bekt
 * @date 9/1/2013
 * Wraps another supervised learner. It applies some unsupervised operation
 * to the data before presenting it to the learner.
 */
public class Filter extends SupervisedLearner {

    private SupervisedLearner pLearner;
    private UnsupervisedLearner pTransform;
    private boolean filterInputs;

    /**
     * 	This takes ownership of pLearner and pTransform.
     * @param filterInputs if true, it applies the transform only the the input features.
     *                     if false, it applies the transform only the the output labels
     *                     (If you wish to transform both inputs and outputs, you must wrap a filter in a filter)
     */
    public Filter(SupervisedLearner pLearner, UnsupervisedLearner pTransform, boolean filterInputs) {
        this.pLearner = pLearner;
        this.pTransform = pTransform;
        this.filterInputs = filterInputs;
    }

    /**
     * Trains the transform and the inner learner.
     */
    @Override
    public void train(Matrix features, Matrix labels) {
        if (features.getNumRows() != labels.getNumRows()) {
            throw new MLException("Features and labels must have the same number of rows.");
        }
        if (filterInputs) {
            Matrix matrix = trainAndTransform(features);
            pLearner.train(matrix, labels);

        } else {
            Matrix matrix = trainAndTransform(labels);
            pLearner.train(features, matrix);
        }
    }

    /**
     * Makes a prediction.
     */
    @Override
    public List<Double> predict(List<Double> in) {
        if (filterInputs) {
            return pLearner.predict(pTransform.transform(in));
        } else {
            return pTransform.untransform(pLearner.predict(in));
        }
    }

    private Matrix trainAndTransform(Matrix matrix) {
        pTransform.train(matrix);

        Matrix nMatrix = new Matrix(matrix, true);
        for (List<Double> row : matrix.getData()){
            nMatrix.addRow(pTransform.transform(row));
        }

        return nMatrix;
    }
}
