package org.example.tokens;

import org.example.NodeType;

public class Token {
    NodeType tag;
    public Token(NodeType tag) {
        this.tag = tag;
    }
    public NodeType getTag() {
        return tag;
    }

    public void setTag(NodeType tag) {
        this.tag = tag;
    }

    public String getTokenName() {
        return tag.name();
    }

    public String getUnknownToken(char c) {
        return "<" + tag.name() + ", " + c + ">";
    }
}
