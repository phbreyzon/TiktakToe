
package tiktaktoe;

public class User {
    private char Symbol; 
    private int turn;
    private String name;


    public User(char symbol, int turn, String name) {
        this.Symbol = symbol;
        this.turn = turn;
        this.name = name;
    }
    
    public char getSymbol() {
        return Symbol;
    }
    public int getTurn() {
        return turn;
    }
    public String getName() {
        return name;
        
    }
    public void setTurn(int turn) {
        this.turn = turn;
    }
    


}
