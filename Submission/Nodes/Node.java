package Nodes;

import exceptions.BeliefNetworkException;
import helpers.Rand;

import java.util.*;

public class Node {
    protected NodeValue nodeValue;
    protected boolean observed;
    /**
     * Draws from a probability distribution given by probabilities. Assumes the indices of probabilities are the values
     * associated with the probabilities. Normalizes probabilities before drawing.
     * @param probabilities
     * @return the nodeValue drawn
     */
    public static NodeValue drawFromDistribution(Map<NodeValue, Double> probabilities){
        Map<NodeValue, Double> normalizedProbabilities = normalizeProbabilities(probabilities);

        Double randVal = Rand.nextDouble();

        double sum = 0.0;
        for(NodeValue key : normalizedProbabilities.keySet()){
            sum += normalizedProbabilities.get(key);
            if (randVal < sum){
                return key;
            }
        }

        throw new BeliefNetworkException("Failed to draw from normalized distribution, double-check that normalizedProbabilities.values() sum to 1");
    }

    /**
     * Returns a normalized list where each nodeValue is the normalized nodeValue of the corresponding probabilities nodeValue.
     * @param probabilities
     * @return
     */
    private static Map<NodeValue, Double> normalizeProbabilities(Map<NodeValue, Double> probabilities) {
        Double sum = 0.0;
        Map<NodeValue, Double> normalizedProbabilities = new HashMap<NodeValue, Double>();
        for(Double prob : probabilities.values()){
            if(prob < 0){
                throw new BeliefNetworkException("Cannot have a negative probability");
            }
            sum += prob;
        }
        Double nSum = 0.0;
        for(NodeValue key : probabilities.keySet()){
            normalizedProbabilities.put(key, probabilities.get(key)/sum); //concurrent modification exception?
            nSum+=(normalizedProbabilities.get(key));
        }
        return normalizedProbabilities;
    }

    public double getValue(){
        return nodeValue.getValue();
    }

    public NodeValue getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(NodeValue nodeValue) {
        this.nodeValue = nodeValue;
    }

    public boolean isObserved() {
        return observed;
    }

    public void setObserved(boolean observed) {
        this.observed = observed;
    }
}
