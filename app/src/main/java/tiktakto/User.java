package tiktakto;

public class User {
    private String Symbol = new String(); 
    private int turn = 0;


    public User(String symbol, int turn) {
        this.Symbol = symbol;
        this.turn = turn;
    }
    
    public String getSymbol() {
        return Symbol;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }


}
