package net.etfbl.kriptografija.algoritmi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Algoritam {
    private static final int SHIFT_UPPERCASE = 32;

    public static String railFence(String text, String key){
        text = text.replaceAll("\\s", "");
        StringBuilder cipher = new StringBuilder();
        int numOfTracks = Integer.parseInt(key);
        char[][] encipherMatrix = new char[numOfTracks][text.length()];

        for (char[] matrix : encipherMatrix) {
            Arrays.fill(matrix, ' ');
        }

        int i = 0;
        int j = 0;
        boolean inc = true; //govori nam da li cemo povecavati broj kolone za sljedeci karakter ili smanjivati
        for(char c : text.toCharArray()) {
            encipherMatrix[i][j] = c;
            j++;

            if (inc) {
                //ako dodjemo do zadnje kolone pocinjemo sa smanjivanjem kolona
                if (++i == numOfTracks - 1) {
                    inc = false;
                }

            } else {
                if (--i == 0) {
                    inc = true;
                }
            }
        }
        for(i = 0; i < encipherMatrix.length; i++) {
            for (j = 0; j < encipherMatrix[i].length; j++){
                if(encipherMatrix[i][j] != ' '){
                    cipher.append(encipherMatrix[i][j]);
                }
            }
        }

        return cipher.toString();
    }

    public static String myszkowski (String text, String key){
        StringBuilder cipher = new StringBuilder();
        text = text.replaceAll("\\s", "");
        int numOfColumns = key.length();
        int numOfRows = text.length() % numOfColumns == 0 ? text.length() / numOfColumns : text.length() / numOfColumns + 1;
        char[][] encipherMatrix = new char[numOfRows][numOfColumns];
        int len = 0;

        //Pravimo pocetnu matricu
        for(int i = 0; i < numOfRows; i++){
            for(int j = 0; j < numOfColumns; j++){
                if(text.length() > len){
                    encipherMatrix[i][j] = text.charAt(len);
                    len++;
                }else{
                    encipherMatrix[i][j] = ' ';
                }

            }
        }

        int priority = 1;
        String englishAlphabet = "abcdefghijklmnopqrstuvwxyz";
        boolean foundLetter = false;
        int[] priorities = new int[key.length()];

        for (char letter :
                englishAlphabet.toCharArray()) {
            for(int i = 0; i < key.length(); i++){
                if(letterMatchesKey(letter, key.charAt(i))){
                    priorities[i] = priority;
                    foundLetter = true;
                }
            }
            if(foundLetter){
                priority++;
                foundLetter = false;
            }

        }


        for(int i = 1; i <= priorities.length && cipher.length() <= text.length(); i++){
            for(int j = 0; j < encipherMatrix.length; j++){
                for(int k = 0; k < encipherMatrix[j].length; k++){
                    if(priorities[k] == i && encipherMatrix[j][k] != ' '){
                        cipher.append(encipherMatrix[j][k]);
                    }
                }
            }
        }


        return cipher.toString();
    }

    public static String playFair(String text, String key){
        StringBuilder cipher = new StringBuilder();
        text = text.replaceAll("\\s", "").replaceAll("j", "i").replaceAll("J", "I");
        key = key.replaceAll("\\s", "").replaceAll("j", "i").replaceAll("J", "I");

        String keyWithoutDuplicates=removeDuplicates(key);
        String textWithPadding=addPadding(text, 'X');
        System.out.println(text);
        System.out.println(textWithPadding);
        System.out.println(keyWithoutDuplicates);
        char[][] encipherMatrix = new char[5][5];

        int row = 0, column = 0;
        for(int i = 0; i < keyWithoutDuplicates.length(); i++){
            encipherMatrix[row][column++] = keyWithoutDuplicates.charAt(i);
            if(column == 5){
                column = 0;
                row++;
            }
        }

        for(char c = 'a'; c <= 'z'; c++){
            if(c != 'j')
            {
                if (keyWithoutDuplicates.indexOf(c) == -1 && keyWithoutDuplicates.indexOf(c - SHIFT_UPPERCASE) == -1) {
                    if(keyWithoutDuplicates.indexOf(c) == -1){
                        encipherMatrix[row][column++] = (char) (c - SHIFT_UPPERCASE);
                    }
                    else{
                        encipherMatrix[row][column++] = c;
                    }

                    if (column == 5) {
                        column = 0;
                        row++;
                    }
                }
            }
        }
        for (int i = 0; i < textWithPadding.length(); i++) {
            if(i % 2 == 0){
                char firstCharacter = textWithPadding.charAt(i);
                char secondCharacter = textWithPadding.charAt(i+1);

                int rowOfFirstCharacter = 0, columnOfFirstCharacter = 0;
                int rowOfSecondCharacter = 0, columnOfSecondCharacter = 0;
                for (int j = 0; j < encipherMatrix.length; j++) {
                    for (int k = 0; k < encipherMatrix[j].length; k++) {
                        if(encipherMatrix[j][k] == firstCharacter){
                            rowOfFirstCharacter = j;
                            columnOfFirstCharacter = k;
                        }
                        if(encipherMatrix[j][k] == secondCharacter){
                            rowOfSecondCharacter = j;
                            columnOfSecondCharacter = k;
                        }
                    }
                }

                char changeForFirstCharacter = ' ', changeForSecondCharacter = ' ';
                if(rowOfFirstCharacter == rowOfSecondCharacter){
                    changeForFirstCharacter = encipherMatrix[rowOfFirstCharacter][++columnOfFirstCharacter%5];
                    changeForSecondCharacter = encipherMatrix[rowOfSecondCharacter][++columnOfSecondCharacter%5];

                }
                else if(columnOfFirstCharacter == columnOfSecondCharacter){
                    changeForFirstCharacter = encipherMatrix[++rowOfFirstCharacter%5][columnOfFirstCharacter];
                    changeForSecondCharacter = encipherMatrix[++rowOfSecondCharacter%5][columnOfSecondCharacter];
                }
                else{
                    changeForFirstCharacter = encipherMatrix[rowOfFirstCharacter][columnOfSecondCharacter];
                    changeForSecondCharacter = encipherMatrix[rowOfSecondCharacter][columnOfFirstCharacter];
                }
                cipher.append(changeForFirstCharacter);
                cipher.append(changeForSecondCharacter);

            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(encipherMatrix[i][j]);
            }
            System.out.println();
        }


        return cipher.toString();
    }



    private static boolean letterMatchesKey(char letter, char keyChar) {
        return letter == keyChar || (letter - SHIFT_UPPERCASE) == keyChar;
    }
    private static String removeDuplicates(String input) {
        StringBuilder result = new StringBuilder();
        StringBuilder seenChars = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            char lowercaseChar = Character.toLowerCase(currentChar);

            if (seenChars.indexOf(String.valueOf(lowercaseChar)) == -1) {
                result.append(currentChar);
                seenChars.append(lowercaseChar);
            }
        }

        return result.toString();
    }

    private static String addPadding(String text, char padding) {
        StringBuilder textWithPadding = new StringBuilder(text);
        for (int i = 0; i < textWithPadding.length(); i++) {
            if(i%2 == 0){
                if(i+1 == textWithPadding.length()){
                    textWithPadding.append(padding);
                }
                else{
                    if(textWithPadding.charAt(i) == textWithPadding.charAt(i+1)){
                        textWithPadding.insert(i+1, padding);
                    }
                }
            }
        }
        return textWithPadding.toString();
    }
}
