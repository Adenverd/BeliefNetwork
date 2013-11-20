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
        this.setNodeValue(new NodeValue(this, node1.getNodeValue().getValue() + node2.getNodeValue().getValue()));
        return this.nodeValue;
    }

    @Override
    public double getValue(){
        return this.getNodeValue().getValue();
    }
}
