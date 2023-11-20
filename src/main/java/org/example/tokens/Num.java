package org.example.tokens;

import org.example.NodeType;

public class Num extends Token {
    private int value;
    public Num(int v) {
        super(NodeType.NUM);
        value = v;
    }

    @Override
    public String getTokenName() {
        return "<" + NodeType.NUM.name() + ", " + value + ">";
    }
}
