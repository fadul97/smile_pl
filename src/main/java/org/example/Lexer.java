package org.example;

import org.example.tokens.Tag;
import org.example.tokens.Token;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

public class Lexer {
    private int line;
    private char peek;
    private HashMap<String, Token> token_table;
    private File mainFile;
    BufferedReader reader;
    private Token token;
    private StringBuilder dotsBuilder;

    public Lexer() {
        line = 1;
        peek = ' ';
        token_table = new HashMap<>();
        dotsBuilder = new StringBuilder();
        token_table.put("TheBeginning", new Token(Tag.BEGINNING, "TheBeginning"));
        token_table.put("TheEnd", new Token(Tag.END, "TheEnd"));
        token_table.put("true", new Token(Tag.TRUE, "true"));
        token_table.put("false", new Token(Tag.FALSE, "false"));
        token_table.put("int", new Token(Tag.TYPE, "int"));
        token_table.put("for", new Token(Tag.FOR, "for"));
        token_table.put("go", new Token(Tag.GO, "go"));
        token_table.put("while", new Token(Tag.WHILE, "while"));
        token_table.put("if", new Token(Tag.IF, "if"));
        token_table.put("scenario", new Token(Tag.SCENARIO, "scenario"));
        token_table.put("..", new Token(Tag.END_OF_EXPRESSION, ".."));
        token_table.put("...", new Token(Tag.THREE_DOT, "..."));
        token_table.put("||", new Token(Tag.OR, "||"));
        // TODO: Adicionar keywords

        // DEBUG: Printa todas as keywords
        System.out.println("===== Tabela HASH =====");
        for (String key : token_table.keySet()) {
            System.out.println("Key: " + key);
        }
        System.out.println("\n\n");
    }

    public void start() throws IOException {
        try {
            mainFile = new File("main.sml");

            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(mainFile),
                            StandardCharsets.UTF_8));
            int character;
            while ((character = reader.read()) != -1) scan((char) character);

        } catch (FileNotFoundException e) {
            System.out.println("Arquivo nao encontrado.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Erro na funcao reader.read().");
            e.printStackTrace();
        }
    }

    private void ignoreWhiteSpace() throws IOException {
        if (peek == '\n') line += 1;
        peek = (char) reader.read();
    }

    private Token readNumbers() throws IOException {
        // Ponto-flutuante não foi encontrado
        boolean dot = false;

        // Acumula numeros
        StringBuilder number = new StringBuilder();
        do {
            if (peek == '.') {
                // Primeiro ponto encontrado
                if (!dot) {
                    dot = true;
                } else {
                    // Segundo ponto encontrado - Final da expressao, loop ou erro
//                    System.out.println("[IMPORTANTE] Segundo ponto");
                    dot = false;
                    dotsBuilder.append(number.charAt(number.length() - 1));
                    number.deleteCharAt(number.length() - 1);
                    break;
                }
            }

            number.append(peek);
            peek = (char) reader.read();
        } while (Character.isDigit(peek) || peek == '.');


        if (dot) {
            token = new Token(Tag.FLOATING, number.toString());
            System.out.println(token.toString());
            return token;
        } else {
            token = new Token(Tag.INTEGER, number.toString());
            System.out.println(token.toString());
            return token;
        }
    }

    private Token readWords() throws IOException {
        StringBuilder word = new StringBuilder();

        do {
            word.append(peek);
            peek = (char) reader.read();
        } while (Character.isAlphabetic(peek));

        // DEBUG: Procurar token na tabela
        Token pos = token_table.get(word.toString());

        // Se lexema ja estiver na tabela
        if (pos != null) {
            // Retorna o token da tabela
            token = pos;
            System.out.println(token.toString());
            return token;
        }

        // Se o lexema ainda não estiver na tabela
        Token t = new Token(Tag.ID, word.toString());
        token_table.put(word.toString(), t);

        // Retorna o token ID
        token = t;
        System.out.println(token.toString());
        return token;
    }

    private Token readOperators() throws IOException {
        char next;
        switch (peek) {
            case '&':
                next = (char) reader.read();
                if (next == '&') {
                    peek = (char) reader.read();
                    token = new Token(Tag.AND, "&&");
                    System.out.println(token.toString());
                    return token;
                } else {
                    // TODO: Unread letra
//                        reader.u
                }
                break;
            case '|':
                next = (char) reader.read();
                if (next == '|') {
                    peek = (char) reader.read();
                    token = token_table.get("||");
                    if (token != null) {
                        System.out.println(token.toString());
                        return token;
                    }
                    else {
                        token = new Token(Tag.OR, "||");
                        System.out.println(token.toString());
                        return token;
                    }
                } else {
                    // TODO: Unread letra
//                        reader.u
                }
                break;
            case '>':
                next = (char) reader.read();
                if (next == '=') {
                    peek = (char) reader.read();
                    token = new Token(Tag.GTE, ">=");
                    System.out.println(token.toString());
                    return token;
                } else {
                    // TODO: Unread letra
//                        reader.u
                }
                break;
            case '<':
                next = (char) reader.read();
                if (next == '=') {
                    peek = (char) reader.read();
                    // Atribuicao
                    if (peek == '>') {
                        token = new Token(Tag.ATTRIBUTION, "<=>");
                        System.out.println(token.toString());
                        return token;
                    } else {
                        token = new Token(Tag.LTE, "<=");
                        System.out.println(token.toString());
                        return token;
                    }
                } else {
                    // TODO: Unread letra
//                        reader.u
                }
                break;
            case '=':
                next = (char) reader.read();
                if (next == '=') {
                    peek = (char) reader.read();
                    token = new Token(Tag.EQ, "==");
                    System.out.println(token.toString());
                    return token;
                } else {
                    // TODO: Unread letra
//                        reader.u
                }
                break;
            // TODO: Arrumar negacao / not equal
            case '!':
                next = (char) reader.read();
                if (next == '=') {
                    peek = (char) reader.read();
                    token = new Token(Tag.NEQ, "!=");
                    System.out.println(token.toString());
                    return token;
                } else {
                    // TODO: Unread letra
//                        reader.u
                }
                break;
        }
        if (token != null) {
            System.out.println(token.toString());
        }
        return token;
    }

    private Token readDots() throws IOException {
        int i = dotsBuilder.length();
        while (peek == '.') {
            i++;
            dotsBuilder.append(peek);
            peek = (char) reader.read();
        }


        if (i == 2) {
            token = token_table.get("..");
            if (token != null)
                System.out.println(token.toString());
            return token;
        } else if (i == 3) {
            token = token_table.get("...");
            if (token != null)
                System.out.println(token.toString());
            return token;
        }

        token = token_table.get(dotsBuilder.toString());
        if (token == null) {
            System.out.println("[ERROR: TOKEN NAO ENCONTRADO]: i(" + i + ") - " + dotsBuilder.toString());
            return new Token(Tag.UNKNOWN, dotsBuilder.toString());
        } else {
            System.out.println(token.toString());
            return token;
        }
    }

    Token scan(char ch) throws IOException {
        peek = ch;
        dotsBuilder.setLength(0);

        // Ignora espaços em branco, tabulações e novas linhas
        while (Character.isWhitespace(peek)) {
            ignoreWhiteSpace();
        }

        // TODO: Ignorar comentários

        // Retorna números
        if (Character.isDigit(peek)) {
            readNumbers();
        }

        // Retorna palavras-chave e identificadores
        if (Character.isAlphabetic(peek)) {
            readWords();
        }

        // TODO: Verificar nossos operadores
        // Retorna operadores com mais de um caractere: >=, <=, == e !=
        if (peek == '<' || peek == '>' || peek == '&' || peek == '|' || peek == '!')
            readOperators();

        // TODO: ? Retorna caracteres não alphanuméricos isolados: (, ), +, -, etc.

        // Retorna pontos
        if (peek == '.') {
            readDots();
        }

        // Retorna números
        if (Character.isDigit(peek)) {
            readNumbers();
        }

        return token;
    }
}
