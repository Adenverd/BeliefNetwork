package ml;

import helpers.MathUtility;
import helpers.Vector;

import java.util.List;
import java.util.Random;

/**
 * @author Sawyer Anderson
 * @date 2013.08.27
 */
public abstract class SupervisedLearner {

    public static int counter = 0;

    public abstract void train(Matrix features, Matrix labels);

    public abstract List<Double> predict(List<Double> in);

    /**
     * Uses Hamming distance for Categorical attributes, and means for Continuous
     * @return Vector based SSE
     */
    public double getAccuracy(Matrix features, Matrix labels) {
        double sum = 0;
        for (int i = 0; i < labels.getNumRows(); i++) {

            List<Double> result = predict(features.getRow(i));

            if (labels.getNumCols() != result.size()) {
                throw new MLException(String.format(
                        "Returned result size [%d] is different than number of columns [%d] in labels!",
                        result.size(), labels.getNumCols()));
            }

            double magnitude = 0;
            for (int j = 0; j < labels.getNumCols(); j++) {

                double actual = labels.getRow(i).get(j);
                double predicted = result.get(j);

                if (labels.isCategorical(j)) {
                    magnitude += MathUtility.isEquals(actual, predicted) ? 0 : 1;
                } else {
                    double res = actual - predicted;
                    magnitude += res * res;
                }
            }
            sum += magnitude;
        }
        return sum;
    }

    /**
     * Returns the MSE for multiple repetitions of n-fold cross validation. Shuffles in between repetitions.
     * @param features
     * @param labels
     * @param n
     * @param repetitions
     * @return
     */
    public double repeatNFoldCrossValidation(Matrix features, Matrix labels, int n, int repetitions){

        double mseTotal = 0.0;
        long seed = System.currentTimeMillis();
        for (int i = 0; i < repetitions; i++) {
            Matrix shuffledFeatures = features.shuffle(new Random(seed));
            Matrix shuffledLabels = labels.shuffle(new Random(seed));
            mseTotal += this.nFoldCrossValidation(shuffledFeatures, shuffledLabels, n);
        }

        return mseTotal / repetitions;
    }

    public double nFoldCrossValidation(Matrix features, Matrix labels, int n) {

        int rows = features.getNumRows();

        if (n > rows) {
            throw new MLException(String.format(
                    "n [%d] must be <= row size [%d]", n, features.getNumRows()));
        }

        if (rows != labels.getNumRows()) {
            throw new MLException("Features and labels rows mismatch");
        }

        int foldSize = rows % n == 0 ? rows / n : rows / n + 1;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            int foldStart = i * foldSize;
            int foldEnd = (i + 1) * foldSize;
            if (foldEnd > rows) {
                foldEnd = rows;
            }

            Matrix toTrainFeatures = new Matrix(features);
            Matrix toTrainLabels = new Matrix(labels);

            Matrix toPredictFeatures = toTrainFeatures.removeFold(foldStart, foldEnd);
            Matrix toPredictLabels = toTrainLabels.removeFold(foldStart, foldEnd);

            train(toTrainFeatures, toTrainLabels);
            sum += this.getAccuracy(toPredictFeatures, toPredictLabels);
        }
        return sum / rows;
    }
}
