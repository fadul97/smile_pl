package org.example;

import org.example.tokens.Tag;
import org.example.tokens.Token;

public class TypeValue {
    private Token token;
    private String value;
    private String type;
    private String var;

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public TypeValue(String type, String var) {
        this.type = type;
        this.value = null;
        this.var = var;
    }
    public TypeValue() {
        this.type = null;
        this.value = null;
        this.var = null;
        this.token = null;
    }

    public TypeValue(Tag type, String lexeme, String value, String var) {
        this.token = new Token(type, lexeme);
        this.value = value;
        this.var = var;
    }

    public Tag getType() {
        return this.token.getTag();
    }

    public String getLexeme() {
        return token.getLexeme();
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TypeValue buscarElementoPorVar(String varProcurada) {
        if (varProcurada.equals(this.var)) {
            return this; // Elemento encontrado
        }
        return null; // Elemento não encontrado
    }

    public String toString() {
        return "TypeValue{" +
                "token='" + token + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", var='" + var + '\'' +
                '}';
    }
}
