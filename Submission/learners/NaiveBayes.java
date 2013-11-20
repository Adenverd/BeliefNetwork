package learners;

import Nodes.*;
import helpers.Counter;
import helpers.Pair;
import ml.ColumnAttributes;
import ml.Matrix;
import ml.SupervisedLearner;

import java.util.*;

public class NaiveBayes extends SupervisedLearner {
    public static final int burnIns = 100;
    public static final int samples = 100;

    private Network network;
    private Map<VariableNode, ColumnAttributes> columnNodeMap;
    private Map<ColumnAttributes, VariableNode> nodeColumnMap;
    private List<CategoricalNode> categoricalParentNodes;
    private List<Node> parentNodes;
    private List<VariableNode> featureNodes;

    public NaiveBayes(){
        network = new Network();
        columnNodeMap = new HashMap<VariableNode, ColumnAttributes>();
        nodeColumnMap = new HashMap<ColumnAttributes, VariableNode>();
        categoricalParentNodes = new ArrayList<CategoricalNode>();
        parentNodes = new ArrayList<Node>();
        featureNodes = new ArrayList<VariableNode>();
    }

    @Override
    public void train(Matrix features, Matrix labels) {
        network = new Network();
        columnNodeMap = new HashMap<VariableNode, ColumnAttributes>();
        nodeColumnMap = new HashMap<ColumnAttributes, VariableNode>();
        categoricalParentNodes = new ArrayList<CategoricalNode>();
        parentNodes = new ArrayList<Node>();
        featureNodes = new ArrayList<VariableNode>();

        //create a node for each parent (label)
        for(ColumnAttributes columnAttributes : labels.getColumnAttributes()){
            //if the label is categorical, create a CategoricalNode
            if(columnAttributes.getColumnType()== ColumnAttributes.ColumnType.CATEGORICAL){
                CategoricalNode parentNode = new CategoricalNode();

                //add each value for the column as a possible value to the node
                for(int i = 0; i < columnAttributes.getValues().size(); i++){
                    parentNode.addPossibleValue(i);

                    //calculate the probability of this value and store it in the node
                    double probability = (double)labels.columnValueCount(columnAttributes, i)/(double)labels.getNumRows();
                    parentNode.setParameter(i, new ConstantNode(probability));
                }
                //map columnAttributes to Node
                columnNodeMap.put(parentNode, columnAttributes);
                nodeColumnMap.put(columnAttributes, parentNode);
                categoricalParentNodes.add(parentNode);
                parentNodes.add(parentNode);
            }
            //if the label is continuous, create a NormalNode
            else{
                NormalNode parentNode = new NormalNode();
                //create a node for each of the parameters for this node
                List<Double> parameters = parentNormalMaximumLikelihoodEstimator(columnAttributes, labels);
                List<Node> nodeParameters = new ArrayList<Node>();
                for(Double parameter : parameters){
                    ConstantNode nodeParameter = new ConstantNode(parameter);
                    nodeParameters.add(nodeParameter);
                }
                parentNode.addParameters(VariableNode.NO_PARENTS, nodeParameters);
                //map columnAttributes to Node
                columnNodeMap.put(parentNode, columnAttributes);
                nodeColumnMap.put(columnAttributes, parentNode);
                parentNodes.add(parentNode);
            }
        }

        //create a node for each child (feature)
        for(ColumnAttributes columnAttributes : features.getColumnAttributes()){
            //if the feature is categorical, create a categorical node
            if(columnAttributes.getColumnType()== ColumnAttributes.ColumnType.CATEGORICAL){
                CategoricalNode childNode = new CategoricalNode();
                //add possible values
                for (int i = 0; i < columnAttributes.getValues().size(); i++){
                    childNode.addPossibleValue(i);
                }

                //add each categorical parent node as a categorical parent
                for(CategoricalNode categoricalParent : categoricalParentNodes){
                    childNode.addCategoricalParent(categoricalParent);
                }

                //calculate the probabilities for each possible value of the child node for each possible combination of parent values
                Map<Pair<Set<NodeValue>, Double>, Double> probabilities = new HashMap<Pair<Set<NodeValue>, Double>, Double>();

                //add the probabilities as rows in the childNode's parameters table
                for (Map.Entry<Pair<Set<NodeValue>, Double>, Double> probability : probabilities.entrySet()){
                    NodeValue possibleValue = childNode.getPossibleValue(probability.getKey().y);
                    Node parameter = new ConstantNode(probability.getValue());
                    childNode.setParameter(probability.getKey().x, possibleValue, parameter);
                }
                nodeColumnMap.put(columnAttributes, childNode);
                columnNodeMap.put(childNode, columnAttributes);
                featureNodes.add(childNode);

                //add each parent node as a categorical parent
                for(Node parentNode : parentNodes){
                    VariableNode pNode = (VariableNode)parentNode;
                    pNode.addChild(childNode);
                }
            }
            //if the feature is continuous, create a normal node
            else{
                NormalNode childNode = new NormalNode();

                //add each categorical parent node as a categorical parent
                for(CategoricalNode categoricalParent : categoricalParentNodes){
                    childNode.addCategoricalParent(categoricalParent);
                }

                //calculate mean and variance for each possible set of parent values
                Map<Set<NodeValue>, DynamicNormalParameters> maxLikelihoods = childNormalMaximumLikelihoodEstimator(childNode.getParameters().keySet(), columnAttributes, features, labels);

                //store means and variances in child node
                for(Map.Entry<Set<NodeValue>, DynamicNormalParameters> entry : maxLikelihoods.entrySet()){
                    List<Node> parameters = new ArrayList<Node>();
                    parameters.add(new ConstantNode(entry.getValue().getMean()));
                    parameters.add(new ConstantNode(entry.getValue().getStdDev()));
                    childNode.addParameters(entry.getKey(), parameters);
                }

                featureNodes.add(childNode);
                nodeColumnMap.put(columnAttributes, childNode);
                columnNodeMap.put(childNode, columnAttributes);

                //add each parent node as a categorical parent
                for(Node parentNode : parentNodes){
                    VariableNode pNode = (VariableNode)parentNode;
                    pNode.addChild(childNode);
                }
            }
        }

        for(VariableNode node : nodeColumnMap.values()){
            network.add(node);
        }
    }

    @Override
    public List<Double> predict(List<Double> in) {
        //Set all the feature nodes to the values in in and set them to observed
        for(int i = 0; i<in.size(); i++){
            VariableNode featureNode = featureNodes.get(i);
            NodeValue nodeValue;
            if(columnNodeMap.get(featureNode).getColumnType()== ColumnAttributes.ColumnType.CATEGORICAL){
                CategoricalNode  categoricalNode = (CategoricalNode) featureNode;
                nodeValue = categoricalNode.getPossibleValue(in.get(i));
            }
            else{
                nodeValue = new NodeValue(featureNode, in.get(i));
            }
            featureNode.setNodeValue(nodeValue);
            featureNode.setObserved(true);
        }

        //Fuck it, assuming there's only one label node and it's categorical
        for(CategoricalNode parentNode : categoricalParentNodes){
                parentNode.setNodeValue(parentNode.getPossibleValue(0.0));
        }

        network.markovChainMonteCarlo(burnIns);


        List<Double> predictions = new ArrayList<Double>();
        for(int i = 0; i < samples; i++){
            List<NodeValue> sample = network.sampleNetwork(parentNodes);
            predictions.add(sample.get(0).getValue());
        }

        Counter<Double> counter = new Counter<Double>();
        for(Double prediction : predictions){
            counter.increment(prediction);
        }

        List<Double> prediction = new ArrayList<Double>();
        prediction.add(counter.getMax());

        return prediction;
    }

    /**
     * Calculates the mean and variance for a continuous column with no parent nodes.
     * @param columnAttributes
     * @param matrix
     * @return
     */
    public List<Double> parentNormalMaximumLikelihoodEstimator(ColumnAttributes columnAttributes, Matrix matrix){
        List<Double> parameters = new ArrayList<Double>(); //stores the mean ([0]) and the variance ([1])
        parameters.add(matrix.columnMean(matrix.getColumnIndex(columnAttributes)));
        parameters.add(matrix.columnVariance(matrix.getColumnIndex(columnAttributes)));
        return parameters;
    }

    /**
     * Calculates the probabilities for each pair of parentValues-featureValue.
     * @param parentValues
     * @param columnAttributes
     * @param features
     * @param labels
     * @return A Map<Pair, Double> where the Pair is the parentValues-featureValue combination and the Double is the probability of that pair in the matrices
     */
    public Map<Pair<Set<NodeValue>, Double>, Double> calculateChildValueProbabilities(Set<Set<NodeValue>> parentValues, ColumnAttributes columnAttributes, Matrix features, Matrix labels){
        int featureColIndex = features.getColumnIndex(columnAttributes);
        Map<Pair<Set<NodeValue>, Double>, Double> probabilities = new HashMap<Pair<Set<NodeValue>, Double>, Double>(); //maps parent value - feature value pairs to their probabilities.
        Map<Set<NodeValue>, Counter<Double>> counters = new HashMap<Set<NodeValue>, Counter<Double>>(); //maps parent value - feature value combinations to counters

        //add a counter for each Set<NodeValue> representing a set of parent values
        for(Set<NodeValue> parentVals : parentValues){
            Counter<Double> counter = new Counter<Double>();
            counters.put(parentVals, counter);
        }

        //loop over all the data
        for(int rowIndex = 0; rowIndex < features.getNumRows(); rowIndex++){
            //Construct a set of NodeValues representing the values of the parents in this row
            Set<NodeValue> rowLabelValues = new HashSet<NodeValue>();
            for(CategoricalNode categoricalParent : categoricalParentNodes){
                //get the column index in labels that corresponds to categoricalParent
                int categoricalParentColIndex = labels.getColumnIndex(columnNodeMap.get(categoricalParent));

                //get the node value that represents the value in this row for the column of categoricalParent
                rowLabelValues.add(categoricalParent.getPossibleValue(labels.getRow(rowIndex).get(categoricalParentColIndex)));
            }
            //increment the counter for this parentValues-featureValue combination
            double featureColValue = features.getRow(rowIndex).get(featureColIndex);
            counters.get(rowLabelValues).increment(featureColValue);
        }

        for(Map.Entry<Set<NodeValue>, Counter<Double>> counterEntry : counters.entrySet()){
            Set<NodeValue> parentSet = counterEntry.getKey();
            Counter<Double> featureValuesCounter = counterEntry.getValue();
            int parentOccurences = featureValuesCounter.getSum(); //the total number of times this parent set occurred in the data set, regardless of feature value
            for(Map.Entry<Double, Integer> featureValueCount : featureValuesCounter.entries()){
                Pair<Set<NodeValue>, Double> parentFeatureValues = new Pair<Set<NodeValue>, Double>(parentSet, featureValueCount.getKey());
                probabilities.put(parentFeatureValues, (double)featureValueCount.getValue()/(double)parentOccurences); //store the probability of this parentSet-featureValue combination
            }
        }

        return probabilities;
    }

    /**
     * Calculates the mean and variance for a continuous column with categorical parents for all possible combinations of parent values.
     * @param parentValues
     * @param columnAttributes
     * @return
     */
    public Map<Set<NodeValue>, DynamicNormalParameters> childNormalMaximumLikelihoodEstimator(Set<Set<NodeValue>> parentValues, ColumnAttributes columnAttributes, Matrix features, Matrix labels){
        int featureColIndex = features.getColumnIndex(columnAttributes); //index of the feature represented by columnAttributes in the features matrix
        Map<Set<NodeValue>, DynamicNormalParameters> parentValuesParameters = new HashMap<Set<NodeValue>, DynamicNormalParameters>(); //maps parent value permutations to their means and variances for this feature column

        //loop over all the data
        for(int rowIndex = 0; rowIndex < features.getNumRows(); rowIndex++){
            //Construct a set of NodeValues representing the values of the parents in this row
            Set<NodeValue> rowLabelValues = new HashSet<NodeValue>();
            for(CategoricalNode categoricalParent : categoricalParentNodes){
                //get the column index in labels that corresponds to categoricalParent
                int categoricalParentColIndex = labels.getColumnIndex(columnNodeMap.get(categoricalParent));

                //get the node value that represents the value in this row for the column of categoricalParent
                rowLabelValues.add(categoricalParent.getPossibleValue(labels.getRow(rowIndex).get(categoricalParentColIndex)));
            }

            //if we already have values for mean and variance given the parent values, update them with the value from this row's feature column
            if(parentValuesParameters.containsKey(rowLabelValues)){
                DynamicNormalParameters prevParameters = parentValuesParameters.get(rowLabelValues);
                double featureColValue = features.getRow(rowIndex).get(featureColIndex);
                prevParameters.addElement(featureColValue);
            }
            //if we haven't seen this set of parent values before, add this row's feature column value to mean and variance
            else{
                DynamicNormalParameters parameters = new DynamicNormalParameters();
                double featureColValue = features.getRow(rowIndex).get(featureColIndex);

                parameters.addElement(featureColValue);

                parentValuesParameters.put(rowLabelValues, parameters);
            }
        }

        return parentValuesParameters;
    }


}
