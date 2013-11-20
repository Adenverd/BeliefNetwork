package ml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import ml.ColumnAttributes.ColumnType;

public class ARFFParser {

    private static final String ATTRIBUTE = "@attribute";
    private static final String DATA = "@data";
    private static final String RELATION = "@relation";
    private static final String COMMENT = "%";

    /**
     * Parses a ARFF file into a Matrix.
     * Only Numeric (Real) and categorical attributes are allowed.
     *
     * @param filepath
     * @throws IOException
     * @throws MLException with a detailed message if parsing fails
     */
    public static Matrix loadARFF(String filepath)
            throws IOException {

        BufferedReader in = new BufferedReader(new FileReader(filepath));
        Matrix matrix = new Matrix();
        boolean isProcessingData = false;
        String line;

        while ((line = in.readLine()) != null) {
            line = line.replaceAll("\\s+", " ").replaceAll("\\{\\s", "{").
                    replaceAll("\\s\\}", "}").replaceAll(",\\s", ",").replaceAll("\\s,", ",");
            String lineLowercase = line.toLowerCase();

            if (lineLowercase.isEmpty() || lineLowercase.startsWith(COMMENT) || lineLowercase.startsWith(RELATION)) {
                continue;
            } else if (lineLowercase.startsWith(ATTRIBUTE)) {
                getAttributes(matrix, line);
            } else if (lineLowercase.startsWith(DATA)) {
                isProcessingData = true;
            } else if (isProcessingData) {
                getData(matrix, line);
            } else {
                throw new MLException("Unrecognized file format, line: " + line);
            }
        }
        in.close();
        return matrix;
    }

    public static Matrix saveToARFF(Matrix matrix, String filepath)
            throws Exception {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Add an @attribute as a column to the matrix
     *
     * @throws MLException if attribute type is not NUMERIC, REAL, or categorical
     */
    private static void getAttributes(Matrix matrix, String line) {
        line = line.toLowerCase();
        String name, type;
        int ind = -1;

        if (line.contains("{") && line.contains("}")) {
            ind = line.indexOf('{');
        } else {
            ind = line.lastIndexOf("integer");
            if (ind == -1)
                ind = line.lastIndexOf("real");
            if (ind == -1)
                ind = line.lastIndexOf("numeric");
            if (ind == -1)
                ind = line.lastIndexOf("continuous");
            if (ind == -1)
                throw new MLException("Can't parse this:" + line);
        }

        name = line.substring(11, ind - 1).trim();
        type = line.substring(ind).trim();

        if (isNumeric(type)) {
            ColumnAttributes column = new ColumnAttributes(name, ColumnType.CONTINUOUS);
            matrix.addColumn(column);
        } else if (type.startsWith("{") && type.endsWith("}")) {
            ColumnAttributes column = new ColumnAttributes(name, ColumnType.CATEGORICAL);
            String[] values = type.substring(1, type.length() - 1).split(",");
            for (int i = 0; i < values.length; i++) {
                column.addValue(values[i].trim());
            }
            matrix.addColumn(column);
        } else {
            throw new MLException("Unrecognized attribute type: " + type);
        }
    }

    /**
     * Adds a row of data to the matrix
     *
     * @throws MLException if a value of a categorical column is not found
     *                     or if the number of columns doesn't match the number
     *                     of columns in the matrix
     */
    private static void getData(Matrix matrix, String line) {
        String[] cols = line.toLowerCase().split(",");
        if (matrix.getNumCols() != cols.length) {
            throw new MLException(String.format(
                    "Number of columns mismatch. Expected: %d, Got: %d", matrix.getNumCols(), cols.length));
        }

        List<Double> row = new ArrayList<Double>();
        for (int i = 0; i < cols.length; i++) {
            String val = cols[i].trim();
            if (val.equals("?")) {
                row.add(Matrix.UNKNOWN_VALUE);
            } else if (matrix.isContinuous(i)) {
                row.add(Double.valueOf(val));
            } else {
                ColumnAttributes column = matrix.getColumnAttributes(i);
                int valueIndex = column.getIndex(val);

                if (valueIndex < 0) {
                    throw new MLException("Value index not found: " + cols[i]);
                }

                row.add((double) valueIndex);
            }
        }
        matrix.addRow(row);
    }

    private static boolean isNumeric(String attr) {
        attr = attr.toLowerCase();
        return attr.equals("integer") || attr.equals("numeric")
                || attr.equals("real") || attr.equals("continuous");
    }
}
