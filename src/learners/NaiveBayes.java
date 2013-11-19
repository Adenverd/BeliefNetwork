package learners;

import Nodes.*;
import helpers.Counter;
import helpers.Pair;
import ml.ColumnAttributes;
import ml.Matrix;
import ml.SupervisedLearner;

import java.util.*;

public class NaiveBayes extends SupervisedLearner {
    private Map<VariableNode, ColumnAttributes> columnNodeMap;
    private List<CategoricalNode> categoricalParentNodes;

    public NaiveBayes(){
        columnNodeMap = new HashMap<VariableNode, ColumnAttributes>();
        categoricalParentNodes = new ArrayList<CategoricalNode>();
    }

    @Override
    public void train(Matrix features, Matrix labels) {
//        Network network = new Network();
//
//        //create a node for each parent (label)
//        for(ColumnAttributes columnAttributes : labels.getColumnAttributes()){
//            //if the label is categorical, create a CategoricalNode
//            if(columnAttributes.getColumnType()== ColumnAttributes.ColumnType.CATEGORICAL){
//                CategoricalNode parentNode = new CategoricalNode();
//
//                //add each value for the column as a possible value to the node
//                for(int i = 0; i < columnAttributes.getValues().size(); i++){
//                    parentNode.addPossibleValue(i);
//
//                    //calculate the probability of this value and store it in the node
//                    double probability = (double)labels.columnValueCount(columnAttributes, i)/(double)labels.getNumRows();
//                    parentNode.setParameter(i, new ConstantNode(probability));
//                }
//                //map columnAttributes to Node
//                columnNodeMap.put(parentNode, columnAttributes);
//                categoricalParentNodes.add(parentNode);
//            }
//            //if the label is continuous, create a NormalNode
//            else{
//                NormalNode parentNode = new NormalNode();
//                //create a node for each of the parameters for this node
//                List<Double> parameters = parentNormalMaximumLikelihoodEstimator(columnAttributes, labels);
//                List<Node> nodeParameters = new ArrayList<Node>();
//                for(Double parameter : parameters){
//                    ConstantNode nodeParameter = new ConstantNode(parameter);
//                    nodeParameters.add(nodeParameter);
//                }
//                parentNode.addParameters(VariableNode.NO_PARENTS, nodeParameters);
//                //map columnAttributes to Node
//                columnNodeMap.put(parentNode, columnAttributes);
//            }
//        }
//
//        //create a node for each child (feature)
//        for(ColumnAttributes columnAttributes : features.getColumnAttributes()){
//            //if the feature is categorical, create a categorical node
//            if(columnAttributes.getColumnType()== ColumnAttributes.ColumnType.CATEGORICAL){
//                CategoricalNode childNode = new CategoricalNode();
//                //add possible values
//                for (int i = 0; i < columnAttributes.getValues().size(); i++){
//                    childNode.addPossibleValue(i);
//                }
//
//                //add each categorical parent node as a categorical parent
//                for(CategoricalNode categoricalParent : categoricalParentNodes){
//                    childNode.addCategoricalParent(categoricalParent);
//                }
//
//                //TODO: calculate the probabilities for each possible value of the child node for each possible combination of parent values
//
//                columnNodeMap.put(childNode, columnAttributes);
//            }
//            //if the feature is continuous, create a normal node
//            else{
//                NormalNode childNode = new NormalNode();
//
//                //add each categorical parent node as a categorical parent
//                for(CategoricalNode categoricalParent : categoricalParentNodes){
//                    childNode.addCategoricalParent(categoricalParent);
//                }
//
//                //calculate mean and variance for each possible set of parent values
//                Map<Set<NodeValue>, DynamicNormalParameters> maxLikelihoods = childNormalMaximumLikelihoodEstimator(childNode.getParameters().keySet(), columnAttributes, features, labels);
//
//                //store means and variances in child node
//                for(Map.Entry<Set<NodeValue>, DynamicNormalParameters> entry : maxLikelihoods.entrySet()){
//                    List<Node> parameters = new ArrayList<Node>();
//                    parameters.add(new ConstantNode(entry.getValue().getMean()));
//                    parameters.add(new ConstantNode(entry.getValue().getVariance()));
//                    childNode.addParameters(entry.getKey(), parameters);
//                }
//
//            }
//        }
    }

    @Override
    public List<Double> predict(List<Double> in) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
//
//    /**
//     * Calculates the mean and variance for a continuous column with no parent nodes.
//     * @param columnAttributes
//     * @param matrix
//     * @return
//     */
//    public List<Double> parentNormalMaximumLikelihoodEstimator(ColumnAttributes columnAttributes, Matrix matrix){
//        List<Double> parameters = new ArrayList<Double>(); //stores the mean ([0]) and the variance ([1])
//        parameters.add(matrix.columnMean(matrix.getColumnIndex(columnAttributes)));
//        parameters.add(matrix.columnVariance(matrix.getColumnIndex(columnAttributes)));
//        return parameters;
//    }
//
////    public Map<Set<NodeValue>, Double> calculateChildValueProbabilities(Set<Set<NodeValue>> parentValues, ColumnAttributes columnAttributes, Matrix features, Matrix labels){
////        int featureColIndex = features.getColumnIndex(columnAttributes);
////        Map<Pair<Set<NodeValue>, Double>, Double> probabilities = new HashMap<Pair<Set<NodeValue>, Double>, Double>(); //maps parent value - feature value pairs to their probabilities.
////        Map<Pair<Set<NodeValue>, Double>, Counter<Double>> counters = new HashMap<Pair<Set<NodeValue>, Double>, Counter<Double>>(); //maps parent value - feature value combinations to counters
////
////        for(int i = 0; i < columnAttributes.getValues().size(); i++){
////            for(Set<NodeValue> parentVals : parentValues){
////                Counter<Double> counter = new Counter<Double>();
////                Pair<Set<NodeValue>, Double> parentValueFeatureValuePair = new Pair<Set<NodeValue>, Double>(parentVals, (double)i);
////
////            }
////        }
////
////        //loop over all the data
////        for(int rowIndex = 0; rowIndex < features.getNumRows(); rowIndex++){
////            //Construct a set of NodeValues representing the values of the parents in this row
////            Set<NodeValue> rowLabelValues = new HashSet<NodeValue>();
////            for(CategoricalNode categoricalParent : categoricalParentNodes){
////                //get the column index in labels that corresponds to categoricalParent
////                int categoricalParentColIndex = labels.getColumnIndex(columnNodeMap.get(categoricalParent));
////
////                //get the node value that represents the value in this row for the column of categoricalParent
////                rowLabelValues.add(categoricalParent.getPossibleValue(labels.getRow(rowIndex).get(categoricalParentColIndex)));
////            }
////            double featureColValue = features.getRow(rowIndex).get(featureColIndex);
////            counters.get(featureColValue).increment(rowLabelValues);
////
////            //if we already have values for mean and variance given the parent values, update them with the values from this row's feature column
////            if(counters.contains(rowLabelValues)){
////                Double prevParameters = parentValuesParameters.get(rowLabelValues);
////                double featureColValue = features.getRow(rowIndex).get(featureColIndex);
////                prevParameters.addElement(featureColValue);
////            }
////            //if we haven't seen this set of parent values before, add this row's feature column value to mean and variance
////            else{
////                DynamicNormalParameters parameters = new DynamicNormalParameters();
////                double featureColValue = features.getRow(rowIndex).get(featureColIndex);
////
////                parameters.addElement(featureColValue);
////
////                parentValuesParameters.put(rowLabelValues, parameters);
////            }
////
////        }
////    }
//
//    /**
//     * Calculates the mean and variance for a continuous column with categorical parents for all possible combinations of parent values.
//     * @param parentValues
//     * @param columnAttributes
//     * @return
//     */
//    public Map<Set<NodeValue>, DynamicNormalParameters> childNormalMaximumLikelihoodEstimator(Set<Set<NodeValue>> parentValues, ColumnAttributes columnAttributes, Matrix features, Matrix labels){
//        int featureColIndex = features.getColumnIndex(columnAttributes); //index of the feature represented by columnAttributes in the features matrix
//        Map<Set<NodeValue>, DynamicNormalParameters> parentValuesParameters = new HashMap<Set<NodeValue>, DynamicNormalParameters>(); //maps parent value permutations to their means and variances for this feature column
//
//        //loop over all the data
//        for(int rowIndex = 0; rowIndex < features.getNumRows(); rowIndex++){
//            //Construct a set of NodeValues representing the values of the parents in this row
//            Set<NodeValue> rowLabelValues = new HashSet<NodeValue>();
//            for(CategoricalNode categoricalParent : categoricalParentNodes){
//                //get the column index in labels that corresponds to categoricalParent
//                int categoricalParentColIndex = labels.getColumnIndex(columnNodeMap.get(categoricalParent));
//
//                //get the node value that represents the value in this row for the column of categoricalParent
//                rowLabelValues.add(categoricalParent.getPossibleValue(labels.getRow(rowIndex).get(categoricalParentColIndex)));
//            }
//
//            //if we already have values for mean and variance given the parent values, update them with the value from this row's feature column
//            if(parentValuesParameters.containsKey(rowLabelValues)){
//                DynamicNormalParameters prevParameters = parentValuesParameters.get(rowLabelValues);
//                double featureColValue = features.getRow(rowIndex).get(featureColIndex);
//                prevParameters.addElement(featureColValue);
//            }
//            //if we haven't seen this set of parent values before, add this row's feature column value to mean and variance
//            else{
//                DynamicNormalParameters parameters = new DynamicNormalParameters();
//                double featureColValue = features.getRow(rowIndex).get(featureColIndex);
//
//                parameters.addElement(featureColValue);
//
//                parentValuesParameters.put(rowLabelValues, parameters);
//            }
//        }
//
//        return parentValuesParameters;
//    }
//
//
}
