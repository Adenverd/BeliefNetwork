package tests;

import Nodes.*;
import helpers.Rand;

import java.util.ArrayList;
import java.util.List;

public class MetropolisTest {

    public static void main(String[] args){
        List<VariableNode> nodes = new ArrayList<VariableNode>();

        NormalNode normalNode = new NormalNode();
        NodeValue zeroValue = new NodeValue(normalNode, 0.0);
        normalNode.setNodeValue(zeroValue);
        List<Node> normalNodeParameters = new ArrayList<Node>();
        ConstantNode meanNode = new ConstantNode(7);
        normalNodeParameters.add(meanNode);
        ConstantNode varianceNode = new ConstantNode(3);
        normalNodeParameters.add(varianceNode);
        normalNode.addParameters(VariableNode.NO_PARENTS, normalNodeParameters);
        nodes.add(normalNode);

        //CalculateSumAndVariance(normalNode, nodes, 100000, 100000); //7, 3

        NormalNode childNode = new NormalNode();
        childNode.setNodeValue(new NodeValue(childNode, 0));
        List<Node> childNodeParameters = new ArrayList<Node>();
        childNodeParameters.add(normalNode);
        childNodeParameters.add(new ConstantNode(1));
        childNode.addParameters(VariableNode.NO_PARENTS, childNodeParameters);
        normalNode.addChild(childNode);
        nodes.add(childNode);

        //CalculateSumAndVariance(normalNode, nodes, 100000, 100000); //7, 3
        childNode.setNodeValue(new NodeValue(childNode, 16.0));
        childNode.setObserved(true);

        //CalculateSumAndVariance(normalNode, nodes, 100000, 100000); //13.75, 0.75

        nodes.remove(normalNode);
        InverseGammaNode inverseGammaNode = new InverseGammaNode();
        inverseGammaNode.setNodeValue(new NodeValue(inverseGammaNode, 0));
        List<Node> igParameters = new ArrayList<Node>();
        igParameters.add(new ConstantNode(7));
        igParameters.add(new ConstantNode(3));
        inverseGammaNode.addParameters(VariableNode.NO_PARENTS, igParameters);
        childNodeParameters.set(0, inverseGammaNode);
        inverseGammaNode.addChild(childNode);
        nodes.add(inverseGammaNode);

        System.out.println(inverseGammaNode.pdf(5, igParameters));

        CalculateSumAndVariance(inverseGammaNode, nodes, 100000, 100000); //15.5, 1

    }

    public static void CalculateSumAndVariance(VariableNode unitNode, List<VariableNode> nodes, int burnIns, int samples){

        for(int i = 0; i < burnIns; i++){
            List<VariableNode> tempNodes = new ArrayList<VariableNode>(nodes);
            for(int j = 0; j < nodes.size(); j++){
                int index = Rand.nextInt(tempNodes.size());
                VariableNode node = tempNodes.get(index);
                if (!node.isObserved()){
                    node.sample();
                }
                tempNodes.remove(node);
            }
        }

        double sum = 0.0;
        double sumSqr = 0.0;
        for(int i = 0; i < samples; i++){
            List<VariableNode> tempNodes = new ArrayList<VariableNode>(nodes);
            for(int j = 0; j < nodes.size(); j++){
                int index = Rand.nextInt(tempNodes.size());
                VariableNode node = tempNodes.get(index);
                if (!node.isObserved()){
                    node.sample();
                }
                tempNodes.remove(node);
            }
            sum += unitNode.getValue();
            sumSqr += Math.pow(unitNode.getValue(), 2);
        }
        double mean = sum/samples;
        double variance = (sumSqr - (sum*sum)/samples)/(samples);

        System.out.println("Mean:\t\t" + mean); //should be 3
        System.out.println("Variance:\t" + variance); //should be 7

    }
}
