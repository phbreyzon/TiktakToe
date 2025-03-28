package com.tiktaktoe;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.net.URL;
import org.apache.commons.math3.analysis.function.Sigmoid;

public class KI extends User {

    private Map<String, double[]> cachedDB;
    private String DATABASE_FILE = "tiktaktoe.db";
    private Connection conn;
    private static final double BIAS = 0.3; // Bias value (between 0 and max 1)

    public KI(char symbol, int turn) {
        super(symbol, turn, "machine");
        cachedDB = new HashMap<>();
        initDatabase();
        loadDatabase();
    }

    private void initDatabase() {
        try {
            // Get path to database file within resources
            URL resourceUrl = getClass().getClassLoader().getResource(DATABASE_FILE);
            String dbPath;
            
            if (resourceUrl == null) {
                // Database doesn't exist yet - create in resources directory
                dbPath = new File("src/main/resources/" + DATABASE_FILE).getAbsolutePath();
            } else {
                dbPath = new File(resourceUrl.toURI()).getAbsolutePath();
            }
            
            // Connect to database
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            // Create table for each symbol if it doesn't exist
            Statement stmt = conn.createStatement();
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS moves_" + getSymbol() + " (" +
                "board_state TEXT PRIMARY KEY, " +
                "move_0 REAL, move_1 REAL, move_2 REAL, " +
                "move_3 REAL, move_4 REAL, move_5 REAL, " +
                "move_6 REAL, move_7 REAL, move_8 REAL" +
                ")"
            );
            stmt.close();
        } catch (Exception e) {
            System.out.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDatabase() {
        try {
            String tableName = "moves_" + this.getSymbol();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            
            while (rs.next()) {
                String boardState = rs.getString("board_state");
                double[] moves = new double[9];
                for (int i = 0; i < 9; i++) {
                    moves[i] = rs.getDouble("move_" + i);
                }
                cachedDB.put(boardState, moves);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error loading database: " + e.getMessage());
        }
    }

    private void saveDatabase() {
        try {
            String tableName = "moves_" + getSymbol();
            conn.setAutoCommit(false);
            
            String sql = "INSERT OR REPLACE INTO " + tableName + 
                         " (board_state, move_0, move_1, move_2, move_3, move_4, move_5, move_6, move_7, move_8) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                         
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            for (Map.Entry<String, double[]> entry : cachedDB.entrySet()) {
                pstmt.setString(1, entry.getKey());
                double[] moves = entry.getValue();
                for (int i = 0; i < 9; i++) {
                    pstmt.setDouble(i + 2, moves[i]);
                }
                pstmt.executeUpdate();
            }
            
            conn.commit();
            pstmt.close();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Error saving database: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
        }
    }

    private String getBoardState(Board board) {
        return String.copyValueOf(board.getMap());
    }

    public int makeMove(Board board) {
        String boardState = getBoardState(board);
        double[] moves = cachedDB.getOrDefault(boardState, new double[9]);
        
        // Find best move
        int bestMove = -1;
        double maxValue = -1;
       
        for (int i = 0; i < 9; i++) {
            double move_biased = (moves[i] + (new Random().nextDouble(1.1) * BIAS)); 
            if (board.getMap()[i] == '-' && move_biased > maxValue) {
                maxValue = move_biased;
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

    public void learnFromGame(List<String> gameStates, List<Integer> moves, boolean won, boolean draw) {
        double reward;
        if(won) reward = 0.1;
        else reward = -0.1;
        if(draw) reward = 0.05;

        for (int i = 0; i < gameStates.size(); i++) {
            String state = gameStates.get(i);
            int move = moves.get(i);
            
            double[] stateValues = cachedDB.getOrDefault(state, new double[9]);
            if(!won) stateValues[move] = -stateValues[move];
            stateValues[move] += reward;
            stateValues = normalizeWeights(stateValues, move);
            cachedDB.put(state, stateValues);
        }
        saveDatabase();
    }

    private double[] normalizeWeights(double[] weights, int move) {
        double[] normalizedWeights = new double[weights.length];
        
        // Find min and max values
        double min = Integer.MAX_VALUE;
        double max = Integer.MIN_VALUE;
        for (double weight : weights) {
            if (weight < min) min = weight;
            if (weight > max) max = weight;
        }

        // Apply Min-Max normalization with bias
        for (int i = 0; i < weights.length; i++) {
            if (max == min | weights[i] <= 0.0000) {
                // Handle the case where all values are the same
                normalizedWeights[i] = 0.5 + (new Random().nextDouble(-1,1) * BIAS); // BIAS Implementation
            } 
            else if(i == move){                
                normalizedWeights[i] = new Sigmoid().value(weights[i]);
            } 
            else normalizedWeights[i] = weights[i];
        }
        return normalizedWeights;
    }
        // Close database connection when done
        @Override
        protected void finalize() throws Throwable {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing database: " + e.getMessage());
            } finally {
                super.finalize();
            }
        }
}
