package Nodes;

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
        return this.parameters.get(parentNodeValues).get(nodeValue).getNodeValue().getValue();
    }

    @Override
    public void sample() {
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
        nParent.addChild(this);

        if(this.getParameters().size()==0){ //if this node doesn't have any parameters, we only need to add the the parent's values each as a set
            for(NodeValue nParentValue : nParent.getPossibleValues().values()){
                Set<NodeValue> parentVal = new HashSet<NodeValue>();
                parentVal.add(nParentValue);
                this.getParameters().put(parentVal, new HashMap<NodeValue, Node>());
            }
        }
        else{
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

                        //add the new Nodes.NodeValue to the duplicate set
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
                this.getParameters().putAll(newParameters);
            }
        }
    }

    public NodeValue addPossibleValue(double value){
        NodeValue nVal = new NodeValue(this, value);

        //it is imperative that the key in possibleValues equals nVal.getValue()
        return this.getPossibleValues().put(value, nVal);
    }

    public NodeValue getPossibleValue(double value){
        if(this.getPossibleValues().containsKey(value)){
            return this.getPossibleValues().get(value);
        }

        throw new BeliefNetworkException("This node does not contain " + value + " as a possible value");
    }

    /**
     * Adds a parameter to parameters for a set of parentValues and one of this node's possible values.
     * @param parentValues
     * @param possibleValue
     * @param parameter
     */
    public void setParameter(Set<NodeValue> parentValues, NodeValue possibleValue, Node parameter){
        if (!this.getPossibleValues().containsValue(possibleValue)){//this.possibleValues should contain possibleValue
            throw new BeliefNetworkException("Cannot add a parameter for a possibleValue that isn't in this.possibleValues");
        }
        else if(!this.getParameters().containsKey(parentValues)){//parameters should contain a spot for this parameter
            if(this.getCategoricalParents().size()==0){//unless this node has no categorical parents
                this.getParameters().put(NO_PARENTS, new HashMap<NodeValue, Node>()); //put an entry for the empty set so we can add parameters to it
                this.getParameters().get(NO_PARENTS).put(possibleValue, parameter);
            }
            else{
                throw new BeliefNetworkException("Cannot add a parameter for a set of parent values that aren't in this.parameters");
            }
        }
        else{ //add the parameter
            this.getParameters().get(parentValues).put(possibleValue, parameter);
        }
    }

    /**
     * Adds a parameter to parameters for a set of parentValues and one of this node's possible values.
     * @param parentValues
     * @param posValue
     * @param parameter
     */
    public void setParameter(Set<NodeValue> parentValues, double posValue, Node parameter){
        if (!this.getPossibleValues().containsKey(posValue)){//this.possibleValues should contain possibleValue
            throw new BeliefNetworkException("Cannot add a parameter for a possibleValue that isn't in this.possibleValues");
        }

        NodeValue possibleValue = this.possibleValues.get(posValue);

        if(!this.getParameters().containsKey(parentValues)){//parameters should contain a spot for this parameter
            if(this.getCategoricalParents().size()==0){//unless this node has no categorical parents
                this.getParameters().put(NO_PARENTS, new HashMap<NodeValue, Node>()); //put an entry for the empty set so we can add parameters to it
                this.getParameters().get(NO_PARENTS).put(possibleValue, parameter);
            }
            else{
                throw new BeliefNetworkException("Cannot add a parameter for a set of parent values that aren't in this.parameters");
            }
        }
        else{ //add the parameter
            this.getParameters().get(parentValues).put(possibleValue, parameter);
        }
    }

    public void setParameter(NodeValue possibleValue, Node parameter){
        setParameter(NO_PARENTS, possibleValue, parameter);
    }

    public void setParameter(double posValue, Node parameter){
       setParameter(NO_PARENTS, posValue, parameter);
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
