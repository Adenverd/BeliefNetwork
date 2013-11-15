public class GibbsTest {

    public static void main(String[] args){
        CategoricalNode nodeA = new CategoricalNode();
        CategoricalNode nodeB = new CategoricalNode();
        CategoricalNode nodeC = new CategoricalNode();
        ConstantNode probAIsTrue = new ConstantNode(2.0/5.0);

        nodeA.addPossibleValue(0.0);
        nodeA.addPossibleValue(1.0);
        nodeB.addPossibleValue(0.0);
        nodeB.addPossibleValue(1.0);
        nodeC.addPossibleValue(0.0);
        nodeC.addPossibleValue(1.0);
    }
}
