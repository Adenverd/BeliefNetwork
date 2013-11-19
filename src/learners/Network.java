package learners;

import Nodes.Node;
import Nodes.NodeValue;
import Nodes.VariableNode;
import helpers.Rand;

import java.util.*;

public class Network {
    protected List<VariableNode> nodes;

    public Network(){
        nodes = new ArrayList<VariableNode>();
    }

    public void markovChainMonteCarlo(int iterations){
        for(int i = 0; i < iterations; i++){
            Collections.shuffle(nodes);
            for(int j = 0; j < nodes.size(); j++){
                VariableNode randNode = nodes.get(j);
                if(!randNode.isObserved()){
                    randNode.sample();
                    int blah = 0;
                }
            }
        }
    }

    public List<NodeValue> sampleNetwork(List<Node> unitNodes){
        Collections.shuffle(nodes);
        for(int j = 0; j < nodes.size(); j++){
            VariableNode node = nodes.get(j);
            if(!node.isObserved()){
                node.sample();
            }
        }

        List<NodeValue> values = new ArrayList<NodeValue>();
        for(Node node : unitNodes){
            values.add(node.getNodeValue());
        }

        return values;
    }

    public VariableNode add(VariableNode variableNode){
        nodes.add(variableNode);
        return variableNode;
    }
}
