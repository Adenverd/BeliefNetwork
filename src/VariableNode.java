import exceptions.BeliefNetworkException;

import java.util.*;

public abstract class VariableNode extends Node{
    protected List<VariableNode> children;
    protected List<CategoricalNode> categoricalParents;

    public VariableNode(){
        children = new ArrayList<VariableNode>();
        categoricalParents = new ArrayList<CategoricalNode>();
    }

    public abstract double conditionalProbability(NodeValue value);

    /**
     * Draws from the joint probability of this node and assigns that nodeValue to this node.
     */
    public abstract void gibbsSample();

    /**
     * Adds a CategoricalNode to this.categoricalParents and resizes
     * this.parameters so that this.parameters.rows = product(foreach(this.parents.values)
     */
    public abstract void addCategoricalParent(CategoricalNode nParent);

    public void addChild(VariableNode child){
        this.getChildren().add(child);
    }

    //GETTERS AND SETTERS
    public List<VariableNode> getChildren() {
        return children;
    }

    public void setChildren(List<VariableNode> children) {
        this.children = children;
    }

    public List<CategoricalNode> getCategoricalParents() {
        return categoricalParents;
    }

    public void setCategoricalParents(List<CategoricalNode> categoricalParents) {
        this.categoricalParents = categoricalParents;
    }
}
