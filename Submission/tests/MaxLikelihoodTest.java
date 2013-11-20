package tests;

import learners.NaiveBayes;
import ml.ARFFParser;
import ml.Matrix;

import java.io.IOException;

public class MaxLikelihoodTest {
    public static void main(String[] args) throws IOException {
        int featuresStart = 0, featuresEnd = 3;
        int labelsStart = 3, labelsEnd = 4;

        Matrix points = ARFFParser.loadARFF(args[0]);
        Matrix features = points.subMatrixCols(featuresStart, featuresEnd);
        Matrix labels = points.subMatrixCols(labelsStart, labelsEnd);

        NaiveBayes learner = new NaiveBayes();
        learner.train(features, labels);
    }
}
