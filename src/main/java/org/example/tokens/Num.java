package org.example.tokens;

import org.example.NodeType;

public class Num extends Token {
    private int value;
    public Num(int v) {
        super(Tag.NUM);
        value = v;
    }

    @Override
    public String getTokenName() {
        return "<" + NodeType.NUM.name() + ", " + value + ">";
    }
}
