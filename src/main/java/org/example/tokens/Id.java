package org.example.tokens;

import org.example.NodeType;

public class Id extends Token {
    private String name;
    public Id() {
        super(Tag.ID);
    }
    public Id(String s) {
        super(Tag.ID);
        name = s;
    }
    public Id(Tag t, String s) {
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
