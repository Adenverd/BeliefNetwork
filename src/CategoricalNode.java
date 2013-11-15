import exceptions.BeliefNetworkException;

import java.util.*;

public class CategoricalNode extends VariableNode {
    protected Map<Double, NodeValue> possibleValues;
    protected Map<Set<NodeValue>, Map<NodeValue, Node>> parameters; //stores the probabilities for each value for each possible combination of parent values

    public CategoricalNode(){
        possibleValues = new HashMap<Double, NodeValue>();
        parameters = new HashMap<Set<NodeValue>, Map<NodeValue, Node>>();
    }

    //TODO: Fix this to use new parameters structure
    public double conditionalProbability(NodeValue nodeValue){
        if (!possibleValues.containsKey(nodeValue.getValue()) || possibleValues.get(nodeValue.getValue())!=nodeValue){ //check for reference equality and that the key = nodeValue.getValue()
            //technically, probability is 0, but this shouldn't ever actually happen in practice
            throw new BeliefNetworkException("Attempting to check the probability of a categorical node containing a value that isn't in possibleValues.");
        }

        //find the appropriate list of parameter nodes in this.parameters by getting the NodeValues of all parents
        Set<NodeValue> parentNodeValues = new HashSet<NodeValue>();
        for(CategoricalNode parent : this.categoricalParents){
            parentNodeValues.add(parent.getNodeValue());
        }

        if(!this.parameters.containsKey(parentNodeValues)){
            throw new BeliefNetworkException("Parameters map doesn't contain an entry for the current combination of parent values.");
        }

        /*Categorical nodes have one column per possible nodeValue that contains the probability of that nodeValue given
        the current permutation of parent values.*/
        return this.parameters.get(parentNodeValues).get((int)nodeValue.getValue()).getNodeValue().getValue();
    }

    @Override
    public void gibbsSample() {
        Map<NodeValue, Double> valueProbabilities = new HashMap<NodeValue, Double>();//map to store the conditional probabilities of each possible nodeValue for this node

        for(NodeValue possibleValue : this.getPossibleValues().values()){
            this.setNodeValue(possibleValue);
            valueProbabilities.put(possibleValue, this.conditionalProbability(possibleValue));

            //multiply the conditional probability of this value at this node by the probabilities of the child nodes' values
            for(VariableNode child : this.getChildren()){
                valueProbabilities.put(possibleValue, valueProbabilities.get(possibleValue)*child.conditionalProbability(child.getNodeValue()));
            }
        }

        this.setNodeValue(drawFromDistribution(valueProbabilities));
    }

    public void addCategoricalParent(CategoricalNode nParent) {
        if(nParent.getPossibleValues().size()==0){
            throw new BeliefNetworkException("Attempting to add a categorical parent with 0 possible values");
        }

        this.getCategoricalParents().add(nParent);

        //duplicate parameters nParent.possibleValues.size() times
        boolean isFirstParentValue = true;
        NodeValue firstNParentValue = null;

        Set<Map<Set<NodeValue>, Map<NodeValue, Node>>> allDuplicateParameters = new HashSet<Map<Set<NodeValue>, Map<NodeValue, Node>>>();

        for(NodeValue nParentValue : nParent.getPossibleValues().values()){
            if(isFirstParentValue){ //if this is the first parent value, store it so we can add it to this.parameters after all the duplication stuffs
                firstNParentValue = nParentValue;
                isFirstParentValue = false;
            }
            else{
                //otherwise, we need to duplicate this.parameters and add nParentValue to each of the keys
                Map<Set<NodeValue>, Map<NodeValue, Node>> duplicateParameters = new HashMap<Set<NodeValue>, Map<NodeValue, Node>>();
                //for each parameters row
                for(Map.Entry<Set<NodeValue>, Map<NodeValue, Node>> parameter : this.getParameters().entrySet()){
                    //duplicate the set of NodeValues
                    Set<NodeValue> newNodeValueSet = new HashSet<NodeValue>();
                    newNodeValueSet.addAll(parameter.getKey());

                    //add the new NodeValue to the duplicate set
                    newNodeValueSet.add(nParentValue);

                    //store it in new map of new parameters
                    duplicateParameters.put(newNodeValueSet, new HashMap<NodeValue, Node>());
                }
                //store this map of new parameters in a set of maps to be added to this.parameters later
                allDuplicateParameters.add(duplicateParameters);
            }
        }

        for(Map.Entry<Set<NodeValue>, Map<NodeValue, Node>> parameter : this.getParameters().entrySet()){
            parameter.getKey().add(firstNParentValue);
        }

        //add all new parameters to this.parameters
        for(Map<Set<NodeValue>, Map<NodeValue, Node>> newParameters : allDuplicateParameters){
            this.parameters.putAll(newParameters);
        }
    }

    public void addPossibleValue(Double value){
        NodeValue nVal = new NodeValue(this, value);

        //it is imperative that the key in possibleValues equals nVal.getValue()
        this.possibleValues.put(value, nVal);
    }

    //GETTERS AND SETTERS
    public Map<Set<NodeValue>, Map<NodeValue, Node>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<Set<NodeValue>, Map<NodeValue, Node>> parameters) {
        this.parameters = parameters;
    }

    public Map<Double, NodeValue> getPossibleValues() {
        return possibleValues;
    }

    public void setPossibleValues(Map<Double, NodeValue> possibleValues) {
        this.possibleValues = possibleValues;
    }

    public void setValue(double value){
        boolean found = false;
        if (!possibleValues.containsKey(value)){
            throw new BeliefNetworkException("Attempting to set nodeValue to a nodeValue not in possibleValues");
        }
        this.nodeValue = possibleValues.get(value);
    }
}
