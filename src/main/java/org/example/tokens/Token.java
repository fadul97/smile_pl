package org.example.tokens;

import org.example.NodeType;

public class Token {
    Tag tag;
    String lexeme;
    public Token(Tag tag, String lexeme) {
        this.tag = tag;
        this.lexeme = lexeme;
    }
    public Token(char ch) {
        this.tag = Tag.UNKNOWN;
        this.lexeme = String.valueOf(ch);
    }

    public Token(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String getTokenName() {
        return tag.name();
    }

    public String toString() {
        return "<" + tag.name() + ", " + lexeme + ">";
    }

}
