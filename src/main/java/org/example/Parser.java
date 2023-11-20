package org.example;

import org.example.tokens.Token;

import java.io.IOException;

public class Parser {
    private Lexer scanner;
    private Token lookahead;

    public Parser() throws IOException {
        scanner = new Lexer();
        scanner.scan();
    }

    public void start() {
        expr();

        if (lookahead.getTag() != NodeType.EOF) {
            StringBuilder word;
            String s = new String("Simbolo \'" + lookahead.toString() + "\' invalido");
            throw new RuntimeException();
        }
    }

    private void expr() {
    }

    private void term() {
//        if (lookahead.get == '(')
    }

    private void fact() {

    }

    private boolean match(NodeType tag) throws IOException {
        if(tag == lookahead.getTag()) {
            lookahead = scanner.scan();
            return true;
        }

        return false;
    }
}
