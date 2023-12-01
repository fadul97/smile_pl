package org.example;

import org.example.tokens.Tag;
import org.example.tokens.Token;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import java.util.Optional;

public class Lexer {
    private int line;
    private char peek;
    private HashMap<String, Token> token_table;
    private File mainFile;
    private BufferedReader reader;
    private Token token;
    private StringBuilder dotsBuilder;
    private String outPath = "out.c";
    public List<Token> seqTokens = new ArrayList<>();
    private StringBuilder stringBuilder;
    private StringBuilder word;
    private BufferedWriter writer;
    private String content;

    public Lexer() {
        line = 1;
        peek = ' ';
        token_table = new HashMap<>();
        dotsBuilder = new StringBuilder();
        stringBuilder = new StringBuilder();
        word = new StringBuilder();
        token_table.put("TheBeginning", new Token(Tag.BEGINNING, "TheBeginning"));
        token_table.put("TheEnd", new Token(Tag.END, "TheEnd"));
        token_table.put("true", new Token(Tag.TRUE, "true"));
        token_table.put("false", new Token(Tag.FALSE, "false"));
        token_table.put("int", new Token(Tag.TYPE, "int"));
        token_table.put("for", new Token(Tag.FOR, "for"));
        token_table.put("go", new Token(Tag.GO, "go"));
        token_table.put("while", new Token(Tag.WHILE, "while"));
        token_table.put("if", new Token(Tag.IF, "if"));
        token_table.put("then", new Token(Tag.THEN, "then"));
        token_table.put("elif", new Token(Tag.ELIF, "elif"));
        token_table.put("ifnot", new Token(Tag.IFNOT, "ifnot"));
        token_table.put("scenario", new Token(Tag.SCENARIO, "scenario"));
        token_table.put("..", new Token(Tag.END_OF_EXPRESSION, ".."));
        token_table.put("...", new Token(Tag.THREE_DOT, "..."));
        token_table.put("||", new Token(Tag.OR, "||"));
        token_table.put("!", new Token(Tag.NOT, "!"));

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
            while ((character = reader.read()) != -1){
                scan((char) character);
                seqTokens.add(token);
            }

            writer = new BufferedWriter(new FileWriter(outPath));

        } catch (FileNotFoundException e) {
            System.out.println("Arquivo nao encontrado.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Erro na funcao reader.read().");
            e.printStackTrace();
        }
    }

    
    private void ignoreWhiteSpace() throws IOException {
        if (peek == '\n')
        line += 1;
        peek = (char) reader.read();
    }
    
    private Token readNumbers() throws IOException {
        // Ponto-flutuante não foi encontrado
        boolean decimal = false;
        
        // Acumula numeros
        StringBuilder number = new StringBuilder();
        do {
            if (peek == '.') {
                peek = (char) reader.read();
                // Primeiro ponto encontrado
                if (!decimal && Character.isDigit(peek)) {
                    number.append(".");
                    decimal = true;
                } else {
                    // Segundo ponto encontrado - Final da expressao, loop ou erro
                    // System.out.println("[IMPORTANTE] Segundo ponto");
                    dotsBuilder.append(".");
                    token = token_table.get(number.toString());
                    if (token == null) {
                        if (decimal)
                        token = new Token(Tag.FLOATING, number.toString());
                        else
                        token = new Token(Tag.INTEGER, number.toString());
                    }
                    // peek = last;
                    System.out.println(token.toString());
                    return token;
                }
            }
            
            number.append(peek);
            peek = (char) reader.read();
        } while (Character.isDigit(peek) || peek == '.');

        if (decimal) {
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
        do {
            word.append(peek);
            peek = (char) reader.read();
        } while (Character.isAlphabetic(peek) || Character.isDigit(peek) || peek == '_');
        
        // DEBUG: Procurar token na tabela
        Token pos = token_table.get(word.toString());
        
        // Se lexema ja estiver na tabela
        if (pos != null) {
            // Retorna o token da tabela
            token = pos;
            System.out.println(token.toString());
            word.setLength(0);
            return token;
        }
        
        // Se o lexema ainda não estiver na tabela
        Token t = new Token(Tag.VAR, word.toString());
        token_table.put(word.toString(), t);
        
        // Retorna o token VAR
        token = t;
        System.out.println(token.toString());
        word.setLength(0);
        return token;
    }
    
    private Token readOperators() throws IOException {
        switch (peek) {
            case '&':
            peek = (char) reader.read();
            if (peek == '&') {
                token = token_table.get("&&");
                    if (token == null) {
                        token = new Token(Tag.AND, "&&");
                    }
                    System.out.println(token.toString());
                    return token;
                } else {
                    // TODO: Unread letra - Talvez nao funcione
                    // TODO: Arrumar linha
                    System.out.println("[ERROR]: Caracter inesperado: '" + peek + "' na linha " + line + ".");
                    token = new Token(Tag.UNKNOWN, "&" + peek);
                    System.out.println(token.toString());
                    return token;
                }
            case '|':
            peek = (char) reader.read();
            if (peek == '|') {
                token = token_table.get("||");
                if (token == null) {
                    token = new Token(Tag.OR, "||");
                }
                System.out.println(token.toString());
                return token;
            } else {
                // TODO: Unread letra - Talvez nao funcione
                // TODO: Arrumar linha
                System.out.println("[ERROR]: Caracter inesperado: '" + peek + "' na linha " + line + ".");
                token = new Token(Tag.UNKNOWN, "|" + peek);
                System.out.println(token.toString());
                return token;
            }
            case '>':
            peek = (char) reader.read();
            if (peek == '=') {
                token = token_table.get(">=");
                if (token == null) {
                    token = new Token(Tag.GTE, ">=");
                }
                System.out.println(token.toString());
                return token;
            } else {
                // TODO: Unread letra
                // TODO: Arrumar linha
                System.out.println("[ERROR]: Caracter inesperado: '" + peek + "' na linha " + line + ".");
                token = new Token(Tag.UNKNOWN, ">" + peek);
                System.out.println(token.toString());
                return token;
            }
            case '<':
            peek = (char) reader.read();
                if (peek == '=') {
                    char last = peek;
                    peek = (char) reader.read();

                    if (peek == '>') { // verifica se é atribuição <=>
                        token = token_table.get("<=>");
                        if (token == null) {
                            token = new Token(Tag.ATTRIBUTION, "<=>");
                        }
                    } else {
                        peek = last;
                        token = token_table.get("<=");
                        if (token == null) {
                            token = new Token(Tag.LTE, "<=");
                        }
                    }
                    System.out.println(token.toString());
                    return token;
                    
                } else {
                    // TODO: Unread letra
                    // TODO: Arrumar linha
                    System.out.println("[ERROR]: Caracter inesperado: '" + peek + "' na linha " + line + ".");
                    token = new Token(Tag.UNKNOWN, "<" + peek);
                    System.out.println(token.toString());
                    return token;
                }
                case '=':
                peek = (char) reader.read();
                if (peek == '=') {
                    token = token_table.get("==");
                    if (token == null) {
                        token = new Token(Tag.EQ, "==");
                        //Comentario
                    }
                    System.out.println(token.toString());
                    return token;
                } else {
                    // TODO: Unread letra
                    // TODO: Arrumar linha
                    System.out.println("[ERROR]: Caracter inesperado: '" + peek + "' na linha " + line + ".");
                    token = new Token(Tag.UNKNOWN, "=" + peek);
                    System.out.println(token.toString());
                    return token;
                }
                // TODO: Arrumar negacao / not equal
                case '!':
                token = token_table.get("!");
                System.out.println(token.toString());
                return token;
        }
        if (token != null) {
            System.out.println(token.toString());
        }
        return token;
    }
    
    private Token readParentheses() throws IOException {
        char last = peek;
        switch (peek) {
            case '(':
            peek = (char) reader.read();
                if (peek != ':') {
                    token = token_table.get("(");
                    if (token == null) {
                        token = new Token(Tag.LEFT_PAR, "(");
                    }
                    System.out.println(token.toString());
                    stringBuilder.append(peek);

//                    peek = last; // pega o ultimo valor para não perder o valor
                    return token;
                } else {
                    token = token_table.get("(:");
                    if (token == null) {
                        token = new Token(Tag.LEFT_SMILE, "(:");
                    }
                    System.out.println(token.toString());
                    return token;
                }
            case ':':
                peek = (char) reader.read();
                if (peek != ')') {
                    token = token_table.get(")");
                    if (token == null) {
                        token = new Token(Tag.DOUBLE_DOT, ":");
                    }
                    System.out.println(token.toString());
                    
                    peek = last; // pega o ultimo valor para não perder o valor
                    return token;
                } else {
                    token = token_table.get(":)");
                    if (token == null) {
                        token = new Token(Tag.RIGHT_SMILE, ":)");
                    }
                    System.out.println(token.toString());
                    return token;
                }
            }
            
            return null;
        }
        
    private Token readDots() throws IOException {
            int i = dotsBuilder.length();
            // Enquanto for um ponto, apenas soma e adiciona na StringBuilder
            while (peek == '.') {
                i++;
                dotsBuilder.append(peek);
                peek = (char) reader.read();
            }
            // Se tiver 2 pontos = Final da expressao
            if (i == 2) {
                token = token_table.get("..");
                if (token != null)
                System.out.println(token.toString());
                return token;
                // Se tiver 3 pontos = FOR loop
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
    
    private Token readString() throws IOException {
//        StringBuilder string = new StringBuilder();

        // Adiciona " na string, se nao tiver sido adicionado
        if (stringBuilder.length() <= 0)
            stringBuilder.append(peek);

        // Le proximo caracter
        peek = (char) reader.read();
        
        // Enquanto nao for ", adiciona na string
        while (peek != '"') {
            if (peek == '{') {
                peek = (char) reader.read();

                // Ignora espacos em branco
                while (Character.isWhitespace(peek)) peek = (char) reader.read();

                readWords();

                // Procurar na tabela de variaveis
                Token t = token_table.get(token.getLexeme());
                if (t != null) {
                    System.out.println("Nao esta na tabela de variaveis.");
                } else {
                    System.out.println("Trocando variavel pelo seu valor.");
                    // TODO: Trocar
                }

                // Ignora espacos em branco
                while (Character.isWhitespace(peek)) peek = (char) reader.read();

//                System.out.println("Token encontrado: " + token.toString());
//                System.out.println("Var encontrada: " + word.toString());
                stringBuilder.append(token.getLexeme());
            } else if (peek == '}') {
                // NADA
            } else {
                stringBuilder.append(peek);
            }

            peek = (char) reader.read();
        }
        
        // Adiciona " na string
        stringBuilder.append(peek);

        // Procura por string na tabela de tokens
        token = token_table.get(stringBuilder.toString());
        if (token == null) {
            token = new Token(Tag.STRING, stringBuilder.toString());
        }

        stringBuilder.setLength(0);
        System.out.println(token.toString());
        return token;
    }
    
    public void translate() throws IOException{
        //TODO: Fazer leitura dos tokens na lista "seqTokens" e redirecionar para cada função

        for (Token element: seqTokens){

            if (element.getTag() == Tag.BEGINNING) {
                wrBeginning();
            }

            if(element.getTag() == Tag.END){
                wrTheEnd();
            }
        }
        //wrAttribution("var", "int", Optional.of("2"));
    }

    private void wrBeginning() throws IOException{

        try {
            content = "#include <stdio.h>\n\n" + 
                  "int main(int agrc, char* argv[]){\n";
            writer.write(content);

        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }
    
    private void wrAttribution(String var, String type, Optional<String> value) throws IOException{
        try {
            
            content = type + " " + var; 
            if (!value.isEmpty()) {
                content+= " = " + value.get() + ";\n";
            }else{
                content+= ";\n";
            }
            
            writer.write(content);
            
        } catch (IOException e) {
            // TODO: handle exception        
        }
    }
    
    //TODO: Criar wrFor
    //TODO: Criar wrWrite
    //TODO: Criar wrWhile
    //TODO: Criar wrWhile
    // ...
    private void wrTheEnd() throws IOException{

        try {
            content = "    return 0;\n}";
            writer.write(content);
            
            writer.close();

        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }
    
    Token scan(char ch) throws IOException {
        peek = ch;
        dotsBuilder.setLength(0);

        // Ignora espaços em branco, tabulações e novas linhas
        while (Character.isWhitespace(peek)) {
            ignoreWhiteSpace();
        }

        // TODO: Ignorar comentários --se der tempo

        // Retorna números
        if (Character.isDigit(peek)) {
            readNumbers();
        }

        // Retorna palavras-chave e identificadores
        if (Character.isAlphabetic(peek) || peek == '_') {
            readWords();
        }

        // Retorna operadores com mais de um caractere: >=, <=, == e !=
        if (peek == '<' || peek == '>' || peek == '&' || peek == '|' || peek == '!')
            readOperators();

        if (peek == '(' || peek == ')' || peek == ':') {
            readParentheses();
        }

        // Retorna string
        if (peek == '"') {
            readString();
        }

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
