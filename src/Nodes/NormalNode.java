package Nodes;

import helpers.Rand;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.List;
import java.util.Random;

public class NormalNode extends ContinuousNode{

    @Override
    /**
     * pdfParams.get(0) is mean, pdfParams.get(1) is variance (o^2)
     */
    public double pdf(double x, List<Node> pdfParams) {
        double mean = pdfParams.get(0).getNodeValue().getValue();
        double variance = pdfParams.get(1).getNodeValue().getValue();
        NormalDistribution normalDistribution = new NormalDistribution(mean, Math.sqrt(variance));

        return normalDistribution.density(x);
    }

    @Override
    /**
     * Metropolis sampling
     */
    public void sample(){
        //calculate joint distribution probability of current value for this node
        NodeValue currentValue = this.getNodeValue();
        double currentProbability = Math.log(this.conditionalProbability(currentValue));
        for (VariableNode child : this.getChildren()){
            currentProbability += Math.log(child.conditionalProbability(child.getNodeValue()));
        }

        //calculate joint distribution probability of a new value for this node
        NodeValue testValue = new NodeValue(this, currentValue.getValue() + Rand.nextGaussian());
        this.setNodeValue(testValue);
        double testProbability = Math.log(this.conditionalProbability(this.getNodeValue()));
        for(VariableNode child : this.getChildren()){
            testProbability += Math.log(child.conditionalProbability(child.getNodeValue()));
        }

        //compare probabilities. If testProbability is better, change current value to testValue
        if(Math.log(Rand.nextDouble()) < testProbability - currentProbability){
            this.setNodeValue(testValue);
        }
    }
}
