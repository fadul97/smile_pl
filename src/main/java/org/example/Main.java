package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Lexer scanner = new Lexer();
        scanner.start();
    }

    //    public static void main(String[] args) {
//        try {
//            File mainFile = new File("main.sml");
//
//            // Read the file using a scanner
//            Scanner myReader = new Scanner(mainFile);
//
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(
//                            new FileInputStream(mainFile),
//                            StandardCharsets.UTF_8));
//
//            StringBuilder word = new StringBuilder();
//
//            boolean isOneDot = false;
//            boolean isTwoDots = false;
//            boolean isThreeDots = false;
//
//            int character;
//            char nextCharacter;
//            while ((character = reader.read()) != -1) {
//                // Read character
//                char c = (char) character;
//
//                // Is white space?
//                if (Character.isWhitespace(c)) {
//                    if(word.length() > 0) {
//                        if (containsLineBreak(word.toString())) {
//                            word = new StringBuilder(word.toString().replace("\n", "").replace("\r", ""));
//                        }
//                        if(!containsEmptySpace(word.toString()) && !containsLineBreak(word.toString()) && word.length() > 0) {
//                            System.out.println("Token: " + word);
//                            word.setLength(0);
//                        }
//                    }
//                // Is it a dot?
//                } else if (c == '.') {
//                    int i = 0;
//                    if (!containsLineBreak(word.toString())) {
//                        System.out.println("Token: " + word);
//                        word.setLength(0);
//                    }
//
//                    while (c == '.') {
//                        i++;
//                        character = reader.read();
//                        c = (char) character;
//                    }
//                    word.append(c);
//
//                    if (i == 2) {
//                        System.out.println("Token: ..");
//                    } else if (i == 3) {
//                        System.out.println("Token: ...");
//                    } else {
//                        System.out.println("ERRO: Muitos pontos");
//                    }
//                } else {
//                    word.append(c);
//                }
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println("Arquivo nao encontrado.");
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("Erro na funcao reader.read().");
//            e.printStackTrace();
//        }
//    }
//
//    // Helper method to check for line break characters
//    private static boolean isLineBreak(char ch) {
//        return ch == '\n' || ch == '\r';
//    }
//
//    private static boolean containsEmptySpace(String str) {
//        return str.contains(" ");
//    }
//
//    private static boolean containsLineBreak(String str) {
//        return str.contains("\n") || str.contains("\r");
//    }
}

/*
public static void main(String[] args) {
        try {
            // Create a file object
            File myObj = new File("main.sml");

            // Read the file using a scanner
            Scanner myReader = new Scanner(myObj);

            // Token list
            ArrayList<String> tk = new ArrayList<>();

            while (myReader.hasNextLine()) {
                String line = myReader.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] tokens = line.split(" "); // Space delimiter

                    tk.add(Arrays.toString(line.split(" ")));

                    for (int i = 0; i < tokens.length; i++) {
                        int dots = countDots(tokens[i]);
                        if (dots == 2) {
                            String[] dotSeparatedTokens = tokens[i].split("(?<=\\.{2,3})|(?=\\.{2,3})");
                            System.out.println(Arrays.toString(dotSeparatedTokens));
                        }

                        if (dots == 3) {
                            char at = tokens[i].charAt(0);
                            while (at != '.') {

                                at++;
                            }
                            String[] dotSeparatedTokens = tokens[i].split("(?<=\\.{0,3})");
                            System.out.println(Arrays.toString(dotSeparatedTokens));
                        }

//                        System.out.println(tokens[i]);
                    }
                }
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static int countDots(String input) {
        int dotCount = 0;
        for (char c : input.toCharArray()) {
            if (c == '.') {
                dotCount++;
            }
        }
        return dotCount;
    }
 */