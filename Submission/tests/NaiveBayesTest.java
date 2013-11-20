package tests;

import learners.NaiveBayes;
import ml.ARFFParser;
import ml.Matrix;

import java.io.IOException;

public class NaiveBayesTest {
    public static void main(String[] args) throws IOException {
        int featuresStart = 0, featuresEnd = 4;
        int labelsStart = 4, labelsEnd = 5;
        Matrix points = ARFFParser.loadARFF(args[0]);

        Matrix features = points.subMatrixCols(featuresStart, featuresEnd);
        Matrix labels = points.subMatrixCols(labelsStart, labelsEnd);

        NaiveBayes learner = new NaiveBayes();


        double error = learner.repeatNFoldCrossValidation(features, labels, 2, 5);
        System.out.println("MSE: " + error);
        //learner.train(features, labels);
    }
}
