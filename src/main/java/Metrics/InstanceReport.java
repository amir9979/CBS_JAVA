package Metrics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to collect metrics about a single run of a single instance. It is strongly coupled with the
 * {@link Metrics.S_Statistics} class.
 * Will only allow one occurrence of a field name. Meaning there can'y be a String field and an Integer field with the
 * same name.
 */
public class InstanceReport {
    private Map<String, String> stringFields = new HashMap<String, String>();
    private Map<String, Integer> integerFields = new HashMap<String, Integer>();
    private Map<String, Float> floatFields = new HashMap<String, Float>();
    private boolean isCommited = false;

    /**
     * Contains constants representing standard names for fields. It is optional to use these names, and any other field
     * names would also be accepted by this class.
     */
    public static class StandardFields{
        public final static String experimentName = "Experiment Name";
        public final static String instanceName = "Instance Name";
        public final static String instanceID = "Instance ID";
        public final static String expandedNodes = "Expanded Nodes";
        public final static String generatedNodes = "Generated Nodes";
        public final static String startTime = "Start Time";
        public final static String endTime = "End Time";
        public final static String elapsedTime = "Elapsed Time";
        public final static String solved = "Solved";
        public final static String timeout = "Timeout";
    }

    /**
     * Stores the value to the given field. If the field (fieldName) already exists, but with a different type,
     * does nothing.
     * @param fieldName the name of the field. @NotNull
     * @param fieldValue the value to associate with the field. @NotNull
     * @return the old value of the field, or null if it didn't exist. also returns null if the field (fieldName) already exists, but with a different type.
     */
    public String putStingValue(String fieldName, String fieldValue){
        if(!canPutToMap(fieldName, stringFields)) {return null;}
        return this.stringFields.put(fieldName, fieldValue);
    }

    /**
     * @param fieldName the name of the field. @NotNull
     * @return the value of the field, or null if it doesn't exist for this type.
     */
    public String getStringValue(String fieldName){
        return this.stringFields.get(fieldName);
    }

    /**
     * Stores the value to the given field. If the field (fieldName) already exists, but with a different type,
     * does nothing.
     * @param fieldName the name of the field. @NotNull
     * @param fieldValue the value to associate with the field. @NotNull
     * @return the old value of the field, or null if it didn't exist. also returns null if the field (fieldName) already exists, but with a different type.
     */
    public Integer putIntegerValue(String fieldName, int fieldValue){
        if(!canPutToMap(fieldName, integerFields)) {return null;}
        return this.integerFields.put(fieldName, fieldValue);
    }


    /**
     * @param fieldName the name of the field. @NotNull
     * @return the value of the field, or null if it doesn't exist for this type.
     */
    public Integer getIntegerValue(String fieldName){
        return this.integerFields.get(fieldName);
    }

    /**
     * Stores the value to the given field. If the field (fieldName) already exists, but with a different type,
     * does nothing.
     * @param fieldName the name of the field. @NotNull
     * @param fieldValue the value to associate with the field. @NotNull
     * @return the old value of the field, or null if it didn't exist. also returns null if the field (fieldName) already exists, but with a different type.
     */
    public Float putFloatValue(String fieldName, float fieldValue){
        if(!canPutToMap(fieldName, floatFields)) {return null;}
        return this.floatFields.put(fieldName, fieldValue);
    }


    /**
     * @param fieldName the name of the field. @NotNull
     * @return the value of the field, or null if it doesn't exist for this type.
     */
    public Float getFloatValue(String fieldName){
        return this.floatFields.get(fieldName);
    }

    /**
     * Adds the value to the currently held value. if the field already exists with another associated type, does
     * nothing. If the field doesn't exist yet, saves it with addToValue as its value.
     * @param fieldName the field to perform an addition on.
     * @param addToValue the value to add (addition) to the current value of the field.
     * @return the new value for the field, or null if the field is associated with a different type.
     */
    public Integer integerAddition(String fieldName, int addToValue){
        if(!canPutToMap(fieldName, integerFields)) {return null;}
        Integer original = this.integerFields.get(fieldName);
        Integer newValue = (original == null ? 0 : original) + addToValue;
        return this.integerFields.put(fieldName, newValue);
    }

    /**
     * Multiplies the value with the currently held value. if the field already exists with another associated type, does
     * nothing. If the field doesn't exist yet, saves it with 0 as its value.
     * @param fieldName the field to perform an multiplication on.
     * @param multiplyValueWith the value by which to multiply the current value of the field.
     * @return the new value for the field, or null if the field is associated with a different type.
     */
    public Integer integerMultiplication(String fieldName, int multiplyValueWith){
        if(!canPutToMap(fieldName, integerFields)) {return null;}
        Integer original = this.integerFields.get(fieldName);
        Integer newValue = (original == null ? 0 : original) * multiplyValueWith;
        return this.integerFields.put(fieldName, newValue);
    }

    /**
     * Adds the value to the currently held value. if the field already exists with another associated type, does
     * nothing. If the field doesn't exist yet, saves it with addToValue as its value.
     * @param fieldName the field to perform an addition on.
     * @param addToValue the value to add (addition) to the current value of the field.
     * @return the new value for the field, or null if the field is associated with a different type.
     */
    public Float floatAddition(String fieldName, float addToValue){
        if(!canPutToMap(fieldName, floatFields)) {return null;}
        Float original = this.floatFields.get(fieldName);
        Float newValue = (original == null ? 0 : original) + addToValue;
        return this.floatFields.put(fieldName, newValue);
    }

    /**
     * Multiplies the value with the currently held value. if the field already exists with another associated type, does
     * nothing. If the field doesn't exist yet, saves it with 0 as its value.
     * @param fieldName the field to perform an multiplication on.
     * @param multiplyValueWith the value by which to multiply the current value of the field.
     * @return the new value for the field, or null if the field is associated with a different type.
     */
    public Float floatMultiplication(String fieldName, float multiplyValueWith){
        if(!canPutToMap(fieldName, floatFields)) {return null;}
        Float original = this.floatFields.get(fieldName);
        Float newValue = (original == null ? 0 : original) * multiplyValueWith;
        return this.floatFields.put(fieldName, newValue);
    }

    /**
     * @param fieldName the field to check.
     * @return true if the report has a value for the given field name.
     */
    public boolean hasField(String fieldName){
        return stringFields.containsKey(fieldName) || integerFields.containsKey(fieldName)
                || floatFields.containsKey(fieldName);
    }

    /**
     * a field can be put to a map if it already contains it, or if no other map contains it.
     * @param fieldName
     * @param map the map that we may want to add/update the field to/in.
     * @return true if the map contains, ot may can contain the field name (key).
     */
    private boolean canPutToMap(String fieldName, Map map){
        if(map.containsKey(fieldName)){
            return true;
        }
        else{
            return hasField(fieldName);
        }
    }

    /**
     * returns a value associated with the field, if one exists, else returns null.
     * @param fieldName the name of the field.
     * @return a value associated with the field, if one exists, else returns null.
     */
    String getValue(String fieldName){
        Object value = this.getStringValue(fieldName);
        if(value == null){
            value = this.getIntegerValue(fieldName);
            if(value == null){
                value = this.getFloatValue((fieldName));
            }
        }
        return value != null ? value.toString() : null;
    }

    @Override
    public String toString() {
        return "InstanceReport{" +
                "stringFields=" + stringFields +
                ", integerFields=" + integerFields +
                ", floatFields=" + floatFields +
                '}';
    }

    /**
     * Commits the report, signaling to the {@link Metrics.S_Statistics} class that the report is final and that it
     * can output the report to its output streams. An instance of this class can only be committed one. Repeated calls
     * to this method will have no effect.
     * @return true if this is the first call to this method on this instance, else false.
     */
    public boolean commit() throws IOException {
        if(isCommited){
            return false;
        }
        else{
            S_Statistics.commit(this);
            isCommited = true;
            return true;
        }
    }

}
