package Nodes;

public class SumNode extends Node{
    protected Node node1;
    protected Node node2;

    public SumNode(Node n1, Node n2){
        this.setObserved(true);
        node1 = n1;
        node2 = n2;
    }

    @Override
    public NodeValue getNodeValue(){
        return new NodeValue(this, node1.getValue()+ node2.getValue());
    }

    @Override
    public double getValue(){
        return node1.getValue() + node2.getValue();
    }
}
