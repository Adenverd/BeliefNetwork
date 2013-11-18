import Nodes.Node;
import Nodes.VariableNode;
import helpers.Rand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Network {
    protected List<VariableNode> nodes;

    public Network(){
        nodes = new ArrayList<VariableNode>();
    }

    public void markovChainMonteCarlo(int iterations){
        List<VariableNode> tempNodes = new ArrayList<VariableNode>(nodes);
        int numNodes = nodes.size();
        for(int i = 0; i < iterations; i++){
            for(int j = 0; j < numNodes; j++){
                VariableNode randNode = tempNodes.get(Rand.nextInt(tempNodes.size()));
                if(!randNode.isObserved()){
                    randNode.sample();
                }
                tempNodes.remove(randNode);
            }
        }
    }

    public VariableNode addNode(VariableNode variableNode){
        nodes.add(variableNode);
        return variableNode;
    }
}
