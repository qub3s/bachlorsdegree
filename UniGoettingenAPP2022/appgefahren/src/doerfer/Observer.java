package doerfer;
import doerfer.preset.TilePlacement;
import doerfer.preset.TileGenerator;
import java.util.concurrent.TimeUnit;

import java.awt.Color;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import com.kitfox.svg.SVGElementException;
import java.io.IOException;
import doerfer.preset.Settings;
import doerfer.preset.PlayerType;
import java.lang.RuntimeException;
import java.lang.Thread;

/**Observer class that manages the flow of the game and interacts witht the grafic @author Georg*/
public class Observer{
    /** cards that are left to play (cards at 0 the game stops) */
    private int cardstoplay;
    /** the id of the player that is the active player */
    private int idofcurrentplayer;
    /** seperate board for the grafic */
    private final Board board;
    /** Tile generator given by the preset */
    private final TileGenerator generator;
    /** Stores the gameconfiguration */
    private final AdvancedGameConfiguration conf;
    /** Stores the gameconfiguration */
    private final Settings sets;
    /** Timeunit to sleep */
    private final TimeUnit time = TimeUnit.MILLISECONDS;
    /** List (Queue) of the player+1 cards that are stored on the stack */
    private final Queue<AdvancedTile> currenttiles = new LinkedList<>();
    /** List of all the players in the game */
    private final List<AdvancedPlayer> players = new LinkedList<>();
    /** List of skipped players */
    private final List<Boolean> skipped = new LinkedList<>();
    /** List of the colors of every player */
    private final List<Color> colors;
    /** Controlls the visual part of the game */
    private AdvancedGameView AGV;
    /** stores wether this is the first move of the game */
    private final boolean firstdraw = true;
    /** set to true if the game ends */
    private boolean isgameover = false;
    /** getNewcurrenttile only removes the first tile if callgetnewcurrenttile = true */
    private boolean callgetNewCurrentTile = false;

    /** construktor for the observer class, creates the players,tilegenerator draws the fist x cards and sets ups different variables  
    @param gameconf different settings
    @throws Exception Throws Exeptions of Subclasses*/
    public Observer(AdvancedGameConfiguration gameconf)throws Exception{
        conf = gameconf;
        sets = conf.settings;
        colors = sets.playerColors;

        generator = new TileGenerator(gameconf);
        cardstoplay = gameconf.getNumTiles();
        idofcurrentplayer = 0;

        for(int n1 = 0; n1 < gameconf.getNumPlayers(); n1++){
            if(sets.playerTypes.get(n1) == PlayerType.HUMAN){
                players.add(new AdvancedPlayer());
            }
            else{
                players.add(new AIPlayer());
            }
            
            skipped.add(false);
            players.get(n1).init(gameconf,n1+1);
            if(sets.playerTypes.get(n1) == PlayerType.RANDOM_AI){players.get(n1).setPLayerasAIPlayer();}
        }

        board = new Board(gameconf);

        fillCurrentTile();
    }

    /** starts the game
    @throws Exception of Subclass */
    public void startgame() throws Exception{
        AGV = new AdvancedGameView(this);
        AGV.setActivePlayer(players.get(0));
        AGV.showBoard();
        AGV.showHUDLayer();
        initmove();
    }
    
    /** first half of the move
    @throws Exception of Subclass */
    public void initmove()throws Exception{
        AGV.showBoard();                        // At the start of every move the new board gets shown
        getNewCurrentTile();                    // new card gets drawn
        callgetNewCurrentTile = true;           // in the first round no cards gets removec cause the most upper card is the right one and needs to be on top of the stack for the gui
        // All the Players get notifyed about the drawn card
        for(AdvancedPlayer i: players) {
            i.notifyNewUncoveredTile(getCurrentTile());
        }
        AGV.showHUDLayer();
        board.togglePlacableDummyTiles(getCurrentTile(), getCurrentPlayerID());             // the board needs to be toggled so that the getplaceabledummytiles produces the right output
        checkIfMovePossible();
        // checking if checkIfMovePossible ended the game
        if(isgameover){
            return;
        }
        
        if(getCurrentPlayer().getIsAIPlayer()){         // If the Player is an AI player here its move gets processed
            TilePlacement tp = getCurrentPlayer().randomAiMove(getCurrentTile()  ,  board.getPlacableDummyTiles()  ,  board);
            AGV.placeTile(getCurrentTile()  , tp , getCurrentPlayer());
            AGV.focus(tp);
            // Set Internal and Grafic playervars to the next player
            idofcurrentplayer = (idofcurrentplayer + 1)%conf.getNumPlayers();
            AGV.setActivePlayer(players.get(idofcurrentplayer));

            // Waiting the given delay
            try{
                time.sleep(conf.settings.delay);
                }
            catch(Exception e){
                throw new RuntimeException("Sleep got interrupted.");
            }

            // Checking if the game is over (important in solo ai rounds)
            AGV.setNewTileNull();
            if(!isgameover){
                initmove();
            }   
        }
    }

    /** second half of the player of ai move
    @throws Exception of Subclass */
    public void restOfMove() throws Exception{
        // Get the Position of the placed tile from the player
        TilePlacement tp = players.get(idofcurrentplayer).requestTilePlacement();
        // Notify all the other Players about the position
        for(int n1 = 0; n1 < players.size(); n1++){
            if(n1 != idofcurrentplayer){
                players.get(n1).notifyTilePlacement(tp);
            }
        }
        if(tp != null){
            System.out.println("There are "+cardstoplay+" Cards left to play.");
            cardstoplay = cardstoplay - 1;
        }
        else{
            return;
        }

        AGV.setPoints(getpoints());
        AGV.showHUDLayer();

        if(cardstoplay == 0){
            endgame();
        }
        else if(!getCurrentPlayer().getIsAIPlayer()){           // ai player cant all initmove here cause of inifnite feedbackloop
            // Set Internal and Grafic playervars to the next player
            idofcurrentplayer = (idofcurrentplayer + 1)%conf.getNumPlayers();
            AGV.setActivePlayer(players.get(idofcurrentplayer));
            System.out.println("end of playermove");
            initmove();
        }
    }

    /** Checks if a move is possible of aborts the game
    @throws Exception of Subclass */
    public void checkIfMovePossible()throws Exception{
        if(!skipped.contains(false)){
            for(int n1 = 0; n1 < skipped.size(); n1++){
                AGV.setPlayerSkipped(players.get(n1),false);
            }
            AGV.showHUDLayer();
            endgame();
            return;
        }
        AGV.setPlayerSkipped(players.get(idofcurrentplayer),false);
        if(board.getPlacableDummyTiles().size() == 0){
            // Skip the player and set vars 
            skipped.set(idofcurrentplayer,true);
            AGV.placeTile(getCurrentTile(),null,getCurrentPlayer());
            AGV.setPlayerSkipped(players.get(idofcurrentplayer),true);

            // Set Internal and Grafic playervars to the next player
            idofcurrentplayer = (idofcurrentplayer + 1)%conf.getNumPlayers();
            AGV.setActivePlayer(players.get(getCurrentPlayerID()));

            // Reset and reload Grafic
            AGV.setNewTileNull();
            AGV.showBoard();
            AGV.showHUDLayer();

            // recursive use of function
            checkIfMovePossible();

            // reset all the vars to false
            for(int n1 = 0; n1 < skipped.size(); n1++){
                skipped.set(n1,false);
            }   
        }
    }

    /** funktion to end the game
    @throws Exception Exeption of subclasses*/
    public void endgame()throws Exception{
        System.out.println("Game Over");
        isgameover = true;
        verifyGame();
        AGV.showHUDLayer();
        AGV.setGameOver(true);
    }

    /** Function that gets called at the end of a game that checks if all the player played the game fair 
    @throws Exception Exeption of Subclass*/
    public void verifyGame()throws Exception{
        // create datatypes to verify the game
        List<Long> seeds = new LinkedList<>();
        List<Integer> scores = new LinkedList<>();
        // collect data from all the player to verify the game
        for(int n1 = 0; n1 < conf.getNumPlayers(); n1++){
            seeds.add(players.get(n1).requestRandomNumberSeed());
            scores.add(players.get(n1).getScore());
        }
        // verify Game
        for(int n1 = 0; n1 < conf.getNumPlayers(); n1++){
            players.get(n1).verifyGame(seeds,scores);
        }
    }

    /** getter for the currentPlayer
    @return the player that is active*/
    public AdvancedPlayer getCurrentPlayer(){
        return players.get(idofcurrentplayer);
    }

    /** fill CurrentTile queue 
    @throws Exception throws Exeptions by subfunctions*/
    public void getNewCurrentTile()throws Exception{
        if(callgetNewCurrentTile){
            currenttiles.remove();
        }
        do{
            long combinedseed = players.get(0).requestNextRandomNumber();

            for(int n1 = 1; n1 < players.size(); n1++){
                combinedseed = combinedseed ^ players.get(n1).requestNextRandomNumber();
            }

            currenttiles.add(new AdvancedTile(generator.generateTile(combinedseed)));
        }
        while(currenttiles.size() < conf.getNumPlayers()+1);
    } 

    /** fill CurrentTile queue 
    @throws Exception throws Exeptions by subfunctions*/
    public void fillCurrentTile()throws Exception{
        do{
            long combinedseed = players.get(0).requestNextRandomNumber();
            players.get(0).resetcallnextrandomnumber();

            for(int n1 = 1; n1 < players.size(); n1++){
                combinedseed = combinedseed ^ players.get(n1).requestNextRandomNumber();
                players.get(n1).resetcallnextrandomnumber();
            }

            currenttiles.add(new AdvancedTile(generator.generateTile(combinedseed)));
        }
        while(currenttiles.size() < conf.getNumPlayers()+1);
    }

    /** getter for the points given by the board
    @return the points*/
    public HashMap<Integer, Integer> getpoints(){
        return board.countPoints();
    }

    /** getter for the id of the current player
    @return id of the current player*/
    public int getCurrentPlayerID(){
        return idofcurrentplayer;
    }

    /** getter for the currenttiles queue 
    @return the player queue*/
    public Queue<AdvancedTile> getTilesToCome(){
        return currenttiles;
    }
    
    /** getter for the current tile
    @return returns the most upper tile in the queue*/
    public AdvancedTile getCurrentTile(){
        return currenttiles.peek();
    }

    /** getter for the colors of the player
    @return List of colors*/
    public List<Color> getColorsOfPlayers(){
        return colors;
    }

    /** getter for the number of player
    @return returns the number of player*/
    public int getNumberOfPlayers() {
        return players.size();
    }

    /** retunrs a list of all the player 
    @return List of the player*/
    public List<AdvancedPlayer> getPlayerList(){
        return players;
    }

    /** gerts the number of cards left to play
    @return number of cards*/
    public int getNumberOfCardsToPlay(){
        return cardstoplay;
    }

    /** gets the names of alle the player
    @return Gives back a list of strings*/
    public List<String> getNames(){
        List<String> names = new LinkedList<>();
        for(int n1 = 0; n1 < conf.getNumPlayers(); n1++){
            names.add(players.get(n1).getName());
        }
        return names;
    }

    /** getter for the baord
    @return returns the board*/
    public Board getBoard() {
        return board;
    }
}