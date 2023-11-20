package org.example;

import org.example.tokens.Id;
import org.example.tokens.Num;
import org.example.tokens.Token;

import java.io.IOException;
import java.util.HashMap;

public class Lexer {
    private int line;
    private char peek;
    private HashMap<String, Id> id_table;

    public Lexer() {
        line = 1;
        peek = ' ';
        id_table = new HashMap<>();
        id_table.put("true", new Id(NodeType.TRUE, "true"));
        id_table.put("false", new Id(NodeType.FALSE, "false"));

        System.out.println("===== Tabela HASH =====");
        for (String key : id_table.keySet()) {
            System.out.println("Key: " + key);
        }
    }

    public void start() throws IOException {
        while (peek != '\n') scan();
    }

    Token scan() throws IOException {
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
            Id pos = id_table.get(word.toString());
            // Se pos ja estiver na tabela
            if (pos != null) {
//                System.out.println("Palavra encontrada na tabela!");

                // DEBUG: exibe token reconhecido
                switch (pos.getTag()) {
                    case TRUE:
//                        System.out.println(pos.getTokenName());
                        System.out.println("<TRUE, " + pos.getName() + ">");
                        break;
                    case FALSE:
                        System.out.println("<TRUE, " + pos.getName() + ">");
                        break;
                    default:
                        System.out.println("<ID, " + pos.getName() + ">");
                        break;
                }

                // Retorna token
                return pos;
            }

            // Se lexema nao estiver na tabela, insere
            Id new_id = new Id(NodeType.ID, word.toString());
            id_table.put(word.toString(), new_id);

            // DEBUG: exibe token reconhecido
            System.out.println(new_id.getTokenName());

            // Retorna token
            return new_id;
        }

        // Operadores e caracteres nao alphanumericos isolados
        Token t = new Token(NodeType.UNKNOWN);

        // DEBUG: exibe token para o caractere
        System.out.println(t.getUnknownToken(peek));

        peek = ' ';

        return t;
    }
}
