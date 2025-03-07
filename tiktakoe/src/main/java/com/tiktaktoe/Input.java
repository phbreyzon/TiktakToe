package com.tiktaktoe;

import java.util.Scanner;

public class Input {
    private static Scanner scanner = new Scanner(System.in);
    
    // Basic string input
    public static String input() {
        return scanner.nextLine();
    }
    
    // Input with prompt
    public static String input(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    // Integer input
    public static int inputInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }
     public static int validMove(String prompt, Board board) {
        while (true) {
            try {
                System.out.print(prompt);
                int position = Integer.parseInt(scanner.nextLine());
                if(board.getMap()[position] == '-') return position;
                else System.out.println("Invalid move, again: ");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }
    
    // Double input
    public static double inputDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    // Boolean input
    public static boolean inputBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().toLowerCase();
            if (input.equals("true") || input.equals("1") || input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("false") || input.equals("0") || input.equals("no") || input.equals("n")) {
                return false;
            } else {
                System.out.println("Please enter true/false, yes/no, or 1/0.");
            }
        }
    }
}