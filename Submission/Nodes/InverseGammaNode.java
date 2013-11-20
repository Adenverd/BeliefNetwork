package Nodes;

import helpers.Rand;

import java.util.List;
import static helpers.MathUtility.inverseGammaPdf;

public class InverseGammaNode extends ContinuousNode{

    public InverseGammaNode(){
        this.setNodeValue(new NodeValue(this, 1));
    }
    /**
     * pdfParams.get(0) is alpha, pdfParams.get(1) is beta
     * @param pdfParams
     * @return
     */
    @Override
    public double pdf(double x, List<Node> pdfParams) {
        if(!(x>0)){
            return 0;
        }
        double alpha = pdfParams.get(0).getNodeValue().getValue();
        double beta = pdfParams.get(1).getNodeValue().getValue();
        return inverseGammaPdf(x, alpha, beta);
//        double gamma = Gamma.gamma(alpha);
//
//        //return (Math.exp(-1*(beta/x))*Math.pow(beta/x, alpha))/(x*gamma);
//        return (Math.pow(beta, alpha)/gamma)*(Math.pow(x, (-1 * alpha)-1))*(Math.exp(-1*beta/x));
    }

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
}
