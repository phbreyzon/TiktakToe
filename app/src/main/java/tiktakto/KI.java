package tiktakto;

public class KI extends User{

    private static final String DATABASE_FILE = "database.csv";


    public KI(String symbol, int turn) {
        super(symbol, turn);

    }
    public int evaluate(Map map){
        
        return 0;
    }

}
