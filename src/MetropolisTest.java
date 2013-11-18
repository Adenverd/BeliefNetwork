import Nodes.*;

import java.util.ArrayList;
import java.util.List;

public class MetropolisTest {

    public static void main(String[] args){

        NormalNode normalNode = new NormalNode();
        NodeValue zeroValue = new NodeValue(normalNode, 0.0);
        normalNode.setNodeValue(zeroValue);
        List<Node> normalNodeParameters = new ArrayList<Node>();
        ConstantNode meanNode = new ConstantNode(7);
        normalNodeParameters.add(meanNode);
        ConstantNode varianceNode = new ConstantNode(3);
        normalNodeParameters.add(varianceNode);
        normalNode.addParameters(VariableNode.NO_PARENTS, normalNodeParameters);

        for (int i = 0; i < 100000; i++){
            normalNode.sample();
        }
        double sum = 0.0;
        double sumSqr = 0.0;
        for(int i = 0; i < 100000; i++){
            normalNode.sample();
            sum += normalNode.getValue();
            sumSqr += Math.pow(normalNode.getValue(), 2);
        }
        double mean = sum/100000.0;
        double variance = (sumSqr - (sum*sum)/100000.0)/(100000.0);

        System.out.println("Mean:\t\t" + mean);
        System.out.println("Variance:\t" + variance);
    }
}
