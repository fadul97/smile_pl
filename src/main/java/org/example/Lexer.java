package org.example;

import org.example.tokens.Id;
import org.example.tokens.Num;
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

    public Lexer() {
        line = 1;
        peek = ' ';
        token_table = new HashMap<>();
        token_table.put("TheBeginning", new Token(Tag.BEGINNING, "TheBeginning"));
        token_table.put("TheEnd", new Token(Tag.END, "TheEnd"));
        token_table.put("true", new Id(Tag.TRUE, "true"));
        token_table.put("false", new Id(Tag.FALSE, "false"));
        token_table.put("int", new Id(Tag.TYPE, "int"));
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

            // Read the file using a scanner
            Scanner myReader = new Scanner(mainFile);

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

    Token scan(char ch) throws IOException {
        peek = ch;

        // Ignora espaços em branco, tabulações e novas linhas
        while (Character.isWhitespace(peek)) {
            if (peek == '\n') line += 1;
            peek = (char) reader.read();
        }

        // TODO: Ignorar comentários

        // Retorna números
        if (Character.isDigit(peek)) {
            // Ponto-flutuante não foi encontrado
            boolean dot = false;

            // Acumula numeros
            StringBuilder number = new StringBuilder();
            int i = 0;
            do {
                if (peek == '.') {
                    // Primeiro ponto encontrado
                    if (!dot) {
                        i++;
                        dot = true;
                    } else {
                        // Segundo ponto encontrado - Final da expressao ou loop
                        Token n = new Token(Tag.INTEGER, number.deleteCharAt(number.length() -1).toString());
                        System.out.println(n.toString());

                        // TODO: Final da expressao (?)
                        i++;
                        Token t = new Token(Tag.END_OF_EXPRESSION, "..");
                        peek = (char) reader.read();
                        if (peek == '.') {
                            i++;
                            // Guardar primeiro valor do loop for
                            t = new Token(Tag.THREE_DOT, "...");
                            token = t;
                            System.out.println(token.toString());
                            return token;
                        } else {
                            // TODO: Final da expressao
                            token = t;
                            System.out.println(token.toString());
                            return token;
                        }
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

        // Retorna palavras-chave e identificadores
        while (Character.isAlphabetic(peek)) {
            StringBuilder word = new StringBuilder();

            do {
                word.append(peek);
                peek = (char) reader.read();
            } while (Character.isAlphabetic(peek));

            // DEBUG: Procurar token na tabela
//            System.out.println("PROCURANDO POR: " + word.toString());
            Token pos = token_table.get(word.toString());

            // Se lexema ja esta na tabela
            if (pos != null) {
                // TODO: Nao acha a keyword 'int' - GAMBIARRA
//                System.out.println("LEXEME: " + pos.getLexeme());
//                if (pos.getLexeme().equals("null")) {
                    pos.setLexeme(word.toString());
//                }
                // Retorna o token da tabela
                token = pos;
                System.out.println(token.toString());
                return token;
            }

            // Se o lexema ainda não está na tabela
            Token t = new Token(Tag.ID, word.toString());
            token_table.put(word.toString(), t);

            // Retorna o token ID
            token = t;
            System.out.println(token.toString());
            return token;
        }

        // TODO: Verificar nossos operadores
        // Retorna operadores com mais de um caractere: >=, <=, == e !=
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
                if (next == '&') {
                    peek = (char) reader.read();
                    token = new Token(Tag.OR, "||");
                    System.out.println(token.toString());
                    return token;
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

        // Retorna caracteres não alphanuméricos isolados: (, ), +, -, etc.
        return token;
    }

    Token scanOld() throws IOException {
        // Ignora espacos em branco, tabs e novas linhas
        while(Character.isWhitespace(peek)) {
            if(peek == '\n') line +=1 ;
            peek = (char) System.in.read();
        }

        // Retorna numeros
        if (Character.isDigit(peek)) {
            int v = 0;

            do {
                // Converte caracter 'n' para digito numero n
                int n = peek - '0';
                v = 10 * v + n;
                peek = (char) System.in.read();
            } while (Character.isDigit(peek));

            // DEBUG
            System.out.println("<NUM, " + v + ">");

            return new Num(v);
        }

        // Retorna palavas e identificadores
        if (Character.isAlphabetic(peek)) {
            StringBuilder word = new StringBuilder();

            // Constroi a palavra
            do {
                word.append(peek);
                peek = (char) System.in.read();
            } while (Character.isAlphabetic(peek));

//            System.out.println("Palavra encontrada: " + word.toString());

            // FIXME: Arrumar como os Tokens sao encontrado
            Token pos = token_table.get(word.toString());
            // Se pos ja estiver na tabela
            if (pos != null) {
//                System.out.println("Palavra encontrada na tabela!");

                // DEBUG: exibe token reconhecido
//                switch (pos.getTag()) {
//                    case TRUE:
////                        System.out.println(pos.getTokenName());
//                        System.out.println("<TRUE, " + pos.getName() + ">");
//                        break;
//                    case FALSE:
//                        System.out.println("<TRUE, " + pos.getName() + ">");
//                        break;
//                    default:
//                        System.out.println("<ID, " + pos.getName() + ">");
//                        break;
//                }

                // Retorna token
                return pos;
            }

            // Se lexema nao estiver na tabela, insere
            Id new_id = new Id(Tag.ID, word.toString());
            token_table.put(word.toString(), new_id);

            // DEBUG: exibe token reconhecido
            System.out.println(new_id.getTokenName());

            // Retorna token
            return new_id;
        }

        // Operadores e caracteres nao alphanumericos isolados
        Token t = new Token(Tag.UNKNOWN, "UNKOWN");

        // DEBUG: exibe token para o caractere
//        System.out.println(t.getUnknownToken(peek));

        peek = ' ';

        System.out.println();
        return t;
    }

    void read() {
        try {
            mainFile = new File("main.sml");

            // Read the file using a scanner
            Scanner myReader = new Scanner(mainFile);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(mainFile),
                            StandardCharsets.UTF_8));

            StringBuilder word = new StringBuilder();

            boolean isOneDot = false;
            boolean isTwoDots = false;
            boolean isThreeDots = false;

            int character;
            char nextCharacter;
            while ((character = reader.read()) != -1) {
                // Read character
                char c = (char) character;

                // Is white space?
                if (Character.isWhitespace(c)) {
                    if(word.length() > 0) {
                        if (containsLineBreak(word.toString())) {
                            word = new StringBuilder(word.toString().replace("\n", "").replace("\r", ""));
                        }
                        if(!containsEmptySpace(word.toString()) && !containsLineBreak(word.toString()) && word.length() > 0) {
                            System.out.println("Token: " + word);
                            word.setLength(0);
                        }
                    }
                    // Is it a dot?
                } else if (c == '.') {
                    int i = 0;
                    if (!containsLineBreak(word.toString())) {
                        System.out.println("Token: " + word);
                        word.setLength(0);
                    }

                    while (c == '.') {
                        i++;
                        character = reader.read();
                        c = (char) character;
                    }
                    word.append(c);

                    if (i == 2) {
                        System.out.println("Token: ..");
                    } else if (i == 3) {
                        System.out.println("Token: ...");
                    } else {
                        System.out.println("ERRO: Muitos pontos");
                    }
                } else {
                    word.append(c);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo nao encontrado.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Erro na funcao reader.read().");
            e.printStackTrace();
        }
    }

    // Helper method to check for line break characters
    private static boolean isLineBreak(char ch) {
        return ch == '\n' || ch == '\r';
    }

    private static boolean containsEmptySpace(String str) {
        return str.contains(" ");
    }

    private static boolean containsLineBreak(String str) {
        return str.contains("\n") || str.contains("\r");
    }
}
