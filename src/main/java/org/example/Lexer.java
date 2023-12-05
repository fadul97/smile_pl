package org.example;

import org.example.tokens.Tag;
import org.example.tokens.Token;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Iterator;

import java.util.Optional;

public class Lexer {
    private int line;
    private char peek;
    private HashMap<String, Token> token_table;
    public List<TypeValue> varList = new ArrayList<>();
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
    private Token element;
    private static char forVar = 'a';

    public Lexer() {
        line = 1;
        peek = ' ';
        token_table = new HashMap<>();
        dotsBuilder = new StringBuilder();
        stringBuilder = new StringBuilder();
        word = new StringBuilder();
        element = new Token(Tag.UNKNOWN, "");
        token_table.put("TheBeginning", new Token(Tag.BEGINNING, "TheBeginning"));
        token_table.put("TheEnd", new Token(Tag.END, "TheEnd"));
        token_table.put("true", new Token(Tag.TRUE, "true"));
        token_table.put("false", new Token(Tag.FALSE, "false"));
        token_table.put("int", new Token(Tag.INTEGER, "int"));
        token_table.put("float", new Token(Tag.FLOATING, "float"));
        token_table.put("string", new Token(Tag.STRING, "string"));
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
        token_table.put("write", new Token(Tag.WRITE, "write"));
        token_table.put("read", new Token(Tag.READ, "read"));
        token_table.put("is", new Token(Tag.IS, "is"));
        token_table.put("-", new Token(Tag.MINUS, "-"));
        token_table.put("+", new Token(Tag.PLUS, "+"));
        token_table.put("*", new Token(Tag.MULTIPLY, "*"));
        token_table.put("/", new Token(Tag.DIVISION, "/"));
        token_table.put("(", new Token(Tag.LEFT_PAR, "("));
        token_table.put(")", new Token(Tag.RIGHT_PAR, ")"));

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
//                seqTokens.add(token);
            }
            
            System.out.println("\n\n");
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
                        if (decimal) {
                            token = new Token(Tag.FLOATING, number.toString());
                            seqTokens.add(token);
                            return token;
                        } else {
                            token = new Token(Tag.INTEGER, number.toString());
                            System.out.println("print aqui" + token.toString());
                            seqTokens.add(token);
                            return token;
                        }
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
            seqTokens.add(token);
            return token;
        } else {
            token = new Token(Tag.INTEGER, number.toString());
            System.out.println("print aqui" + token.toString());
            seqTokens.add(token);
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
            seqTokens.add(token);
            word.setLength(0);
            return token;
        }
        
        // Se o lexema ainda não estiver na tabela
        Token t = new Token(Tag.VAR, word.toString());
        token_table.put(word.toString(), t);

        // Retorna o token VAR
        token = t;
        System.out.println(token.toString());
        seqTokens.add(token);
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
                    seqTokens.add(token);
                    return token;
                } else {
                    // TODO: Unread letra - Talvez nao funcione
                    // TODO: Arrumar linha
                    System.out.println("[ERROR]: Caracter inesperado: '" + peek + "' na linha " + line + ".");
                    token = new Token(Tag.UNKNOWN, "&" + peek);
                    System.out.println(token.toString());
                    seqTokens.add(token);
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
                seqTokens.add(token);
                return token;
            } else {
                // TODO: Unread letra - Talvez nao funcione
                // TODO: Arrumar linha
                System.out.println("[ERROR]: Caracter inesperado: '" + peek + "' na linha " + line + ".");
                token = new Token(Tag.UNKNOWN, "|" + peek);
                System.out.println(token.toString());
                seqTokens.add(token);
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
                seqTokens.add(token);
                return token;
            } else {
                // TODO: Unread letra
                // TODO: Arrumar linha
                System.out.println("[ERROR]: Caracter inesperado: '" + peek + "' na linha " + line + ".");
                token = new Token(Tag.UNKNOWN, ">" + peek);
                System.out.println(token.toString());
                seqTokens.add(token);
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
                    seqTokens.add(token);
                    return token;
                    
                } else {
                    // TODO: Unread letra
                    // TODO: Arrumar linha
                    System.out.println("[ERROR]: Caracter inesperado: '" + peek + "' na linha " + line + ".");
                    token = new Token(Tag.UNKNOWN, "<" + peek);
                    System.out.println(token.toString());
                    seqTokens.add(token);
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
                    seqTokens.add(token);
                    return token;
                } else {
                    // TODO: Unread letra
                    // TODO: Arrumar linha
                    System.out.println("[ERROR]: Caracter inesperado: '" + peek + "' na linha " + line + ".");
                    token = new Token(Tag.UNKNOWN, "=" + peek);
                    System.out.println(token.toString());
                    seqTokens.add(token);
                    return token;
                }
                // TODO: Arrumar negacao / not equal
                case '!':
                token = token_table.get("!");
                System.out.println(token.toString());
                seqTokens.add(token);
                return token;
        }
        if (token != null) {
            System.out.println(token.toString());
        }
        return token;
    }
    
    private Token readMath() throws IOException{
        switch (peek) {
            case '+':
                token = token_table.get("+");
                if (token == null) {
                    token = new Token(Tag.PLUS, "+");
                }
                System.out.println(token.toString());
                seqTokens.add(token);

                return token;
            case '-':
                token = token_table.get("-");
                if (token == null) {
                    token = new Token(Tag.MINUS, "-");
                }
                System.out.println(token.toString());
                seqTokens.add(token);

                return token;
            case '/':
                token = token_table.get("/");
                if (token == null) {
                    token = new Token(Tag.DIVISION, "/");
                }
                System.out.println(token.toString());
                seqTokens.add(token);

                return token;
            case '*': 
                token = token_table.get("*");
                if (token == null) {
                    token = new Token(Tag.MULTIPLY, "*");
                }
                System.out.println(token.toString());
                seqTokens.add(token);

                return token;
        }

        return null;
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
                    seqTokens.add(token);
                    stringBuilder.append(peek);
                    System.out.println("StringBuilder: " + stringBuilder.toString());

//                    peek = last; // pega o ultimo valor para não perder o valor
                    return token;
                } else {
                    token = token_table.get("(:");
                    if (token == null) {
                        token = new Token(Tag.LEFT_SMILE, "(:");
                    }
                    System.out.println(token.toString());
                    seqTokens.add(token);
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
                    seqTokens.add(token);
                    
                    peek = last; // pega o ultimo valor para não perder o valor
                    return token;
                } else {
                    token = token_table.get(":)");
                    if (token == null) {
                        token = new Token(Tag.RIGHT_SMILE, ":)");
                    }
                    System.out.println(token.toString());
                    seqTokens.add(token);
                    return token;
                }
            case ')':
                token = token_table.get(")");
                if (token != null) {
                    System.out.println(token.toString());
                    seqTokens.add(token);
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
                if (token != null) {
                    System.out.println(token.toString());
                    seqTokens.add(token);
                }
                return token;
                // Se tiver 3 pontos = FOR loop
        } else if (i == 3) {
            token = token_table.get("...");
            if (token != null) {
                System.out.println(token.toString());
                seqTokens.add(token);
            }
            return token;
        }

        token = token_table.get(dotsBuilder.toString());
        if (token == null) {
            System.out.println("[ERROR: TOKEN NAO ENCONTRADO]: i(" + i + ") - " + dotsBuilder.toString());
            return new Token(Tag.UNKNOWN, dotsBuilder.toString());
        } else {
            System.out.println(token.toString());
            seqTokens.add(token);
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

        System.out.println("stringBuilder antes do setLenght(0): " + stringBuilder.toString());
        stringBuilder.setLength(0);
        System.out.println(token.toString());
        seqTokens.add(token);
        return token;
    }
    
    public void translate() throws IOException{
        //TODO: Fazer leitura dos tokens na lista "seqTokens" e redirecionar para cada função
        // DEBUG
        System.out.println("\n\n\n========== TRADUZINDO ==========");
        for (Token t : seqTokens) {
            System.out.println(t.toString());
        }

        System.out.println("Acabou de printar a traducao");

        Iterator<Token> iterator = seqTokens.iterator();
        while (iterator.hasNext()){

            element = iterator.next();

            if (element.getTag() == Tag.BEGINNING) {
                wrBeginning();
            }
            //TODO: Fazer a verificação dos tokens
            if (element.getTag() == Tag.VAR) {
                wrAttribution(element, iterator);
            }

            if (element.getTag() == Tag.FOR) {
                wrForLoop(iterator);
//                System.out.println("Fora da funcao: " + element.toString());
            }

            if (element.getTag() == Tag.WHILE) {
                wrWhileLoop(iterator);
            }

            if (element.getTag() == Tag.IF) {
                wrIf(iterator);
            }

            if (element.getTag() == Tag.ELIF) {
                wrElif(iterator);
            }

            if (element.getTag() == Tag.IFNOT) {
                wrIfnot(iterator);
            }

            if (element.getTag() == Tag.WRITE) {
                wrWrite(iterator);
            }

            if (element.getTag() == Tag.READ) {
                wrRead(iterator);
            }

            if(element.getTag() == Tag.END){
                wrTheEnd();
            }
        }
        
        for (TypeValue elemento : varList) {
            System.out.println(elemento.toString());
        }

    }

    private void wrWrite(Iterator<Token> iterator) throws IOException{
        try{
            writer.write("    printf(");

            // Ignora '('
            element = iterator.next();
            System.out.println("Ignorando: " + element.toString());

            String v = new String("");

            element = iterator.next();

            String a = "\n";
            System.out.println("Tamanho de a: " + a.length());
            System.out.println("a: ");

            boolean str = false;
            if (element.getLexeme().length() > 0) {
                if (element.getLexeme().charAt(0) == '"') {
                    v = v.concat(element.getLexeme());
                    str = true;
                } else {
                    if (element.getLexeme().charAt(0) == '{' && element.getLexeme().length() > 1) {
                        v = v.concat(element.getLexeme().substring(1, element.getLexeme().length()));
                    }
                    System.out.println("Lexeme: " + element.getLexeme());
                    System.out.println("Posicao 0: " + element.getLexeme().charAt(0));
//                    v = v.concat("\"");
//                    writer.write("\"");
//                    System.out.println("Nao eh uma string : " + element.getLexeme());
//                    v = v.concat(element.getLexeme());
//                    System.out.println("V atual: " + v);
                }
            }

            if (str) {
                writer.write(v + ");\n");
                System.out.println("Escrevi v no printf: " + v);
            } else {
                // Nao eh uma string
                if (element.getTag() == Tag.VAR) {
                    String varName = element.getLexeme();
                    TypeValue t = null;
                    System.out.println("\n\nProcurando " + varName + " na lista!");
                    for (TypeValue elemento : varList) {
                        if (elemento.getVar().equals(varName)) {
                            System.out.println("Achei na lista! " + varName);
                            t = elemento;
                            break;
                        }
                    }

                    System.out.println("T eh nulo? " + t);
                    if (t != null) {
                        // TODO: Escrever variavel no printf
//                        System.out.println("Tipo da variavel: " + t.getType());
                        Tag novaTag = t.getType();
                        System.out.println("PRINTANDO AQUI" + novaTag.toString());


                        switch (t.getType()) {
                            case FLOATING:
                                v = v.substring(0, 0);
                                v = v.concat("\"%f\", " + t.getVar());
                                break;
                            case INTEGER:
                                v = v.substring(0, 0);
                                v = v.concat("\"%d\", " + t.getVar());
                                break;
                            case STRING:
                                v = v.substring(0, 0);
                                v = v.concat("\"%s\", " + t.getVar());
                                break;
                            default:
                                // nao faz nada
                                System.out.println("[ERROR]: NAO FIZ NADA");
                                break;
                        }
                        
                    } else {
                        System.out.println("TypeValue eh null");
                    }
                }

                System.out.println("V para escrever no printf: " + v);
                System.out.println("Escrevendo \") no out.c");

//                if (v.charAt(v.length()) != '"') {
//                    v = v.concat("\"");
//                }

                writer.write(v + ");\n");
            }


        } catch (Exception e) {
            //  TODO: handle exception
        }
    }

    private void wrRead(Iterator<Token> iterator) throws IOException {
        try {
            String cont = new String();
            cont = cont.concat("    scanf(\"");

            // Ignora '('
            element = iterator.next();

            element = iterator.next();
            System.out.println("Encontrei depois de '(': " + element.toString());

            String v = new String("");
            if (element.getTag() == Tag.VAR) {
                String varName = element.getLexeme();
                TypeValue t = null;
                System.out.println("\n\nProcurando " + varName + " na lista!");
                for (TypeValue elemento : varList) {
                    if (elemento.getVar().equals(varName)) {
                        System.out.println("Achei na lista! " + varName);
                        t = elemento;
                        break;
                    }
                }

                if (t != null) {
                    switch (t.getType()) {
                        case INTEGER:
                            cont = cont.concat("%d\", &" + element.getLexeme());
                            break;
                        case FLOATING:
                            cont = cont.concat("%f\", &" + element.getLexeme());
                            break;
                        case STRING:
                            cont = cont.concat("%s\", " + element.getLexeme());
                            break;
                        default:
                            System.out.println("Unknown type");
                            cont = cont.concat("UNKNOWN\", &" + element.getLexeme());
                            break;
                    }
                }
            }

            writer.write(cont);

            writer.write(");\n");

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void wrIfnot(Iterator<Token> iterator) throws IOException {
        try {
            String cont = "    else";
            writer.write(cont);

            String test = new String();

            element = iterator.next();  // Ja foi escrito o else
            while (element.getTag() != Tag.LEFT_SMILE) {
                if (element.getTag() == Tag.NOT) {
                    element = iterator.next();
                    writer.write("!" + element.getLexeme() + " ");
                    test = test.concat("!" + element.getLexeme() + " ");
                    // Ignora proximo !
                    element = iterator.next();
                } else if (element.getTag() == Tag.THEN) {
                    // Ignora 'then'
                    element = iterator.next();
                    // Ignora '(:' e sai do loop
                    element = iterator.next();
                    break;
                } else {
                    cont = element.getLexeme();
                    test = test.concat(cont + " ");
                    writer.write(cont + " ");
                }

                element = iterator.next();
//                System.out.println("Agora no: " + element.getLexeme());
            }

            writer.write("\n    {\n");

            // TODO: Ler expressoes dentro do else

            writer.write("    }\n");
        }  catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void wrElif(Iterator<Token> iterator) throws IOException {
        try {
            String cont = "    else if (";
            writer.write(cont);

            String test = new String();

            element = iterator.next();  // Ja foi escrito o else if
            while (element.getTag() != Tag.LEFT_SMILE) {
                if (element.getTag() == Tag.NOT) {
                    element = iterator.next();
                    writer.write("!" + element.getLexeme() + " ");
                    test = test.concat("!" + element.getLexeme() + " ");
                    // Ignora proximo !
                    element = iterator.next();
                } else if (element.getTag() == Tag.THEN) {
                    // Ignora 'then'
                    element = iterator.next();
                    // Ignora '(:' e sai do loop
                    element = iterator.next();
                    break;
                } else {
                    cont = element.getLexeme();
                    test = test.concat(cont + " ");
                    writer.write(cont + " ");
                }

                element = iterator.next();
//                System.out.println("Agora no: " + element.getLexeme());
            }

            writer.write(")\n    {\n");

            // TODO: Ler expressoes dentro do else if

            writer.write("    }\n");
        }  catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void wrIf(Iterator<Token> iterator) throws IOException {
        try {
            String cont = "    if (";
            writer.write(cont);

            String test = new String();

            element = iterator.next();  // Ja foi escrito o if
            while (element.getTag() != Tag.LEFT_SMILE) {
                if (element.getTag() == Tag.NOT) {
                    element = iterator.next();
                    writer.write("!" + element.getLexeme() + " ");
                    test = test.concat("!" + element.getLexeme() + " ");
                    // Ignora proximo !
                    element = iterator.next();
                } else if (element.getTag() == Tag.THEN) {
                    // Ignora 'then'
                    element = iterator.next();
                    // Ignora '(:' e sai do loop
                    element = iterator.next();
                    break;
                } else {
                    cont = element.getLexeme();
                    test = test.concat(cont + " ");
                    writer.write(cont + " ");
                }

                element = iterator.next();
//                System.out.println("Agora no: " + element.getLexeme());
            }

            writer.write(")\n    {\n");

            // TODO: Ler expressoes dentro do if

            writer.write("    }\n");
        }  catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void wrWhileLoop(Iterator<Token> iterator) throws IOException{
        try {
            String cont = "    while (";
            writer.write(cont);

            String test = new String();

            element = iterator.next();
            boolean first = true;   // Controlar espaco adicional antes do ')'
            while (element.getTag() != Tag.LEFT_SMILE) {
                if (element.getTag() == Tag.NOT) {
                    element = iterator.next();
                    writer.write("!" + element.getLexeme() + " ");
                    test = test.concat("!" + element.getLexeme() + " ");
                    // Ignora proximo !
                    element = iterator.next();
                } else {
                    cont = element.getLexeme();
                    test = test.concat(cont + " ");
                    writer.write(cont + " ");
                }

                element = iterator.next();
//                System.out.println("Agora no: " + element.getLexeme());
            }

            writer.write(")\n    {\n");

            // TODO: Ler expressoes dentro do while

            writer.write("    }\n");


        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private boolean isFirstGreater(int first, int second) {
        return first > second;
    }

    private void wrForLoop(Iterator<Token> iterator) throws IOException{
        try {
            // Pega primeiro parametro
            element = iterator.next();
            String primParam = element.getLexeme();
//            System.out.println("Primeiro param: " + primParam);

            // Ignora reticencias
            element = iterator.next();

            // Pega segundo parametro
            element = iterator.next();
            String segParam = element.getLexeme();
//            System.out.println("Segundo param: " + segParam);

            boolean increment = true;
            if (isFirstGreater(Integer.parseInt(primParam), Integer.parseInt(segParam))) {
                increment = false;
//                    System.out.println("Primeiro eh maior que segundo, trocando valores e decrementar" + increment);
//                    System.out.println(primParam + " e " + segParam);
            }

            if (increment) {
                content = "    for (int " + forVar + " = " + primParam + "; " + forVar + " <= " + segParam + "; " + forVar + "++) {\n";
            } else {
                content = "    for (int " + forVar + " = " + primParam + "; " + forVar + " >= " + segParam + "; " + forVar + "--) {\n";
            }
            forVar++;

            // Escrever header do loop
            writer.write(content);

            element = iterator.next();
//            System.out.println("Dentro da funcao: " + element.toString());
            // TODO: Escrever expressoes dentro do loop
//            while (element.getTag() != Tag.RIGHT_SMILE) {
////                // Escrever expressoes
//                if (element.getTag() == Tag.VAR) {
//                    wrAttribution(element, iterator);
//                }
//
//                if (element.getTag() == Tag.FOR) {
//                    wrForLoop(iterator);
////                System.out.println("Fora da funcao: " + element.toString());
//                }
//
//                if (element.getTag() == Tag.WHILE) {
//                    wrWhileLoop(iterator);
//                }
//
//                if (element.getTag() == Tag.IF) {
//                    wrIf(iterator);
//                }
//
//                if (element.getTag() == Tag.ELIF) {
//                    wrElif(iterator);
//                }
//
//                if (element.getTag() == Tag.IFNOT) {
//                    wrIfnot(iterator);
//                }
//
//                if (element.getTag() == Tag.WRITE) {
//                    wrWrite(iterator);
//                }
//
//                if (element.getTag() == Tag.READ) {
//                    wrRead(iterator);
//                }
////
////
//                element = iterator.next();
//            }

            writer.write("    }\n");

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void wrBeginning() throws IOException{

        try {
            content = "#include <stdio.h>\n\n" + 
                  "int main(int argc, char* argv[]){\n";
            writer.write(content);

        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }
    
    private void wrAttribution(Token element, Iterator<Token> iterator) throws IOException{
        try {
            //var <=> 10 is int..
            //var <=> 3 / var..
            String var = element.getLexeme();
            String type;
            String value;
            TypeValue tp; 
                
                element = iterator.next();
                if (element.getTag() == Tag.IS) {//var is int..
                    element = iterator.next();
                    type = element.getLexeme();
                    if (type == "string") {
                        type = "char*";
                    }
                    tp = new TypeValue(element.getTag(), var);
                    varList.add(tp);
                    
                    content = "    " + type + " " + var + ";\n"; //ESTA PRONTO
                    
                }else if (element.getTag() == Tag.ATTRIBUTION) {
                    
                    content = var + " =";

                    while (element.getTag() != Tag.END_OF_EXPRESSION) {//var <=> 10 + b / a..
                              
                        element = iterator.next();

                        if (element.getTag() == Tag.VAR){ 
                            String varName = element.getLexeme();
                            content += " " + varName;

                            // for (HashMap.Entry<String, TypeValue> entry : var_table.entrySet()) {
                            //     String varName2 = entry.getKey();
                            //     if(varName2 == varName){//existe var na var_table
                            //         TypeValue tp2 = entry.getValue();
                            //         //fazer verificacao de tipos e pegar o valor da variavel
                            //     }
                            // }

                        }
                        if((element.getTag() == Tag.INTEGER) || (element.getTag() == Tag.FLOATING) || (element.getTag() == Tag.STRING)){//var <=> 10 
                            value = element.getLexeme(); //pegar o valor e concatenar no content
                            content += " " + value;
                            //verificar se ela existe
                        }if (element.getTag() == Tag.IS) { //var <=> 10 is int.. 
                            element = iterator.next();
                            type = element.getLexeme();
                            if (type == "string") {
                                type = "char*";
                            }
                            tp = new TypeValue(element.getTag(), var);
                            varList.add(tp);
                            content =  type + " " + content;
                            break;
                        }else if ((element.getTag() == Tag.MINUS) || (element.getTag() == Tag.PLUS) || (element.getTag() == Tag.MULTIPLY) || (element.getTag() == Tag.DIVISION)){ //+ - etc
                            value = element.getLexeme();
                            content += " " + value; 

                        }
                    }
                    content = "    " + content;
                    content += ";\n";
                    
                }
            
            writer.write(content);

        } catch (IOException e) {
            // TODO: handle exception        
        }
    }

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

        if (peek == '+' || peek == '-' || peek == '/' || peek == '*') {
            readMath();
        }    

        if (peek == '(' || peek == ')' || peek == ':') {
            readParentheses();
        }

        // Retorna palavras-chave e identificadores
        if (Character.isAlphabetic(peek) || peek == '_') {
            readWords();
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
