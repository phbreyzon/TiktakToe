
package tiktaktoe;
public class Board {
    private char[] map = {'-','-','-','-','-','-','-','-','-'};
    
    public void setMap( int position, char symbol){
        map[position] = symbol;
    }

    public char[] getMap(){
        return map;
    }
}
