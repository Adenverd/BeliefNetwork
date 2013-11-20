package ml;

import java.util.*;
import ml.ColumnAttributes.ColumnType;
import static java.lang.Math.sqrt;

public class Matrix {

    public static final Double UNKNOWN_VALUE = Double.NEGATIVE_INFINITY;

    // Data
    private List<List<Double>> data;

    // Maps column index to categorical attributes
    private List<ColumnAttributes> columnAttributes;

    /**
     * Creates an empty matrix
     */
    public Matrix() {
        data = new ArrayList<List<Double>>();
        columnAttributes = new ArrayList<ColumnAttributes>();
    }

    /**
     * Performs a deep-copy of the other Matrix
     *
     * @param other other Matrix
     */
    public Matrix(Matrix other) {
        this(other, false);
    }

    /**
     * Creates a new Matrix with the other Matrix column attributes only
     *
     * @param other
     * @param isAttributesOnly
     */
    public Matrix(Matrix other, boolean isAttributesOnly) {
        data = new ArrayList<List<Double>>();
        columnAttributes = new ArrayList<ColumnAttributes>();

        for (ColumnAttributes attr : other.columnAttributes) {
            columnAttributes.add(new ColumnAttributes(attr));
        }

        if (!isAttributesOnly) {
            for (List<Double> row : other.data) {
                data.add(new ArrayList<Double>(row));
            }
        }
    }

    /**
     * Returns the number of columns in the matrix
     */
    public int getNumCols() {
        return columnAttributes.size();
    }

    /**
     * Returns the number of rows in the matrix
     */
    public int getNumRows() {
        return data.size();
    }

    /**
     * Appends a row to the end of the matrix
     */
    public void addRow(List<Double> row) {
        if (row.size() != getNumCols()) {
            throw new MLException(String.format(
                    "Cannot add a row with mismatching number of columns to matrix. Expected: %d, Got: %d",
                    getNumCols(), row.size()));
        }
        for (Double value : row) {
            if (value == null) {
                throw new MLException("Cannot add a row with a null Double value");
            }
        }
        data.add(row);
    }

    /**
     * Adds a new column to the matrix (throws if matrix is not empty)
     */
    public void addColumn(ColumnAttributes attributes) {
        if (!data.isEmpty()) {
            throw new MLException("Cannot add a column to a matrix that contains rows");
        }
        columnAttributes.add(attributes);
    }

    /**
     * Swaps the locations of two rows in the matrix
     */
    public void swapRows(int row1, int row2) {
        List<Double> tempRow = getRow(row1);
        data.set(row1, data.get(row2));
        data.set(row2, tempRow);
    }

    /**
     * Returns the row at index rowNum in the matrix
     */
    public List<Double> getRow(int row) {
        return data.get(row);
    }

    /**
     * Returns the attributes of column col
     */
    public ColumnAttributes getColumnAttributes(int col) {
        return columnAttributes.get(col);
    }

    public int getColumnIndex(ColumnAttributes colAttributes){
//        if(!columnAttributes.contains(colAttributes)){
//            throw new MLException("This matrix does not contain a column with these column attributes");
//        }
        Integer index = columnAttributes.indexOf(colAttributes);
        if(index.equals(-1)){
            throw new MLException("This matrix does not contain a column with these column attributes");
        }

        return index;
    }

    /**
     * Returns the type of column col
     *
     * @param col
     */
    public ColumnType getColumnType(int col) {
        return columnAttributes.get(col).getColumnType();
    }

    /**
     * Returns true if column col is of type ColumnType.CATEGORICAL, false otherwise
     */
    public boolean isCategorical(int col) {
        return getColumnType(col) == ColumnType.CATEGORICAL;
    }

    /**
     * Returns true if column col is of type ColumnType.CONTINUOUS, false otherwise
     */
    public boolean isContinuous(int col) {
        return getColumnType(col) == ColumnType.CONTINUOUS;
    }

    /**
     * Returns the number of rows whose value at column col is value.
     * @param col
     * @param value
     * @return
     */
    public int columnValueCount(int col, double value){
        int count = 0;
        for(List<Double> row : data){
            if (row.get(col).equals(value)){
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of rows whose value at the column indicated by colAttributes is value.
     * @param value
     * @return
     */
    public int columnValueCount(ColumnAttributes colAtrributes, double value){
        return columnValueCount(getColumnIndex(colAtrributes), value);
    }

    public Double columnMean(int col) {
        if (!isContinuous(col)) {
            throw new MLException("Cannot calculate the mean of a non-continuous column");
        }
        double sum = 0.0;
        int count = 0;
        for (List<Double> row : data) {
            double val = row.get(col);
            if (val != UNKNOWN_VALUE) {
                sum += val;
                count++;
            }
        }
        return sum / count;
    }

    public Double columnVariance(int col){
        double sumOfSquares = 0.0;
        for(List<Double> row : data){
            sumOfSquares += Math.pow(row.get(col), 2);
        }
        return sumOfSquares/getNumRows();
    }

    /**
     * Returns the min elements in the specified column
     * (Elements with the value UNKNOWN_VALUE are ignored.)
     * If no elements in a column have a value, returns
     * UNKNOWN_VALUE
     */
    public Double columnMin(int col) {
        if (!isContinuous(col)) {
            throw new MLException("Cannot calculate the min of a non-continuous column");
        }
        boolean allUnknownValues = true;
        Double min = Double.MAX_VALUE;
        for (List<Double> row : data) {
            double val = row.get(col);
            if (val != UNKNOWN_VALUE) {
                allUnknownValues = false;
                min = min < val ? min : val;
            }
        }
        if (allUnknownValues) {
            return UNKNOWN_VALUE;
        }
        return min;
    }

    /**
     * Returns the max elements in the specified column.
     * (Elements with the value UNKNOWN_VALUE are ignored.)
     * If no element in the column has a value, returns
     * UNKNOWN_VALUE.
     *
     * @param col
     */
    public Double columnMax(int col) {
        if (!isContinuous(col)) {
            throw new MLException("Cannot calculate the max of a non-continuous column");
        }
        boolean allUnknownValues = true;
        Double max = Double.MIN_VALUE;
        for (List<Double> row : data) {
            double val = row.get(col);
            if (val != UNKNOWN_VALUE) {
                allUnknownValues = false;
                max = max > val ? max : val;
            }
        }
        if (allUnknownValues) {
            return UNKNOWN_VALUE;
        }
        return max;
    }

    /**
     * Removes a section of the matrix.
     *
     * @param startRow Start index of section to remove, inclusive.
     * @param endRow   End index of section to remove, exclusive.
     */
    public void removeSubMatrix(int startRow, int endRow) {
        for (int i = 0; i < (endRow - startRow); i++) {
            data.remove(startRow);
        }
    }

    /**
     * Removes a fold from the matrix, returning the sub-Matrix that was removed. Useful for n-fold
     * cross-validation.
     *
     * @param startRow Start index of fold to remove, inclusive.
     * @param endRow   End index of fold to remove, exclusive.
     */
    public Matrix removeFold(int startRow, int endRow) {
        Matrix subMatrix = subMatrixRows(startRow, endRow);
        this.removeSubMatrix(startRow, endRow);
        return subMatrix;
    }

    /**
     * Returns the most common value in the specified column.
     * (Elements with the value UNKNOWN_VALUE are ignored.)
     * If all elements are UNKNOWN_VALUE, returns UNKNOWN_VALUE.
     */
    public Double mostCommonValue(int col) {
        boolean allUnknownValues = true;
        Map<Double, Integer> frequencies = new HashMap<Double, Integer>();
        for (List<Double> row : data) {
            double val = row.get(col);

            if (val != UNKNOWN_VALUE) {
                allUnknownValues = false;
                if (frequencies.containsKey(val)) {
                    frequencies.put(val, frequencies.get(val) + 1);
                } else {
                    frequencies.put(val, 1);
                }
            }
        }

        if (allUnknownValues) {
            return UNKNOWN_VALUE;
        }

        Set<Double> keySet = frequencies.keySet();
        Double mostCommonValue = UNKNOWN_VALUE;
        int highestFrequency = 0;

        for (Double key : keySet) {
            int val = frequencies.get(key);
            if (val > highestFrequency) {
                mostCommonValue = key;
                highestFrequency = val;
            }
        }
        return mostCommonValue;
    }

    /**
     * Returns a section of the matrix as a new matrix, with the same column attributes.
     * Note: This operation does a deep-copy and is expensive.
     *
     * @param startRowIndex Start index row, inclusive.
     * @param endRowIndex   End index row, exclusive.
     */
    public Matrix subMatrixRows(int startRowIndex, int endRowIndex) {
        return subMatrix(startRowIndex, endRowIndex, 0, getNumCols());
    }

    /**
     * Returns a section of the matrix as a new matrix, with the row values.
     * Note: This operation does a deep-copy and is expensive.
     *
     * @param startColIndex Start index column, inclusive.
     * @param endColIndex   End index column, exclusive.
     */
    public Matrix subMatrixCols(int startColIndex, int endColIndex) {
        return subMatrix(0, getNumRows(), startColIndex, endColIndex);
    }

    /**
     * Returns a section of the matrix as a new matrix
     * Note: This operation does a deep-copy and is expensive.
     *
     * @param startRowIndex Start index row, inclusive.
     * @param endRowIndex   End index row, exclusive.
     * @param startColIndex Start index, inclusive.
     * @param endColIndex   End index, exclusive.
     */
    public Matrix subMatrix(int startRowIndex, int endRowIndex, int startColIndex, int endColIndex) {

        if (startRowIndex < 0 || endRowIndex > getNumRows()
                || startColIndex < 0 || endColIndex > getNumCols()) {
            throw new IndexOutOfBoundsException("Sub-matrix index out of range");
        }

        Matrix subMatrix = new Matrix();

        for (int i = startColIndex; i < endColIndex; i++) {
            ColumnAttributes columnAttributes = getColumnAttributes(i);
            subMatrix.addColumn(new ColumnAttributes(columnAttributes));
        }

        for (int i = startRowIndex; i < endRowIndex; i++) {
            List<Double> row = getRow(i);
            List<Double> newRow = new ArrayList<Double>();
            for (int j = startColIndex; j < endColIndex; j++) {
                newRow.add(row.get(j));
            }
            subMatrix.addRow(newRow);
        }
        return subMatrix;
    }

    /**
     * Randomly shuffles the rows of the matrix
     */
//    public void shuffle() {
//        Collections.shuffle(data);
//    }

    /**
     * Shuffles the rows with the given random seed
     */
    public Matrix shuffle(Random rand) {
        Matrix newMatrix = this.subMatrixRows(0, this.getNumRows()); //create a shallow copy of this matrix
        Collections.shuffle(newMatrix.getData(), rand);
        return newMatrix;
    }

    /**
     * Checks if a column is homogeneous
     * @return true if all values in the given column are the same
     */
    public boolean isHomogeneous(int col) {
        double val = getRow(0).get(col);
        for (int i = 1; i < getNumRows(); i++) {
            if (val != getRow(i).get(col)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the deviation for the given column
     * dev = sqrt(variance)
     */
    public double deviation(int col) {
        return sqrt(variance(col));
    }

    /**
     * Calculates the variance for of the given column
     * var = sum[(vali - mean)^2]  / (n-1)
     */
    public double variance(int col) {
        int n = 0;
        double mean = columnMean(col);
        double sum = 0;
        for (List<Double> row : data) {
            double val = row.get(col) - mean;
            if (val != UNKNOWN_VALUE) {
                sum += (val * val);
                n++;
            }
        }
        return n == 1 ? 0 : sum / (n - 1);
    }

    /**
     * For debugging purposes
     */
    public void printMatrix() {

        // Column attributes stuff
        System.out.println(columnAttributes);
        System.out.println();

        // Column names
        for (int i = 0; i < getNumCols(); i++) {
            System.out.printf("%15s", columnAttributes.get(i).getName());
        }
        System.out.println();

        // Data
        for (int i = 0; i < getNumRows(); i++) {
            List<Double> row = getRow(i);
            for (int j = 0; j < getNumCols(); j++) {
                System.out.printf("%15s ", row.get(j));
            }
            System.out.println();
        }
    }

    public void clearData(){
        this.data = new ArrayList<List<Double>>();
    }

    //GETTERS AND SETTERS

    /**
     * Gets the list of this Matrix's column attributes.
     * @return
     */
    public List<ColumnAttributes> getColumnAttributes() {
        return columnAttributes;
    }

    public void setColumnAttributes(List<ColumnAttributes> columnAttributes) {
        this.columnAttributes = columnAttributes;
    }

    public List<List<Double>> getData() {
        return data;
    }

    public void setData(List<List<Double>> data) {
        this.data = data;
    }
}
