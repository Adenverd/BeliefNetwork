package Nodes;

import helpers.Rand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static helpers.MathUtility.normalPdf;
public class NormalNode extends ContinuousNode{

    public NormalNode(){
        super();
        this.setNodeValue(new NodeValue(this, 1));
    }

    public NormalNode(Node mean, Node stdDev){
        List<Node> parameters = new ArrayList<Node>();
        parameters.add(mean);
        parameters.add(stdDev);
        this.addParameters(VariableNode.NO_PARENTS, parameters);
        this.setNodeValue(new NodeValue(this, mean.getValue()));
    }

    @Override
    /**
     * pdfParams.get(0) is mean, pdfParams.get(1) is stdDev (o)
     */
    public double pdf(double x, List<Node> pdfParams) {
        double mean = pdfParams.get(0).getNodeValue().getValue();
        double stdDev = pdfParams.get(1).getNodeValue().getValue();
        return normalPdf(x, mean, stdDev);
//        NormalDistribution normalDistribution = new NormalDistribution(mean, stdDev*stdDev);
//
//        return normalDistribution.density(x);
    }

    @Override
    /**
     * Metropolis sampling
     */
    public void sample(){
        NodeValue currentValue = this.getNodeValue();

        //calculate joint distribution probability of a new value for this node
        NodeValue testValue = new NodeValue(this, currentValue.getValue() + Rand.nextGaussian());
        this.setNodeValue(testValue);
        double testProbability = Math.log(this.conditionalProbability(this.getNodeValue()));
        for(VariableNode child : this.getChildren()){
            testProbability += Math.log(child.conditionalProbability(child.getNodeValue()));
        }

        //calculate joint distribution probability of current value for this node
        this.setNodeValue(currentValue);
        double currentProbability = Math.log(this.conditionalProbability(currentValue));
        for (VariableNode child : this.getChildren()){
            currentProbability += Math.log(child.conditionalProbability(child.getNodeValue()));
        }

        double randUniform = Math.log(Rand.nextDouble());
        //compare probabilities. If testProbability is better, change current value to testValue
        if(randUniform < testProbability - currentProbability){
            this.setNodeValue(testValue);
        }
    }

    @Override
    public void addParameters(Set<NodeValue> parentValues, List<Node> parameterValues){
        parameters.put(parentValues, parameterValues);
        this.setNodeValue(new NodeValue(this, parameterValues.get(0).getValue()));
    }
}
