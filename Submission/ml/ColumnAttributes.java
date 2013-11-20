package ml;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the mappings between numbers and string values for a category
 * e.g. if values.get(3) returns "iris-setosa", then the fourth possible value
 * of this enumeration is "iris-setosa"
 */
public class ColumnAttributes {

    public enum ColumnType {
        CONTINUOUS,
        CATEGORICAL
    }

    private String columnName;
    private ColumnType columnType;

    private List<String> values;

     public ColumnAttributes(String columnName, ColumnType columnType) {
        this.columnName = columnName;
        this.columnType = columnType;

        if (columnType == ColumnType.CATEGORICAL) {
            values = new ArrayList<String>();
        }
    }

    /**
     * Performs a deep-copy of other
     * @param other
     */
    public ColumnAttributes(ColumnAttributes other){
        this.columnName = other.columnName;
        this.columnType = other.columnType;

        if (other.columnType == ColumnType.CATEGORICAL) {
            this.values = new ArrayList<String>();

            for(String val : other.getValues()){
                this.values.add(new String(val));
            }
        }

    }

    public void addValue(String value) {
        values.add(value);
    }

    public String getValue(int index) {
        return values.get(index);
    }

    public int getIndex(String value) {
        return values.indexOf(value);
    }

    public int size(){
        return values.size();
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public String getName() {
        return columnName;
    }

    public List<String> getValues(){
        return values;
    }

    public String toString() {
        return String.format("{ Name: %s, Type: %s, Values: %s }", columnName, columnType, values);
    }

    @Override
    public int hashCode(){
        int hash = 1;
        hash *= 17 + columnType.hashCode();
        hash *= 31 + columnName.hashCode();
        if(values!=null){
            hash *= 13 + values.hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(Object obj){
        if(obj== null)
            return false;
        if(obj == this)
            return true;
        if(!(obj instanceof ColumnAttributes))
            return false;

        ColumnAttributes rhs = (ColumnAttributes) obj;
        if(this.getColumnType() != rhs.getColumnType())
            return false;
        if(this.getValues()!= null){
            if(!this.getValues().equals(rhs.getValues())){
                return false;
            }
        }

        if(!this.getName().equals(rhs.getName()))
            return false;
        return true;
    }

}


