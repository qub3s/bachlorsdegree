package doerfer;
import doerfer.Board;
import doerfer.preset.Player;
import doerfer.preset.RNG;
import doerfer.preset.Tile;
import doerfer.preset.TilePlacement;
import doerfer.preset.TileGenerator;
import doerfer.preset.GameConfiguration;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;

/** Advanced Player Class that Implements Player */
public class AdvancedPlayer implements Player{
    /**the id of the player */
    private int id;

    /**score of the player*/
    private List<Integer> score;
    /**the id of the currentplayer */
    private int currentplayerid;
    /**name of the player */
    private String name;
    /**the color of the player */
    private String color;
    /**random number generator */
    private RNG random;
    /**list of the already played tiles of all players */
    private List<AdvancedTile> playedtiles;
    /**the private board of the player  */
    private Board board;
    /**tileplacement generator */
    private TileGenerator generator;
    /**gameconfiguration */
    private AdvancedGameConfiguration conf;
    /** Placement of the Last Tile */
    private TilePlacement lasttileplacement;
    /** is true when this player isnt played by a human player */
    private boolean isAIPlayer = false;
    /** if call verfiy game is set to true the verfiy game function can be called and the game is over */
    private boolean callverfiygame = false;
    /** the callrequestnexradnum is set to true if request-/notifytileplacement is beeing called */
    private boolean callrequestnexradnum = true;
    /** the callnotifynewuncoveredtile is set to true if nextrandnum is beeing called */
    private boolean callnotifynewuncoveredtile = false;
    /** init is beeing set to false after the first init call */
    private boolean callinit = true;
    /** counter how often the variable can be reset at the start of the game */
    private int counterresetcallnextrandomnumber = 0;
    
    /**sets up the player class 
    *   @param gameconf the game loaded game configuration
    *   @param playerid the id of the player
    *   @throws Exception Throws Exeption, when called at the wrong point in time*/
    public void init(AdvancedGameConfiguration gameconf, int playerid)throws Exception{
        if(!callinit){
            throw new Exception("The init Function can be called exactly once per player!!!");
        }
        conf = gameconf;
        id = playerid;
        currentplayerid = -1;

        name = conf.settings.playerNames.get(id-1);

        random = new RNG();
        
        board = new Board(gameconf);

        score = new LinkedList<>();
        
        generator = new TileGenerator(gameconf);
        
        for(int n1 = 0; n1 < gameconf.getNumPlayers(); n1++){
            score.add(0);
        }
        
        playedtiles = new LinkedList<>();

        return;
    }

    /** Notifys this Player about one Placed Tile 
    @param tile The New Tile that is beeing placed
    @throws Exception Throws Exeption, when called at the wrong point in time*/
    public void notifyNewUncoveredTile(AdvancedTile tile)throws Exception{
        if(!callnotifynewuncoveredtile){
            throw new Exception("The NextRandomNumber Function can only be called after request-or notifygame!!!");
        }
        playedtiles.add(tile);
        return;
    }

    /** notify this player of the TilePlacement
    @param tp the tileplacement */
    public void notifyTilePlacement(TilePlacement tp){
        if(tp != null){
            board.addTile(playedtiles.get(playedtiles.size()-1),tp.getRow(),tp.getColumn(),tp.getRotation(),currentplayerid);
            lasttileplacement = tp;
        }
        callrequestnexradnum = true;
        return;
    }

    /**Requests the next Random number 
    @param tp The Placement of the Tile */
    public void makemove(TilePlacement tp){
        if(tp != null){
            board.addTile(playedtiles.get(playedtiles.size()-1),tp.getRow(),tp.getColumn(),tp.getRotation(),currentplayerid);
        }
        lasttileplacement = tp;
        return;
    }

    /**Requests the next Random number
    @return nextrandomnum
    @throws Exception Throws Exeption, when called at the wrong point in time*/
    public long requestNextRandomNumber()throws Exception{
        if(!callrequestnexradnum){
            throw new Exception("The NextRandomNumber Function can only be called after request-or notifygame!!!");
        }
        currentplayerid = (currentplayerid + 1)%conf.getNumPlayers();
        callnotifynewuncoveredtile = true;
        callrequestnexradnum = false;
        return random.next();
    }

    /** Requests the next Random Number
    @return The next Randomnumber*/
    public long requestRandomNumberSeed(){
        callverfiygame = true;
        return random.getSeed();
    }

    /** Getter for the Placement of the last tile
    @return Last TilePlacement */
    public TilePlacement requestTilePlacement(){
        callrequestnexradnum = true;
        return lasttileplacement;
    }

    /** checks wether the game was valid
    * @param seeds The seeds of the players
    * @param scores The scores of the player
    * @throws Exception Throws Exeption, when the game wasnt fair*/
    public void verifyGame(List<Long> seeds,List<Integer> scores)throws Exception{
        if(!callverfiygame){
            throw new Exception("The verifygame Function is beein called before the game has ended!!!");
        }
        
        // checks if the seed is the same
        if(seeds.get(id-1) != requestRandomNumberSeed()){
              throw new Exception("Wrong seed !!!");
        }

        // checks if the score of all the players is the same
        for(int n1 = 0; n1 < score.size(); n1++){
            if(scores.get(n1) != score.get(n1)){
                throw new Exception("Wrong score !!!");
            }
        }

        // Creating new RNG Elements
        RNG[] rngarr = new RNG[seeds.size()]; 

        for(int n1 = 0; n1 < score.size(); n1++){
            rngarr[n1] = new RNG(seeds.get(n1));
        }
        
        for(int n1 = 0; n1 < playedtiles.size(); n1++){
            long combinedseed = rngarr[0].next(); 
            if(conf.getNumPlayers() > 1){
                for(int n2 = 1; n2 < conf.getNumPlayers(); n2++){
                    combinedseed = combinedseed ^ rngarr[n2].next();;
                }
            }
            if(new AdvancedTile(generator.generateTile(combinedseed)).getBiomes() == playedtiles.get(n1).getBiomes()){
                throw new Exception("The game was rigged and needs to be restarted !!!");
            }
        }
        System.out.println("From player "+id+" the game was fair.");
        return;
    }

    /** resets the blocking var for the requestnextrandomnumber
    @throws Exception Throws Exeption, when called at the wrong point in time*/
    public void resetcallnextrandomnumber()throws Exception{
        if(counterresetcallnextrandomnumber > conf.getNumPlayers()){
            throw new Exception("The NextRandomNumber Function can only be called after request-or notifygame!!!"); 
        }
        counterresetcallnextrandomnumber = counterresetcallnextrandomnumber + 1;
        callrequestnexradnum = true;
        return;
    }

    /** Change name of the Player
    * @param newname String of the new name of the Player*/
    public void setName(String newname){
        name = newname;
    }

    /** Returns the Score of the Player
    @return Score of the Player*/
    public int getScore(){
        HashMap<Integer, Integer> hashmapoints = board.countPoints();
        for(int n1 = 0; n1 < conf.getNumPlayers(); n1++){
            score.set(n1,hashmapoints.get(n1+1));
        }
        return score.get(id-1);
    }

    /**@return returns the current id of this player */
    public int getId(){
        return id;
    }

    /** getter for the name
    @return the neme of the Player
    */
    public String getName(){
        return name;
    }

    /** returns boolean Value if player is an AI Player 
    @return true if ai player*/
    public boolean getIsAIPlayer(){
        return isAIPlayer;
    }

    /** Placeholder for the other function, shoud never be called 
    @throws Exception Cant be called*/
    public void setPLayerasAIPlayer()throws Exception{
        throw new Exception("The function setPLayerasAIPlayer cant be called inside of Advanced Player");
    }

    /** Placeholder for the other function, shoud never be called 
    * @param currenttile would hold the current tile
    * @param tilelist would hold the arraylist
    * @param b would hold the board
    * @return would return Tileplacement
    @throws Exception Cant be called*/

    public TilePlacement randomAiMove(AdvancedTile currenttile, ArrayList<DummyTile> tilelist,Board b)throws Exception{
        throw new Exception("The function randomAiMove cant be called inside of Advanced Player");
    }

    /** function never gets used but needs to be here so the interface can be implemented */
    public void notifyNewUncoveredTile(Tile tile){
        return;
    }

    /** function never gets used but needs to be here so the interface can be implemented
    @param gameconf The Gameconfiguaration
    @param playerid The id of the Player
    */    
    public void init(GameConfiguration gameconf, int playerid){
        return;
    }
}