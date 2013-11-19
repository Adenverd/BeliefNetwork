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
        List<VariableNode> tempNodes = new ArrayList<VariableNode>(nodes);
        int numNodes = nodes.size();
        for(int i = 0; i < iterations; i++){
            Collections.shuffle(tempNodes);
            for(int j = 0; j < tempNodes.size(); j++){
                VariableNode randNode = tempNodes.get(j);
                if(!randNode.isObserved()){
                    randNode.sample();
                    int blah = 0;
                }
            }
        }
    }

    public List<NodeValue> sampleNetwork(List<Node> unitNodes){
        List<VariableNode> tempNodes = new ArrayList<VariableNode>(nodes);
        int numNodes = nodes.size();
        Collections.shuffle(tempNodes);
        for(int j = 0; j < numNodes; j++){
            VariableNode node = tempNodes.get(j);
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
