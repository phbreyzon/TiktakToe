package tiktakto;

public class Board {
    private char[] mapString = {'-','-','-','-','-','-','-','-','-'};
    
    public void setMap( int position, char symbol){
        mapString[position] = symbol;
    }

    public char[] getMap(){
        return mapString;
    }
}
