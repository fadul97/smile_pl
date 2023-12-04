package org.example;

import org.example.tokens.Tag;
import org.example.tokens.Token;

public class TypeValue {
    private Token token;
    private String value;

    public TypeValue(Tag type, String lexeme, String value) {
        this.token = new Token(type, lexeme);
        this.value = value;
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
}
