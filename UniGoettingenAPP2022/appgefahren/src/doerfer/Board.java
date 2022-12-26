package doerfer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import doerfer.preset.Biome;
import doerfer.preset.Tile;
import doerfer.preset.TilePlacement;

/**
 * Class Board
 * A Class that represents the Board
 * The Board is adaptive in all 4 Directions
 * Holds an ArrayList of the AdvancedTiles
 * @author Philipp Schäfer
 */
public class Board
{
    /**The AdvancedGameConfiguration of witch the Board is created*/
    private final AdvancedGameConfiguration config;
    /**The ArrayList of the Tiles currently on the board*/
    private final ArrayList<AdvancedTile> currentTilesOnBoard;
    /**The ArrayList of the Tiles lastly added to the board*/
    private final ArrayList<AdvancedTile> lastAddedTiles;
    /**The ArrayList of the Tiles lastly removed from the board*/
    private DummyTile lastRemovedTile;
    /**A boolean that shows if the actions are part of the Initiallyze or not*/
    private boolean inInit;

    /**
     * Constructer for the Board with a given Config-File-String
     * @param conf the AdvancedGameConfiguration to load the initial data from
     */
    public Board( AdvancedGameConfiguration conf) 
    {
        //initialyze the board with the given Config
        this.config = conf;
        currentTilesOnBoard = new ArrayList<>();
        lastAddedTiles = new ArrayList<>();
        lastRemovedTile = null;
        inInit = true;
        initialyze();
    }

    /**TileGenerator generator
     * a Helper Method to initialyze the Board with the config, only called by the Constructers
     */
    private void initialyze()
    {
        List<TilePlacement> preplacedPlacements = config.getPreplacedTilesPlacements();
        List<Tile> preplacedTiles = config.getPreplacedTiles();
        if( preplacedPlacements.size() != preplacedTiles.size() )
        {
            throw new IllegalArgumentException( "The amount of preplacedPlacement and preplacedTiles aren't the same in the Config." );
        }
        for( int i = 0; i < preplacedTiles.size(); i++ )
        {
            if( !( preplacedTiles.get( i ) instanceof AdvancedTile ) )
            {
                throw new IllegalArgumentException( "Preplaced Tiles can't be casted to AdvancedTiles." );
            }
            AdvancedTile currentTile = (AdvancedTile) preplacedTiles.get( i );
            TilePlacement currentPlacement = preplacedPlacements.get( i );
            int currentPlayerID = config.getPreplacedTilesPlayerIDs().get( i );
            addTile( currentTile, currentPlacement, currentPlayerID );                  //adds preplaced Tiles
        }
        inInit = false;                             //init is over 
    }

    /**
     * Returns a tile from a given row and column, Can be an AdvancedTilePlaced or a DummyTile
     * @param row the row of the Tile
     * @param column the column of the Tile
     * @return the AdvancedTilePlaced from the given coordinates, null if given coordinates have no Tile or a DummyTile if given coordinates contain a DummyTile
     */
    public AdvancedTile getTile( int row, int column)
    {
        for (AdvancedTile advancedTile : currentTilesOnBoard) {
            if (advancedTile instanceof AdvancedTilePlaced)        //actual Tile
            {
                AdvancedTilePlaced tilePlacedInLoop = (AdvancedTilePlaced) advancedTile;
                if (tilePlacedInLoop.getTilePlacement().getRow() == row && tilePlacedInLoop.getTilePlacement().getColumn() == column)      //is Tile at wanted coordinates
                {
                    return tilePlacedInLoop;
                }
            } else if (advancedTile instanceof DummyTile)        //DummyTile
            {
                DummyTile dummyInLoop = (DummyTile) advancedTile;
                if (dummyInLoop.getTilePlacement().getRow() == row && dummyInLoop.getTilePlacement().getColumn() == column)            //is Tile at wanted coordinates
                {
                    return dummyInLoop;
                }
            }
        }
        return null;                //no Tile at the coordinates
    }

    /**
     * Getter for the List of Tiles
     * @return the List of Advanced Tiles containing a DummyTile or a AdvancedTilePlaced
     */
    public ArrayList<AdvancedTile> getTiles()
    {
        return currentTilesOnBoard;
    }

    /**
     * Getter for the List of Tiles that were addded with the last addTile(), DummyTiles and AdvancedTilePlaced
     * @return an ArrayList of Tiles that were added last
     */
    public ArrayList<AdvancedTile> getLastAddedTiles()
    {
        return lastAddedTiles;
    }

    /**
     * Getter for the DummyTile that was removed with the last addTile()
     * @return the DummyTile that was removed
     */
    public DummyTile getLastRemovedTile()
    {
        return lastRemovedTile;
    }

    /**
     * Adds a given Tile to the given parameters
     * @param tile the tile to place
     * @param row the row to place the tile in
     * @param column the column to place the tile in
     * @param rotation the rotation of the Tile
     * @param tilePlayerID the Id of the Player who's tile this is
     * @return a boolean if the Tile was added or not
     */
    public boolean addTile( AdvancedTile tile, int row, int column, int rotation, int tilePlayerID)
    {
        return addTile( tile, new TilePlacement(row, column, rotation), tilePlayerID);
    }

    /**
     * Adds a given Tile to the given placement
     * @param tile the tile to place
     * @param placement the TilePlacement to place the tile in
     * @param tilePlayerID the Id of the Player who's tile this is
     * @return a boolean if the Tile was added or not
     */
    public boolean addTile( AdvancedTile tile, TilePlacement placement, int tilePlayerID )
    {
        for( int i = 0; i < currentTilesOnBoard.size(); i++ )
        {
            if( currentTilesOnBoard.get( i ) instanceof AdvancedTilePlaced )
            {
                AdvancedTilePlaced tileInLoop = (AdvancedTilePlaced) currentTilesOnBoard.get(i);
                if( tileInLoop.getTilePlacement().getColumn() == placement.getColumn() && tileInLoop.getTilePlacement().getRow() == placement.getRow() )
                {
                    return false;                   //there is already a tile at the coordinates
                }
            }
            else if( currentTilesOnBoard.get( i ) instanceof DummyTile )                   //DummyTile
            {
                DummyTile dummyTileInLoop = ( DummyTile ) currentTilesOnBoard.get( i );
                if( dummyTileInLoop.getTilePlacement().getColumn() == placement.getColumn() && dummyTileInLoop.getTilePlacement().getRow() == placement.getRow() )      //Tile should be placed on this DummyTile
                {
                    if( !inInit )           //not called in the initialyze() method
                    {
                        if( placementIsViable( tile, placement, tilePlayerID) )             //placing is not ageinst gamerules
                        {
                            lastRemovedTile = null;         //resetting methods for the graphic so the tiles that changed can be updated
                            lastAddedTiles.clear();
                            placeDummyTilesAround( placement.getRow(), placement.getColumn() );
                            lastRemovedTile = dummyTileInLoop;
                            currentTilesOnBoard.remove( i );        //removes DummyTile were Tile is about to be placed
                            AdvancedTilePlaced currentTilePlaced = new AdvancedTilePlaced( tile, placement, tilePlayerID);  //Tile-Placed is genereated
                            lastAddedTiles.add( currentTilePlaced );
                            currentTilesOnBoard.add( currentTilePlaced );
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                        
                    }
                    else            //called by initialyze so don't have to check if everything is viable
                    {
                        currentTilesOnBoard.remove( i );
                        break;
                    }
                }
            }
        }
        if( inInit )                //in init und Tile existiert noch nicht
        {
            AdvancedTilePlaced tileToPlace = new AdvancedTilePlaced(tile, placement, tilePlayerID);
            currentTilesOnBoard.add( tileToPlace );
            lastAddedTiles.add( tileToPlace );
            return placeDummyTilesAround( placement.getRow(), placement.getColumn() );
        }
        else                        //Board dosen't contain DummyTile to place the tile on
        {
            return false;
        }
    }

    /**
     * Method that checks if it is okay to place a given Tile at a given place
     * @param tile the tile to place
     * @param placement the placement to put the tile in
     * @param tilePlayerID the ID of the Player whose Tile this is
     * @return a boolean if it is viable to place a Tile there
     */
    public boolean placementIsViable( AdvancedTile tile, TilePlacement placement, int tilePlayerID )
    {
        AdvancedTile[] neighbours = getNeighbours(placement.getRow(), placement.getColumn());
        boolean idFits = false;                         //is there a Tile with the same ID next to this one?
        for (AdvancedTile neighbour : neighbours) {
            if (neighbour instanceof AdvancedTilePlaced) {
                AdvancedTilePlaced currentNeighbourPlaced = (AdvancedTilePlaced) neighbour;

                if (currentNeighbourPlaced.getPlayerID() == tilePlayerID) {
                    idFits = true;                      //found Tile with same ID
                }
            }
        }
        if( !idFits )
        {
            return false;
        }
        Biome[] neighbourBiomes = getNeighbourBiomes(placement.getRow(), placement.getColumn() );
        for( int i = 0; i < 6; i++ )
        {
            int idInList = ( i - placement.getRotation() ) % 6;         //need biome in that direction and need to include Rotation for that
            if( idInList < 0 )
            {
                idInList += 6;
            }
            
            if( tile.getEdge( idInList ) == Biome.WATER && ( neighbourBiomes[i] != null && neighbourBiomes[i] != Biome.WATER )
            ||  tile.getEdge( idInList ) != Biome.WATER && ( neighbourBiomes[i] != null && neighbourBiomes[i] == Biome.WATER )
            ||  tile.getEdge( idInList ) == Biome.TRAINTRACKS && ( neighbourBiomes[i] != null && neighbourBiomes[i] != Biome.TRAINTRACKS )
            ||  tile.getEdge( idInList ) != Biome.TRAINTRACKS && ( neighbourBiomes[i] != null && neighbourBiomes[i] == Biome.TRAINTRACKS ) )
            {                                                       //water is next to not water or Traintracks is next to not Traintracks
                return false;
            }
        }
        return true;                                                //found no error
    }

    /**
     * A private helper method that places DummyTiles around a given coordinate, only called by addTile
     * @param row the row of the coordinate
     * @param column the column of the coordinate
     * @return a boolean if it worked or not
     */
    private boolean placeDummyTilesAround( int row, int column)
    {
        if( ( column ) % 2 == 0)                       //important because it results in different neighbours
        {
            addDummyTile( row - 1, column);
            addDummyTile( row - 1, column + 1 );
            addDummyTile( row, column + 1 );
            addDummyTile( row + 1, column );
            addDummyTile( row, column - 1 );
            addDummyTile( row -1, column - 1);
            return true;
        }
        else
        {
            addDummyTile( row - 1, column );
            addDummyTile( row, column + 1 );
            addDummyTile( row + 1, column + 1 );
            addDummyTile( row + 1, column );
            addDummyTile( row + 1, column -1 );
            addDummyTile( row, column - 1 );
            return true;
        }
    }

    /**
     * Private Helper Mothod to add a DummyTile to a given location
     * @param row the row to add the DummyTile to
     * @param column the column to add the DummyTile to
     * @return a boolean if the Tile was set or not
     */
    private boolean addDummyTile( int row, int column)
    {
        //check if there is a Tile at that position
        for (AdvancedTile advancedTile : currentTilesOnBoard) {
            if (advancedTile instanceof DummyTile) {
                DummyTile dummyTileInLoop = (DummyTile) advancedTile;
                if (dummyTileInLoop.getTilePlacement().getRow() == row && dummyTileInLoop.getTilePlacement().getColumn() == column) {
                    return false;
                }
            } else if (advancedTile instanceof AdvancedTilePlaced) {
                AdvancedTilePlaced tilePlacedInLoop = (AdvancedTilePlaced) advancedTile;
                if (tilePlacedInLoop.getTilePlacement().getRow() == row && tilePlacedInLoop.getTilePlacement().getColumn() == column) {
                    return false;
                }
            }
        }
        DummyTile toAdd = new DummyTile( row, column );         //actuall adding of the DummyTile
        currentTilesOnBoard.add( toAdd );
        lastAddedTiles.add( toAdd );
        return true;
    }

    /**
     * Returns the Tiles around the given coordinates from north clockwise
     * @param row the row of the Tile the neighbours are looked for
     * @param column the column of the Tile the neighbours are looked for
     * @return an Array of the neighbouring AdvancedTiles or DummyTile
     */
    public AdvancedTile[] getNeighbours( int row, int column )
    {
        AdvancedTile[] neighbours = new AdvancedTile[6];
        if( column % 2 == 0)
        {
            neighbours[0] = getTile( row - 1, column);
            neighbours[1] = getTile( row - 1, column + 1 );
            neighbours[2] = getTile( row, column + 1 );
            neighbours[3] = getTile( row + 1, column );
            neighbours[4] = getTile( row, column - 1 );
            neighbours[5] = getTile( row -1, column - 1);
        }
        else
        {
            neighbours[0] = getTile( row - 1, column );
            neighbours[1] = getTile( row, column + 1 );
            neighbours[2] = getTile( row + 1, column + 1 );
            neighbours[3] = getTile( row + 1, column );
            neighbours[4] = getTile( row + 1, column -1 );
            neighbours[5] = getTile( row, column - 1 );
        }
        return neighbours;
    }

    /**
     * Getter for the adjacent Biome north
     * @param row the row of the Tile the neigthbour is wanted from
     * @param column the column of the Tile the neighbour is wanted from
     * @return the Biome that is adjacent in the north, null if there is no Tile or a DummyTile
     */
    public Biome getNeighbourBiomeNorth( int row, int column)
    {
        AdvancedTile tile = getNeighbours( row, column )[0];
        if( tile == null || tile instanceof DummyTile )             //DummyTile has no real Biomes
        {
            return null;
        }
        AdvancedTilePlaced tilePlaced = (AdvancedTilePlaced) tile;
        int idInList = ( 3 - tilePlaced.getTilePlacement().getRotation() ) % 6;         //edge on the oposit site, with rotation
        if( idInList < 0 )
        {
            idInList += 6;
        }
        return tile.getEdge( idInList );
    }

    /**
     * Getter for the adjacent Biome north east
     * @param row the row of the Tile the neigthbour is wanted from
     * @param column the column of the Tile the neighbour is wanted from
     * @return the Biome that is adjacent in the north east, null if there is no Tile or a DummyTile
     */
    public Biome getNeighbourBiomeNorthEast( int row, int column)
    {
        AdvancedTile tile = getNeighbours( row, column )[1];
        if( tile == null || tile instanceof DummyTile )             //DummyTile has no real Biomes
        {
            return null;
        }
        AdvancedTilePlaced tilePlaced = (AdvancedTilePlaced) tile;
        int idInList = ( 4 - tilePlaced.getTilePlacement().getRotation() ) % 6;         //edge on the oposit site, with rotation
        if( idInList < 0 )
        {
            idInList += 6;
        }
        return tile.getEdge( idInList );
    }

    /**
     * Getter for the adjacent Biome south east
     * @param row the row of the Tile the neigthbour is wanted from
     * @param column the column of the Tile the neighbour is wanted from
     * @return the Biome that is adjacent in the south east, null if there is no Tile or a DummyTile
     */
    public Biome getNeighbourBiomeSouthEast( int row, int column)
    {
        AdvancedTile tile = getNeighbours( row, column )[2];
        if( tile == null || tile instanceof DummyTile )             //DummyTile has no real Biomes
        {
            return null;
        }
        AdvancedTilePlaced tilePlaced = (AdvancedTilePlaced) tile;
        int idInList = ( 5 - tilePlaced.getTilePlacement().getRotation() ) % 6;         //edge on the oposit site, with rotation
        if( idInList < 0 )
        {
            idInList += 6;
        }
        return tile.getEdge( idInList );
    }

    /**
     * Getter for the adjacent Biome south
     * @param row the row of the Tile the neigthbour is wanted from
     * @param column the column of the Tile the neighbour is wanted from
     * @return the Biome that is adjacent in the south, null if there is no Tile or a DummyTile
     */
    public Biome getNeighbourBiomeSouth( int row, int column)
    {
        AdvancedTile tile = getNeighbours( row, column )[3];
        if( tile == null || tile instanceof DummyTile )             //DummyTile has no real Biomes
        {
            return null;
        }
        AdvancedTilePlaced tilePlaced = (AdvancedTilePlaced) tile;
        int idInList = ( - tilePlaced.getTilePlacement().getRotation() ) % 6;         //edge on the oposit site, with rotation
        if( idInList < 0 )
        {
            idInList += 6;
        }
        return tile.getEdge( idInList );
    }

    /**
     * Getter for the adjacent Biome south west
     * @param row the row of the Tile the neigthbour is wanted from
     * @param column the column of the Tile the neighbour is wanted from
     * @return the Biome that is adjacent in the south west, null if there is no Tile or a DummyTile
     */
    public Biome getNeighbourBiomeSouthWest( int row, int column)
    {
        AdvancedTile tile = getNeighbours( row, column )[4];
        if( tile == null || tile instanceof DummyTile )             //DummyTile has no real Biomes
        {
            return null;
        }
        AdvancedTilePlaced tilePlaced = (AdvancedTilePlaced) tile;
        int idInList = ( 1 - tilePlaced.getTilePlacement().getRotation() ) % 6;         //edge on the oposit site, with rotation
        if( idInList < 0 )
        {
            idInList += 6;
        }
        return tile.getEdge( idInList );
    }

    /**
     * Getter for the adjacent Biome north west
     * @param row the row of the Tile the neigthbour is wanted from
     * @param column the column of the Tile the neighbour is wanted from
     * @return the Biome that is adjacent in the north west, null if there is no Tile or a DummyTile
     */
    public Biome getNeighbourBiomeNorthWest( int row, int column)
    {
        AdvancedTile tile = getNeighbours( row, column )[5];
        if( tile == null || tile instanceof DummyTile )             //DummyTile has no real Biomes
        {
            return null;
        }
        AdvancedTilePlaced tilePlaced = (AdvancedTilePlaced) tile;
        int idInList = ( 2 - tilePlaced.getTilePlacement().getRotation() ) % 6;         //edge on the oposit site, with rotation
        if( idInList < 0 )
        {
            idInList += 6;
        }
        return tile.getEdge( idInList );
    }

    /**
     * Returns the Neighbouring Biomes starting with north clockwise
     * @param row the row of the tile the neighbours are looked for
     * @param column the row of the tile the neighbours are looked for
     * @return an Array of the neighbouring Biomes, null at the places where no Tile or a DummyTile is the neighbour
     */
    public Biome[] getNeighbourBiomes( int row, int column )
    {
        Biome[] res = new Biome[ 6 ];
        res[0] = getNeighbourBiomeNorth(row, column);
        res[1] = getNeighbourBiomeNorthEast(row, column);
        res[2] = getNeighbourBiomeSouthEast(row, column);
        res[3] = getNeighbourBiomeSouth(row, column);
        res[4] = getNeighbourBiomeSouthWest(row, column);
        res[5] = getNeighbourBiomeNorthWest(row, column);
        
        return res;
    }

    /**
     * Returns an Array of all activated DummyTiles where a Tile can be placed
     * @return the Array of activated DummyTiles
     */
    public ArrayList<DummyTile> getPlacableDummyTiles()
    {
        ArrayList<DummyTile> activatedDummyTiles = new ArrayList<>();
        for (AdvancedTile advancedTile : currentTilesOnBoard) {
            if (advancedTile instanceof DummyTile) {
                DummyTile currentTile = (DummyTile) advancedTile;
                if (currentTile.isActivated()) {
                    activatedDummyTiles.add(currentTile);                     //add if Tile is DummyTile and activated
                }
            }
        }
        return activatedDummyTiles;
    }

/**
     * Toggles the DummyTiles from board to match if newTile can be placed there or not
     * @param newTile the Tile on the stack that should be placed
     * @param playerID the player ID of the player whose turn it is
     */
    public void togglePlacableDummyTiles( AdvancedTile newTile, int playerID)
    {
        if(!( newTile == null || currentTilesOnBoard == null || currentTilesOnBoard.size() == 0 )) {
            for (AdvancedTile i : currentTilesOnBoard ) {
                if (i instanceof DummyTile) {
                    DummyTile currentDummyTile = (DummyTile) i;
                    toggleTileIfNecessary(newTile, currentDummyTile, playerID);         //checks for every Tile if it is a DummyTile and toggles it if necessary
                }
            }
        }
    }

    /**
     * Helper-Method that toggles a given DummyTile to match if newTile can be placed there or not
     * @param newTile the tile on the Stack to check the positions with
     * @param dummyTile the tile to check and potentially toggle
     * @param playerID the ID of the player whose turn it is
     */
    private void toggleTileIfNecessary( AdvancedTile newTile, DummyTile dummyTile, int playerID )
    {
        for( int j = 0; j < 6; j++ )
        {
            TilePlacement currentPlacement = new TilePlacement( dummyTile.getTilePlacement().getRow(), dummyTile.getTilePlacement().getColumn(), j );   //makes Placements for every Rotation and checks wit them
            if( placementIsViable( newTile, currentPlacement, playerID+1 ) )
            {
                if( !dummyTile.isActivated() )
                {
                    dummyTile.toggle();         //toggles if viable Placement found and Tile wasn't activated before
                }
                return;
            }
        }
        if( dummyTile.isActivated() )
        {
            dummyTile.toggle();             //found no viable Rotation and Tile was activated, so gets deactivated
        }
    }

    /**
     * Iterates through every TilePlaced on the Board and sets all CountedEdges to false
     */
    private void resetCountedEdges()
    {
        for (AdvancedTile advancedTile : currentTilesOnBoard) {
            if (advancedTile instanceof AdvancedTilePlaced) {
                AdvancedTilePlaced currentTilePlaced = (AdvancedTilePlaced) advancedTile;
                currentTilePlaced.resetCountedEdge();               //resets every Counted Edge
            }
        }
    }

    /**
     * counts the ponts each player gets on the map right now
     * @return a Hashmap that maps every PlayerID on the map with the points of the player
     */
    public HashMap<Integer, Integer> countPoints()
    {
        resetCountedEdges();            //reset so every Edge has still to be counted
        HashMap<Integer, Integer > playerIDsWithPoints = new HashMap<>();
        //iterate through every Tile
        for (AdvancedTile advancedTile : currentTilesOnBoard) {
            if (advancedTile instanceof AdvancedTilePlaced) {
                AdvancedTilePlaced currentTilePlaced = (AdvancedTilePlaced) advancedTile;
                for (int direction = 0; direction < 6; direction++)           //iterate through edges
                {
                    if (!currentTilePlaced.getCountedEdge()[direction])        //already counted
                    {
                        BooleanHolder neighboursDummyTiles = new BooleanHolder(false);            //indicates if a DummyTile is borderd by the area
                        int idInList = (direction - currentTilePlaced.getTilePlacement().getRotation()) % 6;      //id of the edge in the list
                        if (idInList < 0) {
                            idInList += 6;
                        }
                        ArrayList<Integer> playerIDsInBiomeArea = new ArrayList<>();
                        int biomeBorders = countBiomeAreaInterTileBorders(currentTilePlaced, -1, direction, currentTilePlaced.getEdge(idInList), neighboursDummyTiles, playerIDsInBiomeArea);   //amount of the interTileBorders of the area
                        if (neighboursDummyTiles.getValue())       //borders dummyTiles, no points
                        {
                            biomeBorders = 0;
                        }
                        int points = (playerIDsInBiomeArea.size() * biomeBorders) + (int) Math.floor(Math.pow(biomeBorders, 1.5));        //calculates points
                        for (Integer integer : playerIDsInBiomeArea) {
                            if (integer != 0) {
                                if (playerIDsWithPoints.containsKey(integer)) {
                                    playerIDsWithPoints.replace(integer, playerIDsWithPoints.get(integer) + points);
                                } else {
                                    playerIDsWithPoints.put(integer, points);
                                }
                            }
                        }       //adds the points from the area to every playerID in it
                    }
                }
            }               //else DummyTile, no points
        }
        return playerIDsWithPoints;
    }

    /**
     * Recursive Method that goes through the current Biome-Area and counts the Inter tile borders in it
     * checks if the area neighbours a Dummy Tile 
     * @param tp the TilePlaced that contains the area
     * @param from the direction where the area is coming from, -1 in first call 
     * @param direction the direction to check in, only not -1 in first call
     * @param biome the biome of the area
     * @param neighboursDummyTiles a BooleanHolder that contains a boolean if the Area bordes a DummyTile
     * @param playersIncluded an ArrayList of the Players in the area
     * @return the amount of inter tile borders in the area
     */
    private int countBiomeAreaInterTileBorders( AdvancedTilePlaced tp, int from, int direction, Biome biome, BooleanHolder neighboursDummyTiles, ArrayList<Integer> playersIncluded )
    {
        int res = 0;
        if( !playersIncluded.contains( tp.getPlayerID() ))
        {
            playersIncluded.add( tp.getPlayerID() );            //add player that can get points for the area
        }
        if( from == -1 )                                        //first call, direction is the edge to start with
        {
            if( tp.getCountedEdge()[direction] )
            {
                return 0;                                       //edge already counted
            }
            int directionIDInList = ( direction - tp.getTilePlacement().getRotation() ) % 6;
            if( directionIDInList < 0 )
            {
                directionIDInList += 6;
            }
            tp.countEdge(direction);
            if( getNeighbourBiomes(tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[direction] == tp.getEdge( directionIDInList ) ) 
            {                   //found inter Tile Border
                AdvancedTilePlaced neighbourInDirection = (AdvancedTilePlaced) getNeighbours( tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[direction];
                if( !neighbourInDirection.getCountedEdge()[ ( direction + 3 ) % 6] )        //border of neighbourtile not counted yet
                {
                    neighbourInDirection.countEdge( ( direction + 3 ) % 6 );                //count it now
                    res +=1;
                    res += countBiomeAreaInterTileBorders( neighbourInDirection, (direction + 3) %6, -1, biome, neighboursDummyTiles, playersIncluded );            //continue with that edge of the neighbour
                }
            }
            else if( getNeighbourBiomes(tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[direction] == null )                                    //area borders DummyTile so should'nt  
            {
                neighboursDummyTiles.setValue( true );
            }       //otherwise Biomes don't match but theres an AdvancedTilePlaced
            res += countBiomeAreaInterTileBorders(tp, direction, -1, biome, neighboursDummyTiles, playersIncluded );            //look for other parts of the area in the tile
        }
        else    //from is set and direction is -1
        {
            if( tp.getCenter() == biome )               //center is part of the area so every edge that is the given biome is part of the area
            {
                for( int i = 0; i < 6; i++ )
                {
                    if( !tp.getCountedEdge()[i])
                    {
                        int idInListI = i - tp.getTilePlacement().getRotation();
                        if( idInListI < 0 )
                        {
                            idInListI += 6;
                        }
                        if( tp.getEdge( idInListI ) == biome )              //find those parts
                        {
                            tp.countEdge( i );                              //count edge
                            if( getNeighbourBiomes(tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[i] == biome )        //neighbourEdge has same biome
                            {
                                AdvancedTilePlaced neighbourInDirectionI = (AdvancedTilePlaced) getNeighbours( tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[i];
                                if( !neighbourInDirectionI.getCountedEdge()[( i + 3 ) % 6] )        //neighbouring edge not counted yet
                                {
                                    neighbourInDirectionI.countEdge( ( i + 3 ) % 6 );
                                    res +=1;
                                    res += countBiomeAreaInterTileBorders( neighbourInDirectionI, ( i + 3 ) %6 , -1, biome, neighboursDummyTiles, playersIncluded ); //again with the neighbouring edge as new from
                                }
                            }
                            else if( getNeighbourBiomes(tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[i] == null )        //Neighbour is a DummyTile
                            {
                                neighboursDummyTiles.setValue( true );
                            }
                            //else the neighbour is a TilePlaced but has a different biome there
                        }
                    }
                }
            }
            else
            {
                int idEdgeFromPlus1 = ( from + 1 - tp.getTilePlacement().getRotation() ) % 6;
                int idEdgeFromMinus1 = ( from - 1 - tp.getTilePlacement().getRotation() ) % 6;
                if( idEdgeFromPlus1 < 0 )
                {
                    idEdgeFromPlus1 += 6;
                }
                if( idEdgeFromMinus1 < 0 )
                {
                    idEdgeFromMinus1 += 6;
                }
                if( tp.getEdge( idEdgeFromPlus1 ) == biome && !tp.getCountedEdge()[( from + 1) %6] )       //neighbouring Edge one up, same Tile has the same biome
                {
                    tp.countEdge( ( from + 1 ) % 6 );
                    if( getNeighbourBiomes( tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[( from + 1) %6] == biome )  //neighbour to that tile has the same biome
                    {
                        AdvancedTilePlaced neighbourTilePlaced = (AdvancedTilePlaced) getNeighbours( tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[(from + 1)%6];
                        if( !neighbourTilePlaced.getCountedEdge()[( from + 4 ) % 6] )           //edge of the neighbour isn't counted
                        {
                            neighbourTilePlaced.countEdge( ( from + 4 ) % 6 );
                            res += 1;
                            res += countBiomeAreaInterTileBorders( neighbourTilePlaced, ( from + 4 ) % 6, -1, biome, neighboursDummyTiles, playersIncluded );
                        }
                    }
                    else if( getNeighbourBiomes( tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[( from + 1) %6] == null )      //neighbouring edge with same biome borders dummyTile
                    {
                        neighboursDummyTiles.setValue( true );
                    }
                    res += countBiomeAreaInterTileBorders( tp, ( from + 1 ) % 6, -1, biome, neighboursDummyTiles, playersIncluded );        //check one more up
                }
                int fromMinus1 = ( from - 1 ) % 6;
                if( fromMinus1 < 0 )
                {
                    fromMinus1 += 6;
                }
                if( tp.getEdge( idEdgeFromMinus1 ) == biome && !tp.getCountedEdge()[fromMinus1] )        //neighbouring Edge-Biome one down is the same
                {
                    tp.countEdge( fromMinus1 );
                    if( getNeighbourBiomes( tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[fromMinus1] == biome )      //neighbouring biome to the edge -1 is the same biome
                    {
                        AdvancedTilePlaced neighbourTilePlaced = (AdvancedTilePlaced) getNeighbours( tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[fromMinus1];
                        if( !neighbourTilePlaced.getCountedEdge()[( fromMinus1 + 3 ) % 6])      //edge of neighbour not counted yet
                        {
                            neighbourTilePlaced.countEdge( ( fromMinus1 + 3 ) % 6 );
                            res += 1;
                            res += countBiomeAreaInterTileBorders( neighbourTilePlaced, ( fromMinus1 + 3 ) % 6, -1, biome, neighboursDummyTiles, playersIncluded );     //go into that tile
                        }
                    }
                    else if( getNeighbourBiomes( tp.getTilePlacement().getRow(), tp.getTilePlacement().getColumn() )[fromMinus1] == null )      //neighbour is a DummyTile
                    {
                        neighboursDummyTiles.setValue( true );
                    }
                    res += countBiomeAreaInterTileBorders( tp, fromMinus1, -1, biome, neighboursDummyTiles, playersIncluded );          //check one more down
                }
            }
        }
        return res;
    }
}

/**
 * A class that holds a Boolean
 */
class BooleanHolder
{
    /**
     * The boolean the holder holds
     */
    Boolean value;

    /**
     * Constructor that initialize the Holder with a boolean
     * @param val the boolean to initialize the Holder with
     */
    public BooleanHolder( boolean val )
    {
        value = val;
    }

    /**
     * Setter for the value that is held
     * @param val the boolean value to set it to
     */
    public void setValue( boolean val)
    {
        value = val;
    }

    /**
     * Getter for the value that is held
     * @return the boolean value that is held
     */
    public boolean getValue()
    {
        return value;
    }
}