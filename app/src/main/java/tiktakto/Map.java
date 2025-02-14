package tiktakto;

public class Map {
    private String[] mapString = {"-","-","-","-","-","-","-","-","-"};
    
    public String[] updateMap( int position, String symbol){
        mapString[position] = symbol;
        return mapString;
    }

    public String[] getMap(){
        return mapString;
    }
}
