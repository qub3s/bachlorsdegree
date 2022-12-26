package doerfer;

import com.kitfox.svg.SVGElementException;

import doerfer.preset.*;
import doerfer.preset.graphics.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static java.lang.Math.min;


/**
 * Class AdvancedGameView
 * Main method for input and (graphical) output. Has its own Master-Board which is updated and shown.
 *
 * @author Jannis Fischer
 */
public class AdvancedGameView implements GameView {

    /**
     * Observer object which coordinates all turns in the game and controls the GameView
     */
    private final Observer o;

    /**
     * Master-game-board. From this board, all objects are shown.
     */
    private static Board board;

    /**
     * Scale in y-direction of all tiles on the board. Default: 1f. Use 0.625 vor "3D-view"
     */
    private float yScale;

    /**
     * GPanel which is responsible for the graphics. Holds one GGroup each for the mainLayer and the HUDLayer
     */
    private static GPanel panel;

    /**
     * MainLayer, on which the board is shown. Is part of GPanel. Also, mouse listener are added to mainLayer of objects of it.
     */
    private static GGroup mainLayer;

    /**
     * HUDLayer, on which the whole user-interface is shown, eg. card-stack, player-icons, ...
     */
    private static GGroup HUDLayer;

    /** The frame, on which the mainLayer and the HUDLayer are shown */
    private static JFrame frame;

    /**
     * List for the upcoming tiles on the stack. Just for drawing purposes, as these AdvancedTiles are non-interactive objects.
     */
    private final List<AdvancedTile> nextnewTiles = new ArrayList<>();

    /**
     * Tile on the top of the stack. When left-clicking on a dummy tile, this tile is placed on the specific position.
     */
    private AdvancedTile newTile;

    /**
     * The rotation of the AdvancedTile "newTile" on top of the stack is saved.
     */
    private int tileRotation;

    /**
     * Default zoom of the MainLayer (the board), when starting the game or pressing "0"
     */
    private final float defaultZoom = 2f;

    /**
     * Default x-position of the viewpoint on the mainLayer (board)
     */
    private final int defaultX = 1000;

    /**
     * Default y-position of the viewpoint on the mainLayer (board)
     */
    private final int defaultY = 300;

    /**
     * Current x-coordinate of the viewpoint on the mainLayer (board)
     */
    private int shiftX = defaultX;

    /**
     * Current y-coordinate of the viewpoint on the mainLayer (board)
     */
    private int shiftY = defaultY;

    /**
     * Current zoom-factor of the viewpoint on the mainLayer (board)
     */
    private float scale = defaultZoom;

    /**
     * List of the PlayerIcons shown in the bottom left corner. Used to translate (highlight) the active player
     */
    private final List<GElement> drawablePlayerList = new ArrayList<>();

    /**
     * Player whose turn it currently is.
     */
    private Player activePlayer;

    /**
     * Remembers if the last move failed (e.g. the user tried to place the new/current Tile on a position,
     * where it is possible, but the rotation is wrong). Then no new tile is used
     */
    private boolean lastMoveFailed = false;

    /**
     * List von the DummyTiles on the stack. Is used to remove those dummys, if there are fewer cards on the stack
     */
    private final List<DummyTile> dummysOnStack = new ArrayList<>();          //save dummyTiles on the visual stack for later removal

    /**
     * If the game is over, this variable is true
     */
    private boolean gameOver = false;

    /**
     * GText object which indicated how many dummy (unrevealed) tiles are on the stack. Is shown, when the number is greater 3
     */
    private final GText text = new GText(1350,800,"");

    /**
     * Saves the 3D-Overlay of the new tile (the top tile on the stack) to remove it from the HUDLayer, when it is needed
     */
    private GElement overlayFirst;

    /**
     * List of all 3D-Overlays of the revealed tiles on the stack, but not the top one. Is used to remove them from the
     * HUDLayer, when it is needed
     */
    private final List<GElement> overlayList = new ArrayList<>();

    /**
     * Saves, which of the players where skipped to show it on the board.
     */
    private final boolean[] playerSkipped;

    /**
     * Saves the drawable objects, when a player is skipped. Is used to remove the objects from the HUDLayer, when its needed
     */
    private final List<GElement> skipOverlayList = new ArrayList<>();

    /**
     * Saves the current points of all players.
     */
    private final Integer[] points;

    /**
     * List of text elements which show the points of the players.
     */
    private final List<GText> drawablePointList = new ArrayList<>();

    /**
     * List of GElements to remove them from the mainLayer when its needed
     */
    private final List<GElement> mainLayerOverlays= new ArrayList<>();

    /**
     * True, if 3D vision of the board is used
     */
    private boolean threeD = true;

    /**
     * Safes the absolute Path
     */
    private final String path;

    /**
     * Constructor. Sets most of the used class variables, creates the HUD-Objects like the player icons and the dummys
     * on the stack and creates the JFrame which is shown
     * @param ob Observer which controls the GameView
     * @throws IOException Exception from addChild and GElement
     * @throws SVGElementException Exception from GPanel
     */
    public AdvancedGameView(Observer ob) throws IOException, SVGElementException {

        //////////////////////////// FILL CLASS ATTRIBUTES AND ADD LISTENERS ////////////////////////////////////
        o = ob;
        board = ob.getBoard();
        panel = new GPanel();
        mainLayer = panel.getLayerMain();
        frame = new JFrame();
        HUDLayer = panel.getLayerHUD();
        playerSkipped = new boolean[o.getNumberOfPlayers()];
        //playerSkipped = new boolean[]{true,true,true,true};
        points = new Integer[o.getNumberOfPlayers()];
        path = new File( "" ).getAbsolutePath();

        MouseAdapter tileMA = new AdvancedMouseWheelListener();
        AdvancedKeyListener aKL = new AdvancedKeyListener();
        frame.addKeyListener(aKL);
        frame.addMouseWheelListener(tileMA);
        if(threeD) {
            yScale = 0.625f;
        }
        else {
            yScale = 1;
        }


        //##################################################################################

        //################# CREATE HUD-OBJECTS LIKE PLAYER-ICONS AND STACK ###################

        createPlayerIcons();

        int dummysToDraw = o.getNumberOfCardsToPlay()-o.getNumberOfPlayers()-1; //Text indicating how many dummys on stack
        if(dummysToDraw > 3) {
            text.setText(dummysToDraw+" x");
        }
        text.setFontSize(50);
        text.setFill(Color.gray);
        HUDLayer.addChild(text);
        frame.setVisible(true);

        for(int i = 3; i > 0; i--) {                           //add Dummys on visual stack
            DummyTile stackDummy = new DummyTile(0,0);
            dummysOnStack.add(stackDummy);
            AdvancedDrawableTile dummyADT = stackDummy.getVisualTile(panel);
            dummyADT.setStrokeWidth(1f);
            dummyADT.transform().scale(2.5f,2.5f*0.625f);
            GElement overlay = new GElement(new File(path + "/CustomGraphics/3DOverlay.svg"));
            //GElement overlay = new GElement(new File("src/doerfer/graphics/3DOverlay.svg"));
            overlay.transform().scale(0.48f,0.48f/0.625f);
            overlay.transform().translate(-0.45f,13.5f);
            dummyADT.addChild(overlay);
            dummyADT.transform().translate(1450,640+i*30);
            dummyADT.setOpacity(1);
            HUDLayer.addChild(dummyADT);
        }

        //################################################################################
        frame.addComponentListener(new ComponentAdapter() {         //Used to have the correct scale when window size is changed
            /**
             * Invoked when the component's size changes.
             *
             * @param e ComponentEvent
             */
            @Override
            public void componentResized(ComponentEvent e) {
                panel.updateScale();
            }
        });
        frame.add(panel);
        frame.pack();
        frame.repaint(); //first repaint
        panel.updateScale();
        frame.repaint(); // repaint, because after calling updateScale() you should always call repaint
        Dimension d = new Dimension(1600, 900);
        frame.setMinimumSize(d);
        frame.setVisible(true);
        panel.updateScale();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Creates the PlayerIcons and adds them to the HUDLayer
     * Also adds the text which shows the points of the specific player
     * @throws IOException from GElement
     */
    public void createPlayerIcons() throws IOException {
        if (o.getNumberOfPlayers() > 4) {
            System.out.println("TOO MANY PLAYERS");
            System.exit(0);
        }
        for (int i = 0; i < o.getNumberOfPlayers(); i++) {
            GElement drawablePlayer;                                  //Load and save icon
            switch(i) {
                case 0:
                    //drawablePlayer = new GElement(new File("src/doerfer/graphics/Player_Blue.svg"));
                    drawablePlayer = new GElement(new File( path + "/CustomGraphics/Player_Blue.svg"));
                    break;
                case 1:
                    //drawablePlayer = new GElement(new File("src/doerfer/graphics/Player_Green.svg"));
                    drawablePlayer = new GElement(new File( path + "/CustomGraphics/Player_Green.svg"));
                    break;
                case 2:
                    //drawablePlayer = new GElement(new File("src/doerfer/graphics/Player_Red.svg"));
                    drawablePlayer = new GElement(new File( path + "/CustomGraphics/Player_Red.svg"));
                    break;
                case 3:
                    //drawablePlayer = new GElement(new File("src/doerfer/graphics/Player_Yellow.svg"));
                    drawablePlayer = new GElement(new File( path + "/CustomGraphics/Player_Yellow.svg"));
                    break;
                case 4:
                    //drawablePlayer = new GElement(new File("src/doerfer/graphics/Player_Pink.svg"));
                    drawablePlayer = new GElement(new File( path + "/CustomGraphics/Player_Pink.svg"));
                    break;
                case 5:
                    //drawablePlayer = new GElement(new File("src/doerfer/graphics/Player_Cyan.svg"));
                    drawablePlayer = new GElement(new File( path + "/CustomGraphics/Player_Cyan.svg"));
                    break;
                default:
                    //drawablePlayer = new GElement(new File("src/doerfer/graphics/Player_Black.svg"));
                    drawablePlayer = new GElement(new File( path + "/CustomGraphics/Player_Black.svg"));
                    break;
            }
            drawablePlayer.transform().scale(0.5f);
            drawablePlayer.transform().translate(50+i*100,800);
            HUDLayer.addChild(drawablePlayer);
            drawablePlayerList.add(drawablePlayer);

            GText pointText = new GText((i+1)*100,890,"0");         //Text which shows the points of the player
            pointText.setAlignment(TextAnchor.MIDDLE);
            pointText.setFontSize(35);
            pointText.setFill(Color.black);
            HUDLayer.addChild(pointText);
            drawablePointList.add(pointText);
        }
    }

    /**
     * Sets the newTile to null. Is used to tell the GameView, that it is possible to get a newTile
     */
    public void setNewTileNull(){
        newTile = null;
    }

    /**
     * Sets the players and the colors of the specific player. Not used
     * @param pList List of players
     * @param cList List of colors in the same order as the players
     */
    @Override
    public void setPlayers(List<Player> pList, List<Color> cList) {
    }

    /**
     * Updates, which player is the active one. Also highlights the specific player-icon and de-highlights the previous.
     * @param player Player which is active
     */
    @Override
    public void setActivePlayer(Player player){
        if(activePlayer != null) {                      //get old Player Icon to old position
            int oldID = ((AdvancedPlayer) activePlayer).getId()-1;
            drawablePlayerList.get(oldID).transform().translate(50+oldID*100,800);

        }
        activePlayer = player;                          //highlight active player
        int id = ((AdvancedPlayer) player).getId()-1;   //-1 because of different way of counting
        drawablePlayerList.get(id).transform().translate(50+id*100,760);
        frame.repaint();
    }

    /**
     * If a player is skipped or not skipped anymore, this method should be called to save and show this
     * @param player Player who is skipped or not
     * @param b true, if player is skipped, else false
     */
    @Override
    public void setPlayerSkipped(Player player, boolean b){
        int ID = ((AdvancedPlayer) player).getId();
        playerSkipped[ID-1] = b;
    }

    /**
     * Is called to place a Tile on the board. If this is not possible, the screen is lighting red for a short time,
     * indicating tha move wasn't valid. If it was valid, the player is called to make their move.
     * @param tile The advancedTile that should be placed
     * @param tilePlacement TilePlacement with the position and the rotation of the tile to be placed
     * @param player The player who wants to place the tile
     */
    @Override
    public void placeTile(Tile tile, TilePlacement tilePlacement, Player player) {
        if (tile instanceof AdvancedTile) {
            if(tilePlacement != null){
                if (!board.addTile((AdvancedTile) tile,tilePlacement,((AdvancedPlayer) player).getId())) {
                    //addTile returns falls, if move isn't valid
                    lastMoveFailed = true;
                    showInvalidMove();         //red screen for invalid move
                }
            }
            if(!lastMoveFailed) {           //if move is valid, the player makes his move
                o.getCurrentPlayer().makemove(tilePlacement);
                try {
                    o.restOfMove();
                } catch (Exception e) {
                    System.out.println("Error in playerMove");
                    System.exit(0);
                }
            }
            else {
                lastMoveFailed = false;
            }
        }
        else {
            System.out.println("Error: Tried to set a Tile which isn't an AdvancedTile");
            System.exit(0);
        }
        try {
            showBoard();                    //show board to show the latest changes
        } catch (IOException e) {
            System.out.println("IOError: Can't read or write input/output file");
            System.exit(0);
        }
        //o.setcancontinue();
    }

    /**
     * Sets the viewpoint of the mainLayer to the position of the given tilePlacement
     * @param tP the tilePlacement with the position, which tile should be in focus
     */
    @Override
    public void focus(TilePlacement tP) {
        shiftX = -(int) DrawableTileComponent.WIDTH * 3 / 4 * tP.getColumn()+frame.getWidth()/2;
        shiftY = -(int) DrawableTileComponent.HEIGHT * tP.getRow() - (int) DrawableTileComponent.HEIGHT / 2 * (Math.abs(tP.getColumn() % 2))+frame.getHeight()/2;
    }

    /**
     * Sets the uncovered tiles. Not used.
     * @param list The list of the uncovered tiles
     */
    @Override
    public void setUncoveredTiles(List<Tile> list) {

    }

    /**
     * Sets how many tiles are left. Not used, as the GameView asks the observer.
     * @param i number of tiles left
     */
    @Override
    public void setTilesLeft(int i) {

    }

    /**
     * Is called to tell the GameView, that the game is over or not.
     * If so, the EndScreen is shown.
     * @param b true if the game is over, false else
     */
    @Override
    public void setGameOver(boolean b) {
        gameOver = b;
        if (b) {
            for (GElement e : HUDLayer) {       //All Elements from HUDLayer are invisible now
                e.setOpacity(0);
            }
            for (AdvancedTile at : board.getTiles()) {
                if(at instanceof DummyTile) {
                    try {
                        at.getVisualTile(panel).setOpacity(0);      //all dummys are invisible now
                    } catch (IOException e) {
                        System.out.println("IOError: Can't read or write input/output file");
                        System.exit(0);
                    }
                }
                if(at instanceof AdvancedTilePlaced) {
                    try {                                           //all placed tiles are de-highlighted
                        at.getVisualTile(panel).setOpacity(0.5f);
                    } catch (IOException e) {
                        System.out.println("IOError: Can't read or write input/output file");
                        System.exit(0);
                    }
                }
            }

            //############################ SCOREBOARD ###########################################

            GRect scoreBackground = new GRect(1400,0,400,900);
            scoreBackground.setFill(UIManager.getColor ( "Panel.background" ));
            HUDLayer.addChild(scoreBackground);
            List<Color> colorList = new ArrayList<>();
            colorList.add(Color.blue);
            colorList.add(Color.green);
            colorList.add(Color.red);
            colorList.add(Color.yellow);

            List<String> nameList = o.getNames();

            for(int i = 0 ; i < o.getNumberOfPlayers(); i++) {
                GText colorText = new GText(1450,400+i*70,"");
                GText pointText = new GText(1700,400+i*70,"0");
                colorText.setFill(Color.gray);
                pointText.setFill(Color.gray);
                colorText.setFontSize(50);
                pointText.setFontSize(50);
                pointText.setAlignment(TextAnchor.MIDDLE);

                List<Integer> iList = Arrays.asList(points);
                int maxValue = Collections.max(iList);
                int indexMaxValue = iList.indexOf(maxValue);
                String cS = ""; //colorString
                if(nameList == null) {
                    Color c = colorList.get(indexMaxValue);
                    if (c.equals(Color.blue)) {
                        cS = "Blue";
                    }
                    else if (c.equals(Color.green)) {
                        cS = "Green";
                    }
                    else if (c.equals(Color.red)) {
                        cS = "Red";
                    }
                    else if (c.equals(Color.yellow)) {
                        cS = "Yellow";
                    }
                    else if (c.equals(Color.pink)) {
                        cS = "Pink";
                    }
                    else if (c.equals(Color.cyan)) {
                        cS = "Cyan";
                    }
                }
                else {
                    cS = nameList.get(indexMaxValue);
                }
                colorText.setText(cS + ": ");
                pointText.setText(Integer.toString(maxValue));
                points[indexMaxValue] = -1;
                HUDLayer.addChild(colorText);
                HUDLayer.addChild(pointText);

                if(i > 0) {
                    GRect tableLine = new GRect(1440,350+i*70,290,3);
                    tableLine.setFill(Color.gray);
                    HUDLayer.addChild(tableLine);
                }
            }

            GElement crown = null;
            try {
                //crown = new GElement(new File("src/doerfer/graphics/Crown.svg"));
                crown = new GElement(new File( path + "/CustomGraphics/Crown.svg"));
            } catch (IOException e) {
                System.out.println("IOError: Can't read or write input/output file");
                System.exit(0);
            }
            crown.transform().scale(1f);
            crown.transform().translate(1450,0);
            HUDLayer.addChild(crown);

            //##################################################################################
        }
    }

    /**
     * Displays, that an Error occurred and show the specific message
     * @param e Exception from which the message is shown
     */
    @Override
    public void displayError(Exception e) {
        String s = "Error occurred:\n";
        s+=e.getMessage();
        GText errorMessage = new GText(400,800,s);
        errorMessage.setFontSize(30);
        errorMessage.setFill(Color.red);
        HUDLayer.addChild(errorMessage);
    }

    /**
     * Sets and shows the tile placements, which are valid. Not used, as the board has dummy tiles, which indicates,
     * where a tile could be placed.
     * @param set The set of the valid tilePlacements
     */
    @Override
    public void setValidTilePlacements(Set<TilePlacement> set) {

    }

    /**
     * If the player tries to place a tile with an incorrect rotation, the background is turning red for about
     * half a second. Then it returns to the standard background color
     */
    public void showInvalidMove(){
        Color lightRed = new Color(255,150,150);
        frame.setBackground(lightRed);
        frame.repaint();
        int delay = 400;

        /*
          ActionListener
          Is used to return to the default background color after about half a second to handle busy waiting
         */
        ActionListener showPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setBackground(UIManager.getColor ( "Panel.background" ));
                frame.repaint();
            }
        };
        Timer timer = new Timer(delay, showPerformer);
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Returns the tilePlacement, where a human player want's to place the tile (after he clicked on a tile). Not used.
     * @return null
     */
    @Override
    public TilePlacement requestTilePlacement() {
        return null;
    }

    /**
     * Used to update the elements on the HUDLayer
     * @throws IOException Exception
     */
    public void showHUDLayer() throws IOException {
        setPoints(o.getpoints());
        //remove old Elements from Layer
        if (overlayFirst != null) {
            HUDLayer.removeChild(overlayFirst);
        }
        for(int i = 0; i < overlayList.size(); i++) {
            if(overlayList.get(i) != null) {
                nextnewTiles.get(i).getVisualTile(panel).removeChild(overlayList.get(i));
            }
        }
        for(AdvancedTile nextnewTile: nextnewTiles) {
            if(nextnewTile != null) {
                HUDLayer.removeChild(nextnewTile.getVisualTile(panel));    //remove old secondary tiles
            }
        }
        overlayList.clear();
        nextnewTiles.clear();


        if (newTile == null) { //get new Tile, if needed
            newTile = o.getCurrentTile();
            AdvancedPlayer advancedActivePlayer = (AdvancedPlayer) activePlayer;
            board.togglePlacableDummyTiles( newTile, advancedActivePlayer.getId() - 1);
            }
        else {              //else remove the visual tile form HUD, it will later be added again
            HUDLayer.removeChild(newTile.getVisualTile(panel));
        }

        Queue<AdvancedTile> que = o.getTilesToCome();
        for(int i = 0; i < min(o.getNumberOfPlayers(),(o.getNumberOfCardsToPlay()-1)); i++) {
            nextnewTiles.add((AdvancedTile) (que.toArray())[i+1]);                           //update secondary tiles

        }


        AdvancedDrawableTile aDT = newTile.getVisualTile(panel);

        for(int i = 0; i < nextnewTiles.size(); i++) {  //create visual Stack
            AdvancedTile nextnewTile = nextnewTiles.get(i);
            AdvancedDrawableTile nextADT = nextnewTile.getVisualTile(panel);
            nextADT.transform().scale(2.5f,2.5f*0.625f);

            //GElement overlay = new GElement(new File("src/doerfer/graphics/3DOverlay.svg"));
            GElement overlay = new GElement(new File( path + "/CustomGraphics/3DOverlay.svg"));
            overlay.transform().scale(0.48f,0.48f/0.625f);
            overlay.transform().translate(-0.45f,13.6f);
            overlayList.add(overlay);
            nextADT.addChild(overlay);
            nextADT.transform().translate(1450,640+(i-nextnewTiles.size()+1)*130);
            nextADT.setStroke(Color.gray);
        }
        //Create visual first Tile on the stack
        aDT.transform().rotate(60*tileRotation,DrawableTileComponent.WIDTH/2,DrawableTileComponent.HEIGHT/2);
        aDT.transform().scale(2.5f,2.5f*0.625f);

        //overlayFirst = new GElement(new File("src/doerfer/graphics/3DOverlay.svg"));
        overlayFirst = new GElement(new File( path + "/CustomGraphics/3DOverlay.svg"));
        overlayFirst.transform().scale(0.48f*2.5f,0.48f*2.5f);
        overlayFirst.transform().translate(-1.7f+1450f,21f+510+(-nextnewTiles.size()+1)*130);



        aDT.transform().translate(1450,510+(-nextnewTiles.size()+1)*130);
        aDT.setStroke(Color.gray);

        //remove the tiles and add them again to have them in the right order
        for(AdvancedTile i : nextnewTiles) {
            HUDLayer.removeChild(i.getVisualTile(panel));
        }

        if(!gameOver) {
            for(int i = nextnewTiles.size()-1; i >=0 ; i--) {
                HUDLayer.addChild(nextnewTiles.get(i).getVisualTile(panel));
            }
        }

        //removes the first tile and adds it, to have it on the top of the visual layer
        HUDLayer.removeChild(aDT);
        if(o.getNumberOfCardsToPlay() > 0 && !gameOver) {
            HUDLayer.addChild(aDT);
            HUDLayer.addChild(overlayFirst);
        }
        showDummysOnStack();
        showSkipped();
        frame.repaint();
    }

    /**
     * Updates the points of the players and shows them
     * @param pointMap HashMap of the playerID and the points
     */
    public void setPoints(HashMap<Integer,Integer> pointMap) {
        for(int i = 0; i < o.getNumberOfPlayers(); i++) {
            points[i] = pointMap.get(i+1);
            drawablePointList.get(i).setText(Integer.toString(points[i]));
            frame.repaint();
        }
    }

    /**
     * Shows the (up to three) Dummys on the stack and shows, if there are more dummys than shown
     * @throws IOException from getVisualTile
     */
    public void showDummysOnStack() throws IOException {
        int dummysToDraw = o.getNumberOfCardsToPlay()-o.getNumberOfPlayers()-1;
        if(dummysToDraw > 3) {
            text.setText(dummysToDraw+" x");
        }
        else {
            text.setText("");
        }
        for(int i = 0; i < min((4- o.getNumberOfCardsToPlay()+o.getNumberOfPlayers()),3); i++) {            //remove dummys, if there are only few left
            HUDLayer.removeChild(dummysOnStack.get(i).getVisualTile(panel));
        }
    }

    /**
     * Shows the "Skipped" Label on the screen, if a player is skipped.
     * @throws IOException from GElement
     */
    private void showSkipped() throws IOException {
        for(GElement e : skipOverlayList) {
            HUDLayer.removeChild(e);
        }
        skipOverlayList.clear();
        for(int i = 0; i < playerSkipped.length; i++) {
            if(playerSkipped[i]) {
                //GElement skipped = new GElement(new File("src/doerfer/graphics/Skipped.svg"));
                GElement skipped = new GElement(new File( path + "/CustomGraphics/Skipped.svg"));
                skipped.transform().translate(225+i*100,460);
                skipped.transform().scale(0.5f);
                skipped.transform().rotate(-50,550,800);
                skipOverlayList.add(skipped);
                HUDLayer.addChild(skipped);
            }
        }
    }

    /**
     * Updates the mainLayer with tiles of the board. Also adds overlays to the tiles if 3D-mode is enabled
     * @throws IOException Exception
     */
    public void showBoard() throws IOException {
        if(threeD) {
            yScale = 0.625f;
        }
        else {
            yScale = 1;
        }

        for (AdvancedTile aT: board.getTiles()) {       //remove old elements
            if (aT instanceof DummyTile) {
                mainLayer.removeChild(aT.getVisualTile(panel));
            }
        }
        for (GElement e : mainLayerOverlays) {
            mainLayer.removeChild(e);
        }
        if(threeD) {                                                            //Add overlays for 3D-view
            for(AdvancedTile aT : board.getTiles()) {
                if(aT instanceof AdvancedTilePlaced) {
                    int row = ((AdvancedTilePlaced) aT).getTilePlacement().getRow();
                    int column = ((AdvancedTilePlaced) aT).getTilePlacement().getColumn();
                    float xPos = DrawableTileComponent.WIDTH * 3 / 4 * column;
                    float yPos = DrawableTileComponent.HEIGHT * row + DrawableTileComponent.HEIGHT / 2 * (Math.abs(column % 2));

                    //display all three sides
                    if (board.getNeighbourBiomeSouthWest(row,column) == null && board.getNeighbourBiomeSouth(row,column) == null && board.getNeighbourBiomeSouthEast(row,column) == null) {
                        //GElement overlay = new GElement(new File("src/doerfer/graphics/3DOverlay.svg"));
                        GElement overlay = new GElement(new File( path + "/CustomGraphics/3DOverlay.svg"));
                        overlay.transform().scale(0.48f,0.48f/0.625f);
                        overlay.transform().translate(-0.45f+xPos,13.5f+yPos);
                        mainLayerOverlays.add(overlay);
                        mainLayer.addChild(overlay);
                    }
                    //display left and middle
                    else if (board.getNeighbourBiomeSouthWest(row,column) == null && board.getNeighbourBiomeSouth(row,column) == null) {
                        //GElement overlay = new GElement(new File("src/doerfer/graphics/3DOverlayLeft.svg"));
                        GElement overlay = new GElement(new File( path + "/CustomGraphics/3DOverlayLeft.svg"));
                        overlay.transform().scale(0.48f,0.48f/0.625f);
                        overlay.transform().translate(-0.45f+xPos,13.5f+yPos);
                        mainLayerOverlays.add(overlay);
                        mainLayer.addChild(overlay);
                    }
                    //display right and middle
                    else if (board.getNeighbourBiomeSouthEast(row,column) == null && board.getNeighbourBiomeSouth(row,column) == null) {
                        //GElement overlay = new GElement(new File("src/doerfer/graphics/3DOverlayRight.svg"));
                        GElement overlay = new GElement(new File( path + "/CustomGraphics/3DOverlayRight.svg"));
                        overlay.transform().scale(0.48f,0.48f/0.625f);
                        overlay.transform().translate(-0.45f+xPos,13.5f+yPos);
                        mainLayerOverlays.add(overlay);
                        mainLayer.addChild(overlay);
                    }
                    //display only middle
                    else if (board.getNeighbourBiomeSouth(row,column) == null) {
                        //GElement overlay = new GElement(new File("src/doerfer/graphics/3DOverlayMiddle.svg"));
                        GElement overlay = new GElement(new File( path + "/CustomGraphics/3DOverlayMiddle.svg"));
                        overlay.transform().scale(0.48f,0.48f/0.625f);
                        overlay.transform().translate(-0.45f+xPos,13.5f+yPos);
                        mainLayerOverlays.add(overlay);
                        mainLayer.addChild(overlay);
                    }
                    //display only left
                    else if (board.getNeighbourBiomeSouthWest(row,column) == null || board.getNeighbourBiomeSouthEast(row,column) == null) {
                        if (board.getNeighbourBiomeSouthWest(row,column) == null) {
                            //GElement overlay = new GElement(new File("src/doerfer/graphics/3DOverlayLeftPart.svg"));
                            GElement overlay = new GElement(new File( path + "/CustomGraphics/3DOverlayLeftPart.svg"));
                            overlay.transform().scale(0.48f,0.48f/0.625f);
                            overlay.transform().translate(-0.45f+xPos,13.5f+yPos);
                            mainLayerOverlays.add(overlay);
                            mainLayer.addChild(overlay);
                        }
                        //also display right
                        if (board.getNeighbourBiomeSouthEast(row,column) == null) {
                            //GElement overlay2 = new GElement(new File("src/doerfer/graphics/3DOverlayRightPart.svg"));
                            GElement overlay2 = new GElement(new File( path + "/CustomGraphics/3DOverlayRightPart.svg"));
                            overlay2.transform().scale(0.48f,0.48f/0.625f);
                            overlay2.transform().translate(-0.45f+xPos,13.5f+yPos);
                            mainLayerOverlays.add(overlay2);
                            mainLayer.addChild(overlay2);
                        }
                    }
                    //display only right
                    else if (board.getNeighbourBiomeSouthEast(row,column) == null) {
                        //GElement overlay = new GElement(new File("src/doerfer/graphics/3DOverlayRightPart.svg"));
                        GElement overlay = new GElement(new File( path + "/CustomGraphics/3DOverlayRightPart.svg"));
                        overlay.transform().scale(0.48f,0.48f/0.625f);
                        overlay.transform().translate(-0.45f+xPos,13.5f+yPos);
                        mainLayerOverlays.add(overlay);
                        mainLayer.addChild(overlay);
                    }
                }
            }
        }

        ArrayList<AdvancedTile> lastAddedTiles = board.getLastAddedTiles();
        DummyTile removedDummy = board.getLastRemovedTile();
        if (removedDummy != null) {      //removes DummyTile
            mainLayer.removeChild(removedDummy.getVisualTile(panel));
        }

        for (AdvancedTile j : lastAddedTiles) {         //add new tiles
            if (j instanceof AdvancedTilePlaced) {
                TilePlacement tP = ((AdvancedTilePlaced) j).getTilePlacement();
                AdvancedDrawableTile dT = j.getVisualTile(panel);
                dT.transform().translate(DrawableTileComponent.WIDTH * 3 / 4 * tP.getColumn(), DrawableTileComponent.HEIGHT * tP.getRow() + DrawableTileComponent.HEIGHT / 2 * (Math.abs(tP.getColumn() % 2)));
                mainLayer.addChild(dT);
            } else if (j instanceof DummyTile) {
                TilePlacement tP = ((DummyTile) j).getTilePlacement();
                AdvancedDrawableTile dT = j.getVisualTile(panel);
                if (dT.isMouseListenerEmpty()) {
                    dT.setMouseListener(new DummyMA(dT));
                }
                dT.transform().translate(DrawableTileComponent.WIDTH * 3 / 4 * tP.getColumn(), DrawableTileComponent.HEIGHT * tP.getRow() + DrawableTileComponent.HEIGHT / 2 * (Math.abs(tP.getColumn() % 2)));
                mainLayer.addChild(dT);
            }
        }
        for (AdvancedTile aT: board.getTiles()) {
            if (aT instanceof DummyTile) {
                mainLayer.addChild(aT.getVisualTile(panel));
            }
        }
        mainLayer.transform().translate(shiftX,shiftY);
        mainLayer.transform().scale(scale,scale*yScale);
        frame.repaint();

    }

    /**
     * Class DummyMA
     * MouseListener which is added to each DummyTile to detect left-clicks then placing a tile
     * @author Jannis Fischer
     */
    private class DummyMA extends MouseAdapter {
        /**
         * The drawableTile the MouseAdapter is attached to
         */
        AdvancedDrawableTile advancedDrawableTile;

        /**
         * Constructor
         * @param dT The AdvancedDrawableTile the MouseAdapter is attached to
         */
        public DummyMA (AdvancedDrawableTile dT) {
            advancedDrawableTile = dT;
        }

        /**
         * Main-method of the MouseAdapter. Is called, when one clicked on the dummyTile
         * @param e the event to be processed
         */
        public void mouseClicked(MouseEvent e) {
            //only when the game is not over, the dummy is activated, and it is a human players turn
            if(e.getClickCount() > 0 && ((DummyTile) advancedDrawableTile.getAdvancedTile()).isActivated() && !gameOver && !o.getCurrentPlayer().getIsAIPlayer()) {
                TilePlacement oldTP = ((DummyTile) advancedDrawableTile.getAdvancedTile()).getTilePlacement();
                TilePlacement tP = new TilePlacement(oldTP.getRow(),oldTP.getColumn(),tileRotation);

                placeTile(newTile,tP,activePlayer);
                try {
                    HUDLayer.removeChild(newTile.getVisualTile(panel));
                } catch (IOException ex) {
                    System.out.println("IOError: Can't read or write input/output file");
                    System.exit(0);
                }

                newTile = null;
                tileRotation = 0;
                try {
                    showHUDLayer();
                } catch (IOException ex) {
                    System.out.println("IOError: Can't read or write input/output file");
                    System.exit(0);
                }
            }
        }

        /**
         * When the mouse enters an activated dummyTile, the opacity is decreased to highlight it
         * @param evt the event to be processed
         */
        public void mouseEntered(MouseEvent evt) {
            if(((DummyTile) advancedDrawableTile.getAdvancedTile()).isActivated() && !gameOver) {
                advancedDrawableTile.setOpacity(0.3f);
                frame.repaint();
            }
        }

        /**
         * When the mouse leaves an activated dummyTile it is de-highlighted, so it is shown in the default way
         * @param evt the event to be processed
         */
        public void mouseExited (MouseEvent evt) {
            if(((DummyTile) advancedDrawableTile.getAdvancedTile()).isActivated() && !gameOver) {
                advancedDrawableTile.setOpacity(0.6f);
                frame.repaint();
            }
        }
    }

    /**
     * Class AdvancedMouseWheelListener
     * Listens to the mouseWheel to rotate the visual tile at the top of the stack and with control+mouseWheel one can
     * zoom in and out
     * @author Jannis Fischer
     */
    private class AdvancedMouseWheelListener extends MouseAdapter {
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            if(e.isControlDown()) {             //zoom in and out
                if (e.getWheelRotation() > 0 && scale > 1) {
                    scale-=0.05;
                }
                else if (e.getWheelRotation() < 0 && scale < 4) {
                    scale +=0.05;
                }
                mainLayer.transform().scale(scale,scale*yScale);
                panel.updateScale();
                frame.repaint();
            }
            else {
                //rotate tile
                if (e.getWheelRotation() < 0)
                {
                    if(tileRotation > 0) {
                        tileRotation--;
                    }
                    else {
                        tileRotation = 5;
                    }
                }
                else
                {
                    if(tileRotation < 5) {
                        tileRotation++;
                    }
                    else {
                        tileRotation = 0;
                    }
                }
            }
            try {
                showHUDLayer();
            } catch (IOException ex) {
                System.out.println("IOError: Can't read or write input/output file");
                System.exit(0);
            }
        }
    }

    /**
     * Class AdvancedKeyListener
     *
     * Used for keyboard input like navigation(wasd,up,down,left,right,+,-,0), to take Screenshots(F2)
     * and to toggle 2D/3D vision
     *
     * @author Jannis Fischer
     */
    private class AdvancedKeyListener implements KeyListener {
        /**
         * Invoked when a key has been typed. Not used.
         *
         * @param e the event to be processed
         */
        @Override
        public void keyTyped(KeyEvent e) {
        }

        /**
         * Invoked when a key has been pressed.
         * Used for the navigation on the mainLayer (board) (WASD, UP, DOWN, LEFT, RIGHT, 0), scaling (+,-),
         * taking Screenshots (F2) and to toggle 2D/3D vision (T)
         *
         * @param e the event to be processed
         */
        @Override
        public void keyPressed(KeyEvent e) {
            //WASD + up,down,left,right
            if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
                shiftX-=10;
            }
            else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
                shiftX+=10;
            }
            else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                shiftY+=10;
            }
            else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                shiftY-=10;
            }
            //Original view
            else if (e.getKeyCode() == KeyEvent.VK_0) {
                shiftX = defaultX;
                shiftY = defaultY;
                scale = defaultZoom;
            }
            //zoom in and out
            else if (e.getKeyCode() == KeyEvent.VK_PLUS) {
                if (scale < 4) {
                    scale+=0.05;
                }
            }
            else if(e.getKeyCode() == KeyEvent.VK_MINUS) {
                if (scale > 1) {
                    scale-=0.05;
                }
            }
            //toggle 2D/3D vision
            else if(e.getKeyCode() == KeyEvent.VK_V) {
                threeD = !threeD;
                if(threeD) {
                    yScale = 0.625f;
                }
                else {
                    yScale = 1;
                }
                try {
                    showBoard();
                } catch (IOException ex) {
                    System.out.println("IOError: Can't read or write input/output file");
                    System.exit(0);
                }
            }
            //take Screenshot
            else if (e.getKeyCode() == KeyEvent.VK_F2) {
                BufferedImage image = new BufferedImage(frame.getWidth(),frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = image.createGraphics();
                //graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                frame.print(graphics2D);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println("Screenshot: " + dtf.format(now)+".jpeg");
                //File directory = new File("out/screenshots");
                File directory = new File( path + "/screenshots" );
                if(!directory.exists()) {
                    if (!directory.mkdir()) {
                        System.out.println("Folder could not be created");
                        System.exit(0);
                    }

                }
                image = image.getSubimage(7,30,1281,658);
                try {
                    ImageIO.write(image,"jpeg", new File(path+"/screenshots/"+dtf.format(now)+".jpeg"));
                } catch (IOException ex) {
                    System.out.println("IOError: Can't write input file: Screenshot");
                }
            }
            mainLayer.transform().translate(shiftX,shiftY);
            mainLayer.transform().scale(scale,scale*yScale);
            panel.updateScale();
            frame.repaint();
        }

        /**
         * Invoked when a key has been released. Not used.
         *
         * @param e the event to be processed
         */
        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
