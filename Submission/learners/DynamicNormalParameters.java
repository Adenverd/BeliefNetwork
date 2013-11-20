package learners;

import java.util.List;

public class DynamicNormalParameters {
    protected double meanSum;
    protected double varianceSum;
    protected int numElements;

    public DynamicNormalParameters(){
        meanSum = 0.0;
        varianceSum = 0.0;
        numElements = 0;
    }

    public void addElement(double element){
        meanSum += element;
        varianceSum += Math.pow(element, 2);
        numElements++;
    }

    public double getMean(){
        return meanSum/numElements;
    }

    public double getVariance(){
        return (varianceSum - (meanSum*meanSum)/numElements)/(numElements-1);
    }

    public double getStdDev(){
        return Math.sqrt(getVariance());
    }
}
