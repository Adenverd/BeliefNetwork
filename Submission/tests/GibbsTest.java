package tests;

import Nodes.CategoricalNode;
import Nodes.ConstantNode;
import Nodes.NodeValue;
import exceptions.BeliefNetworkException;
import helpers.Counter;

import java.util.*;

public class GibbsTest {

    public static void main(String[] args){
        CategoricalNode nodeA = new CategoricalNode();
        CategoricalNode nodeB = new CategoricalNode();
        CategoricalNode nodeC = new CategoricalNode();
        List<CategoricalNode> nodes = new ArrayList<CategoricalNode>();
        nodes.add(nodeA);
        nodes.add(nodeB);
        nodes.add(nodeC);

        //Build nodes, set a's parameters
        nodeA.addPossibleValue(0.0);
        nodeA.addPossibleValue(1.0);
        nodeA.setParameter(nodeA.getPossibleValue(1.0), new ConstantNode(2.0 / 5.0));
        nodeA.setParameter(nodeA.getPossibleValue(0.0), new ConstantNode(3.0 / 5.0));

        nodeB.addPossibleValue(0.0);
        nodeB.addPossibleValue(1.0);
        nodeC.addPossibleValue(0.0);
        nodeC.addPossibleValue(1.0);

        //set values
        nodeA.setValue(0.0);
        nodeB.setValue(0.0);
        nodeC.setValue(0.0);

        //connect nodes
        nodeB.addCategoricalParent(nodeA);
        nodeC.addCategoricalParent(nodeB);

        //set b parameters
        Set<NodeValue> parentValues = new HashSet<NodeValue>();
        parentValues.add(nodeA.getPossibleValue(1.0));
        nodeB.setParameter(parentValues, 1.0, new ConstantNode(2.0 / 3.0));
        nodeB.setParameter(parentValues, 0.0, new ConstantNode(1.0/3.0));
        parentValues.clear();
        parentValues.add(nodeA.getPossibleValue(0.0));
        nodeB.setParameter(parentValues, 1.0, new ConstantNode(3.0/7.0));
        nodeB.setParameter(parentValues, 0.0, new ConstantNode(4.0/7.0));

        //set c parameters
        parentValues.clear();
        parentValues.add(nodeB.getPossibleValue(1.0));
        nodeC.setParameter(parentValues, 1.0, new ConstantNode(1.0 / 2.0));
        nodeC.setParameter(parentValues, 0.0, new ConstantNode(1.0/2.0));
        parentValues.clear();
        parentValues.add(nodeB.getPossibleValue(0.0));
        nodeC.setParameter(parentValues, 1.0, new ConstantNode(1.0/3.0));
        nodeC.setParameter(parentValues, 0.0, new ConstantNode(2.0/3.0));

        Random random = new Random(1);

        Counter<Set<NodeValue>> distributionCounter = new Counter<Set<NodeValue>>();
        for (int i = 0; i < 10000; i++){
            Set<NodeValue> nodeValues = new HashSet<NodeValue>();
            for (int j = 0; j < 3; j++){ //sample each non-observed node
                int index = random.nextInt(nodes.size());
                CategoricalNode node = nodes.get(index);
                nodes.remove(index);
                if (!node.isObserved()){
                    node.sample();
                }
                nodeValues.add(node.getNodeValue());
            }

            if(nodes.size()!=0){
                throw new BeliefNetworkException("nodes should be empty");
            }
            distributionCounter.increment(nodeValues);
            nodes.add(nodeA);
            nodes.add(nodeB);
            nodes.add(nodeC);
        }

        nodeA.setValue(0.0);
        nodeA.setObserved(true);
        nodeC.setValue(0.0);
        nodeC.setObserved(true);
        int zeroCount = 0;
        int oneCount = 0;
        Counter<NodeValue> valueCounter = new Counter<NodeValue>();
        for(int i = 0; i < 10000; i++){
            nodeB.sample();
            Double value = nodeB.getNodeValue().getValue();
            if (value.equals(0.0)){
                zeroCount++;
            }
            else{
                oneCount++;
            }
        }
        System.out.println(zeroCount + "\t" + oneCount);
    }
}
