package tiktakto;

public class User {
    private char Symbol; 
    private int turn;


    public User(char symbol, int turn) {
        this.Symbol = symbol;
        this.turn = turn;
    }
    
    public char getSymbol() {
        return Symbol;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }


}
