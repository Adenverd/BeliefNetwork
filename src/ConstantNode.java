public class ConstantNode extends Node {
    public ConstantNode(double value){
        this.observed = true;
        this.value = value;
    }
}
