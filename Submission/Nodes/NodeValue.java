package Nodes;

public class NodeValue {
    private Node node;
    private double value;

    public NodeValue(Node n, double v){
        node = n;
        value = v;
    }

    //GETTERS AND SETTERS
    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
