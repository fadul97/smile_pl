package org.example.tokens;

import org.example.NodeType;

public class Id extends Token {
    private String name;
    public Id() {
        super(NodeType.ID);
    }
    public Id(String s) {
        super(NodeType.ID);
        name = s;
    }
    public Id(NodeType t, String s) {
        super(t);
        name = s;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTokenName() {
        return "<" + NodeType.ID.name() + ", " + name + ">";
    }
}
