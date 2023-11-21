package org.example.tokens;

public class Token {
    Tag tag;
    String lexeme;
    public Token(Tag tag, String lexeme) {
        this.tag = tag;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return "<" + tag.toString() + ", " + lexeme + ">";
    }

}
