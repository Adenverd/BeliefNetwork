import exceptions.BeliefNetworkException;

import java.util.*;

public abstract class ContinuousNode extends VariableNode{

    protected Map<Set<NodeValue>, List<Node>> parameters;

    public ContinuousNode(){
        parameters = new HashMap<Set<NodeValue>, List<Node>>();
    }

    @Override
    public double conditionalProbability(NodeValue value) {
        //find appropriate row in this.parameters
        Set<NodeValue> parentValues = new HashSet<NodeValue>();
        for(CategoricalNode parent : this.getCategoricalParents()){
            parentValues.add(parent.getNodeValue());
        }

        //Continuous nodes use PDF to calculate conditional probability
        return this.pdf(value.getValue(), this.getParameters().get(parentValues));
    }

    @Override
    public void gibbsSample(){

    }

    @Override
    public void addCategoricalParent(CategoricalNode nParent) {
        if(nParent.getPossibleValues().size()==0){
            throw new BeliefNetworkException("Attempting to add a categorical parent with 0 possible values");
        }

        this.getCategoricalParents().add(nParent);

        //duplicate parameters nParent.possibleValues.size() times
        boolean isFirstParentValue = true;
        NodeValue firstNParentValue = null;

        Set<Map<Set<NodeValue>, List<Node>>> allDuplicateParameters = new HashSet<Map<Set<NodeValue>, List<Node>>>();

        for(NodeValue nParentValue : nParent.getPossibleValues().values()){
            if(isFirstParentValue){ //if this is the first parent value, store it so we can add it to this.parameters after all the duplication stuffs
                firstNParentValue = nParentValue;
                isFirstParentValue = false;
            }
            else{
                //otherwise, we need to duplicate this.parameters and add nParentValue to each of the keys
                Map<Set<NodeValue>, List<Node>> duplicateParameters = new HashMap<Set<NodeValue>, List<Node>>();
                //for each parameters row
                for(Map.Entry<Set<NodeValue>, List<Node>> parameter : this.getParameters().entrySet()){
                    //duplicate the set of NodeValues
                    Set<NodeValue> newNodeValueSet = new HashSet<NodeValue>();
                    newNodeValueSet.addAll(parameter.getKey());

                    //add the new NodeValue to the duplicate set
                    newNodeValueSet.add(nParentValue);

                    //store it in new map of new parameters
                    duplicateParameters.put(newNodeValueSet, new ArrayList<Node>());
                }
                //store this map of new parameters in a set of maps to be added to this.parameters later
                allDuplicateParameters.add(duplicateParameters);
            }
        }

        for(Map.Entry<Set<NodeValue>, List<Node>> parameter : this.getParameters().entrySet()){
            parameter.getKey().add(firstNParentValue);
        }

        //add all new parameters to this.parameters
        for(Map<Set<NodeValue>, List<Node>> newParameters : allDuplicateParameters){
            this.parameters.putAll(newParameters);
        }
    }

    /**
     * Returns the probability of x for this distribution given the parameters
     * @param pdfParams
     * @param x
     * @return
     */
    public abstract double pdf(double x, List<Node> pdfParams);

    public Map<Set<NodeValue>, List<Node>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<Set<NodeValue>, List<Node>> parameters) {
        this.parameters = parameters;
    }

}
