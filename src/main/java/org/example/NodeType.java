package org.example;

public enum NodeType {
    NUM,
    ID,
    TRUE,
    FALSE,
    UNKNOWN,
    IDENTIFIER,
    LOG,
    REL,
    ASSIGN,
    IF_STMT,
    WHILE_STMT,
    FOR_STMT,
    EOF;

    String convertToString(NodeType type) {
        return type.name();
    }
}
