package tiktakto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class KI extends User{

    private Map<String, int[]> loadedDB;
    private static final String DATABASE_FILE = "database.csv";


    public KI(char symbol, int turn) {
        super(symbol, turn, "machine");
        loadedDB = new HashMap<>();
        loadDatabase();



    }

    // Load the database from CSV file
    private void loadDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String boardState = parts[0];
                int[] moves = new int[9];
                for (int i = 0; i < 9; i++) {
                    moves[i] = Integer.parseInt(parts[i + 1]);
                }
                loadedDB.put(boardState, moves);
            }
        } catch (IOException e) {
            System.out.println("No existing database found. Starting fresh.");
        }
    }




    // Save the database to CSV file
    private void saveDatabase() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATABASE_FILE))) {
            for (Map.Entry<String, int[]> entry : loadedDB.entrySet()) {
                StringBuilder line = new StringBuilder(entry.getKey());
                for (int value : entry.getValue()) {
                    line.append(",").append(value);
                }
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error saving database: " + e.getMessage());
        }
    }


        // Get board state as string
    private String getBoardState(Board board) {
        return String.copyValueOf(board.getMap());
    }




   // Make a move based on learned data
    public int makeMove(Board board) {
        String boardState = getBoardState(board);
        int[] moves = loadedDB.getOrDefault(boardState, new int[9]);
        
        // Find best move
        int bestMove = -1;
        int maxValue = -1;
        
        for (int i = 0; i < 9; i++) {
            if (board.getMap()[i] == '-' && moves[i] > maxValue) {
                maxValue = moves[i];
                bestMove = i;
            }
        }
        
        // If no learned moves, make random move
        if (bestMove == -1) {
            List<Integer> availableMoves = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (board.getMap()[i] == '-') {
                    availableMoves.add(i);
                }
            }
            if (!availableMoves.isEmpty()) {
                bestMove = availableMoves.get(new Random().nextInt(availableMoves.size()));
            }
        }
        return bestMove;
    }



    // Learn from game outcome
    public void learnFromGame(List<String> gameStates, List<Integer> moves, boolean won) {
            int reward;
            if(won) reward = 1;
            else reward = -1;

        for (int i = 0; i < gameStates.size(); i++) {
            String state = gameStates.get(i);
            int move = moves.get(i);
            
            int[] stateValues = loadedDB.getOrDefault(state, new int[9]);
            stateValues[move] += reward;
            loadedDB.put(state, stateValues);
        }
        
        saveDatabase();
    }







}
