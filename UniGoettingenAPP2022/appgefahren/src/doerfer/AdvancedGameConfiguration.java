package doerfer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import doerfer.preset.Biome;
import doerfer.preset.GameConfiguration;
import doerfer.preset.Tile;
import doerfer.preset.TilePlacement;
import doerfer.preset.Settings;

/**
 * Class AdvancedGameConfiguration, implements GameConfiguration
 * A class that reads the configuration File and creates the data to use in the game
 *
 * @author Philipp Schäfer
 */
public class AdvancedGameConfiguration implements GameConfiguration
{
    /**The magic number that should be the first line of the configuration File*/
    private final String MAGICNUMBER = "DoerferGameConfigurationv1";
    /**The description given in the configuration File*/
    private String description;
    /**The number of Players given in the configuration File*/
    private int playerCount;
    /**The number of Tiles on the stack initially given in the configuration File*/
    private int tileCount;
    /**A Hashmap that links the Biomes to their chances given in the configuration File*/
    private final HashMap<Biome, Integer> biomeChances;
    /**A Hashmap that links the Biomes to their weights given in the configuration File*/
    private final HashMap<Biome, Integer> biomeWeights;
    /**The number of preplaced Tiles given in the configuration File*/
    private int prePlacedTilesCount;
    /**A list of preplaced Tiles given in the configuration File*/
    private final List<Tile> preplacedTiles;
    /**A list of preplaced Tileplacements given in the configuration File*/
    private final List<TilePlacement> preplaceTileplacements;
    /**A list of the Player IDs for every Preplaced Tile in the same order given in the configuration File*/
    private final List<Integer> preplacedTilesPlayerIDs;
    /**A list of the PlayerIDs*/
    private final List<Integer> playerIDs;
    /** settings variable inside of the gameview */
    public final Settings settings;
    /**
     * Constructor that creates the necessary Data of a Configuration-File
     * @param sets the Settings
     */
    public AdvancedGameConfiguration(Settings sets)
    {
        settings = sets;
        if( settings.delay < 150 )
        {
            System.out.println( "The delay is lower than the alowed amount of 150. Changing it to 150." );
            settings.delay = 150;
        }
        Scanner in = null;
        try
        {
            File file = sets.gameConfigurationFile;
            in = new Scanner( file );
        }
        catch( FileNotFoundException ex )               //File dosen't exist
        {
            System.out.println( "The Location of the configurationfile is not correct please use a valid path to a configuration file." );
            System.exit(0);
        }

        biomeChances = new HashMap<>();
        biomeWeights = new HashMap<>();
        preplacedTiles = new ArrayList<>();
        preplaceTileplacements = new ArrayList<>();
        preplacedTilesPlayerIDs = new ArrayList<>();
        playerIDs = new ArrayList<>();

        for( int i = 0; i < 11; i++ )
            {
            if( !in.hasNextLine() )     //the config is to short
            {
                in.close();
                throw new IllegalArgumentException( "The config is shorter than the at least required 11 lines." );
            }
            else
            {
                String line = in.nextLine();
                switch(i)
                {
                    case 0:                                     //should have the magic number
                        if( ! MAGICNUMBER.equals( line ) )
                        {
                            in.close();
                            throw new IllegalArgumentException( "The config that was handed contains the wrong magic number in line 1." );
                        }
                        break;
                    case 1:                                     //should have the description
                        description = line;
                        break;
                    case 2:                                     //sould have the Player-Count
                        if( isNumeric( line ) )
                        {
                            playerCount = Integer.parseInt( line );
                        }
                        else
                        {
                            in.close();
                            throw new IllegalArgumentException( "The player-amount in the config is no Integer." );
                        }
                        if( playerCount > 4 )
                        {
                            in.close();
                            throw new IllegalArgumentException( "The player-amount in the config is bigger than the maximum of 4." );
                        }
                        if( playerCount != settings.playerNames.size() && settings.playerNames.size() != 4 )
                        {
                            in.close();
                            throw new IllegalArgumentException( "The player-amount and amount of names don't match" );
                        }
                        if( playerCount != settings.playerTypes.size() && settings.playerTypes.size() != 4 )
                        {
                            in.close();
                            throw new IllegalArgumentException( "The player-amount and amount of Player Types don't match" );
                        }
                        if( playerCount != settings.playerColors.size() && settings.playerColors.size() != 4)
                        {
                            in.close();
                            throw new IllegalArgumentException( "The player-amount and Player Colors don't match" );
                        }
                        break;
                    case 3:                                     //should have the number of Tiles on the Stack
                        if( isNumeric( line ) )
                        {
                            int temp = Integer.parseInt( line );
                            if( temp <= playerCount + 1)
                            {
                                in.close();
                                throw new IllegalArgumentException( "Not enough Tiles on the stack given in the config compared to the amount of players." );
                            }
                            else
                            {
                                tileCount = temp;
                            }
                        }
                        else
                        {
                            in.close();
                            throw new IllegalArgumentException( "The amount of Tiles on the stack given in the config is no Integer." );
                        }
                        break;
                    case 4: case 5: case 6: case 7: case 8: case 9:                                 //should have the 6 Biomes and their Chances and Weights
                        String[] words = line.split( " " );                                 //splits the string at the spaces
                        if( words.length < 3 )
                        {
                            in.close();
                            throw new IllegalArgumentException( "The Biomes aren't given all required parameters in the config." );
                        }
                        Biome currentBiome = Biome.valueOf( words[0] );
                        if( isNumeric( words[1] ) && isNumeric( words[2] ) )
                        {
                            if( biomeChances.containsKey( currentBiome ) || biomeWeights.containsKey( currentBiome ) )
                            {
                                in.close();
                                throw new IllegalArgumentException( "A Biome is in the list multiple times in the config." );
                            }
                            else
                            {
                                biomeChances.put( currentBiome, Integer.parseInt( words[1] ) );     //add the chance with the biome to the Hashmap
                                biomeWeights.put( currentBiome, Integer.parseInt( words[2] ) );     //add the weight with the biome to the Hashmap
                            }
                        }
                        else
                        {
                            in.close();
                            throw new IllegalArgumentException( "The chance or the weight of the biomes given in the config is not an Integer." );
                        }
                        break;
                    case 10:
                        if( isNumeric(line) )                               //should have the number of preplaced Tiles
                        {
                            prePlacedTilesCount = Integer.parseInt( line );
                        }
                        else
                        {
                            in.close();
                            throw new IllegalArgumentException( "The amount of starting-Tiles given in the config is not an Integer." );
                        }
                        break;
                }
            }
        }
        for( int i = 0; i < prePlacedTilesCount; i++ )
        {
                                                    //rest is the preplaced Tiles with playerID, row, collumn, rotation and the 6 biomes on the edges
            if( !in.hasNextLine() )
            {
                in.close();
                throw new IllegalArgumentException( "In the config the the amount of preplaced Tiles dosen't fit the amount of given preplaced Tiles." );
            }
            else
            {
                String line = in.nextLine();
                String[] words = line.split( " " );                 //splits the string at the spaces
                if (words.length < 5)
                {
                    in.close();
                    throw new IllegalArgumentException( "In the config not everything that is needed is given for the preplaced tiles." );
                }
                int playerID;
                int row;
                int column;
                int rot;
                if( isNumeric( words[0] ) && isNumeric( words[1] ) && isNumeric( words[2] ) && isNumeric( words[3] ) )
                {
                    playerID = Integer.parseInt( words[0] );
                    row = Integer.parseInt( words[1] );
                    column = Integer.parseInt( words[2] );
                    rot = Integer.parseInt( words[3] );
                }
                else
                {
                    in.close();
                    throw new IllegalArgumentException( "In the config the coordinates, id or rotation for the preplaced Tiles is not an Integer." );
                }
                preplaceTileplacements.add( new TilePlacement( row, column, rot ) );
                preplacedTilesPlayerIDs.add( playerID );
                if( !playerIDs.contains( playerID ) && playerID != 0 )
                {
                    playerIDs.add( playerID );
                }
                String[] initBiome = words[4].split( "," );             //splits the string at the "," to get the Biomes of the Tile
                List<Biome> biomesInTile = new ArrayList<>();
                if( initBiome.length < 6 )
                {
                    in.close();
                    throw new IllegalArgumentException( "There aren't enough Biomes given in the config for a PreplacedTile." );
                }
                else
                {
                    for( int j = 0; j < 6; j++ )
                    {
                        Biome[] biomes = Biome.values();
                        boolean found = false;
                        for (Biome biome : biomes) {
                            if (Biome.valueOf(initBiome[j]) == biome) {
                                found = true;       //checks if the Biome is a valid Biome
                            }
                        }
                        if( found )
                        {
                            Biome currentBiome = Biome.valueOf( initBiome[j] );
                            biomesInTile.add( currentBiome );       //adds Biome to the List for the tile
                        }
                        else
                        {
                            in.close();
                            throw new IllegalArgumentException( "The config contains unknown Biomes." );
                        }

                    }
                    preplacedTiles.add( new AdvancedTile( biomesInTile) );
                }
            }
        }
        if( playerIDs.size() != playerCount )
        {
            throw new IllegalArgumentException( "The amount of players and the different IDs in the preplaced Tiles don't match in the config." );
        }
        int[] numberOfTilesPerPlayer = new int[playerIDs.size() ];
        for (Integer preplacedTilesPlayerID : preplacedTilesPlayerIDs) {
            if (preplacedTilesPlayerID != 0) {
                for (int j = 0; j < playerIDs.size(); j++) {
                    if (preplacedTilesPlayerID.equals(playerIDs.get(j))) {
                        numberOfTilesPerPlayer[j]++;                            //checks if every Player has the same amount of Preplaced Tiles
                    }
                }
            }
        }
        int numberofTilesPlayerOne = numberOfTilesPerPlayer[0];
        boolean unfair = false;
        for( int i = 1; i < numberOfTilesPerPlayer.length; i++ )
        {
            if( numberOfTilesPerPlayer[i] != numberofTilesPlayerOne )
            {
                unfair = true;
                break;
            }
        }
        if( unfair )
        {
            System.out.println( "The game might be a bit unfair because not every player has the same amount of DummyTiles in the Config." );
        }
        if( tileCount % playerCount != 0 )
        {
            System.out.println( "The game might be a bit unfair because the number of Tiles on the Stack isn't dividable by the amount of players." );
        }
        for( int i = 1; i <= playerCount; i++ )
        {
            if( !preplacedTilesPlayerIDs.contains( i ))
            {
                in.close();
                throw new IllegalArgumentException( "Please put the PlayerIDs from 1 to the amount of players you have in the Config." );
            }
        }

        for( int i = 0; i < preplaceTileplacements.size(); i++ )
        {
            for( int j = i + 1; j < preplaceTileplacements.size(); j ++ )
            {
                if( preplaceTileplacements.get(i).getColumn() == preplaceTileplacements.get( j ).getColumn() && preplaceTileplacements.get(i).getRow() == preplaceTileplacements.get( j ).getRow() )
                {
                    in.close();
                    throw new IllegalArgumentException( "Some of the Preplaced Tiles are in the same position in the Config" );
                }
            }
        }


        in.close();
    }

    /**
     * Helper-Method to find out if a String can be turned to an Integer or not
     * @param s the String to test
     * @return a boolean if the String can be turned to an Integer
     */
    private boolean isNumeric( String s )
    {
        if( s == null )
        {
            return false;
        }
        try
        {
            Integer.parseInt( s );
        }
        catch( NumberFormatException ex )           //cant be parsed to an int
        {
            return false;
        }
        return true;
    }

    /**
     * A getter for a Map of the Biomes with their Biome-Chances
     * @return the Map of the Biomes with their Biome-Chances
     */
    @Override
    public Map<Biome, Integer> getBiomeChances()
    {
        return biomeChances;
    }

    /**
     * A getter for a Map of the Biomes with their Biome-Weights
     * @return the Map of the Biomes with their Biome-Weights
     */
    @Override
    public Map<Biome, Integer> getBiomeWeights()
    {
        return biomeWeights;
    }

    /**
     * A getter for the Description of the Configuration
     * @return a String with the description of the Configuration
     */
    @Override
    public String getDescription()
    {
        return description;
    }

    /**
     * A getter for the number of players
     * @return an Integer with the number of players from the Configuration
     */
    @Override
    public int getNumPlayers()
    {
        return playerCount;
    }

    /**
     * A getter for the number of Tiles on the Stack initially
     * @return an Integer with the number of the Tiles on the Stack initially from the Configuration
     */
    @Override
    public int getNumTiles()
    {
        return tileCount;
    }

    /**
     * A getter for the List of preplaced Tiles
     * @return a List with the preplaced Tiles from the Configuration
     */
    @Override
    public List<Tile> getPreplacedTiles()
    {
        return preplacedTiles;
    }

    /**
     * A getter for the List of preplayed TilePlacements
     * @return a List with preplaced Tileplacements from the Configuration
     */
    @Override
    public List<TilePlacement> getPreplacedTilesPlacements()
    {
        return preplaceTileplacements;
    }

    /**
     * A getter for the List of Player-IDs
     * @return a List of Player-IDs  from the Configuration
     */
    @Override
    public List<Integer> getPreplacedTilesPlayerIDs()
    {
        return preplacedTilesPlayerIDs;
    }
}
