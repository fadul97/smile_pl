package org.example;

public class TypeValue {
    String type;
    String value;

    public TypeValue(String type) {
        this.type = type;
        this.value = null;
    }
    public TypeValue(String type, String value) {
        this.type = type;
        this.value = value;
    }
    public String getType() {
        return type;
    }
    public String getValue() {
        return value;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setValue(String value) {
        this.value = value;
    }
    
}
