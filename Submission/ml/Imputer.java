package ml;

import helpers.Counter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kanat Bekt
 * @date 9/1/2013
 * Replaces missing continuous values with the mean.
 * Replaces missing categorical values with the most-common value (or mode).
 */
public class Imputer extends UnsupervisedLearner {

    private List<Double> columnCentroids = new ArrayList<Double>();
    private Matrix template;

    /**
     * Computes the column means of continuous attributes, and
     * mode of of nominal attributes.
     */
    @Override
    public void train(final Matrix matrix) {
        List<List<Double>> data = matrix.getData();
        for (int colIndex = 0; colIndex < data.get(0).size(); colIndex++){
            if (matrix.getColumnAttributes(colIndex).getColumnType()== ColumnAttributes.ColumnType.CATEGORICAL){
                Counter<Double> counter = new Counter<Double>();
                for (int rowIndex = 0; rowIndex < data.size(); rowIndex++){
                    Double value = data.get(rowIndex).get(colIndex);
                    if(!value.equals(Matrix.UNKNOWN_VALUE)){
                        counter.increment(data.get(rowIndex).get(colIndex));
                    }
                }
                columnCentroids.add(colIndex, counter.getMax());
            }
            else if (matrix.getColumnAttributes(colIndex).getColumnType()== ColumnAttributes.ColumnType.CONTINUOUS){
                Double total = 0.0;
                for (int rowIndex = 0; rowIndex < data.size(); rowIndex++){
                    Double value = data.get(rowIndex).get(colIndex);
                    if(!value.equals(Matrix.UNKNOWN_VALUE)){
                        total+=data.get(rowIndex).get(colIndex);
                    }
                }
                Double average = total/data.size();
                columnCentroids.add(colIndex, average);
            }
        }

    }

    /**
     * Replaces missing values with the centroid value.
     */
    @Override
    public List<Double> transform(List<Double> in) {
        List<Double> out = new ArrayList<Double>();
        for (int i = 0; i < in.size(); i++){
            if(in.get(i).equals(Matrix.UNKNOWN_VALUE)){
                out.add(columnCentroids.get(i));
            }
            else{
                out.add(new Double(in.get(i).doubleValue()));
            }
        }
        return out;
    }

    /**
     * Copy in to out. (In other words, this is a no-op).
     */
    @Override
    public List<Double> untransform(List<Double> in) {
        return null;
    }

    /**
     * Returns a zero-row matrix with the same column
     * meta-data as the one that was passed to train.
     */
    @Override
    public Matrix outputTemplate() {
        return template;
    }

}
