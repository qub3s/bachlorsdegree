package doerfer;

import doerfer.preset.TilePlacement;
import doerfer.preset.graphics.DrawableTileComponent;
import doerfer.preset.graphics.GPanel;

import java.io.IOException;

/**
 * Class AdvancedTilePlaced
 * This class is used only for placed tiles, so that the coordinates, the placing player and the rotation
 * are known. Extends and uses the AdvancedTile, which doesn't have position or playerID
 *
 * @author Jannis Fischer
 */

public class AdvancedTilePlaced extends AdvancedTile{

    /**
     * Saves where the tile is (x,y coordinates) and its rotation (0,1,...,5)
     */
    private final TilePlacement tilePlacement;

    /**
     * The PlayerID saves the ID of the player who placed the tile
     */
    private final int playerID;

    /**
     * Saves for every Edge if it was already counted or not
     */
    private final boolean[] countedEdge;

    /**
     * Constructor of a placed Tile. To create the list of biomes the Advanced Tile at is used. From that, the biome -
     * list is extracted.
     *
     * @param aT The original advancedTile, which should be placed nor
     * @param tP The tilePlacement, where the tile sits and which rotation is has
     * @param ID the player who sets the advancedTile
     */
    public AdvancedTilePlaced(AdvancedTile aT, TilePlacement tP, int ID) {
        super(aT.getBiomes());
        playerID = ID;
        tilePlacement = new TilePlacement(tP.getRow(), tP.getColumn(), tP.getRotation()%6);
        countedEdge = new boolean[6];
        for( int i = 0; i < 6; i++ )
        {
            countedEdge[i] = false;
        }
    }

    /**
     * Returns the ID of the player who placed the tile
     * @return int, the ID of the player
     */
    public int getPlayerID () {
        return playerID;
    }

    /**
     * Returns the tilePlacement (so the x,y coordinates and the rotation) of the tile
     * @return tilePlacement
     */
    public TilePlacement getTilePlacement() {
        return tilePlacement;
    }

    /**
     * Returns an AdvancedDrawableTile with center and edges of the current tile. All six edges and the center are added to a new DrawableTile
     * Also it sets a stroke in a specific color depending on the player who placed the tile
     * @param p GPanel which is used
     * @return DrawableTile
     * @throws IOException from addChild
     */

    public AdvancedDrawableTile createVisualTile(GPanel p) throws IOException {
        AdvancedDrawableTile tile = new AdvancedDrawableTile(p,this);
        switch (playerID) {
            case 0:
                tile.setStroke("black");
                break;
            case 1:
                tile.setStroke("blue");
                break;
            case 2:
                tile.setStroke("green");
                break;
            case 3:
                tile.setStroke("red");
                break;
            case 4:
                tile.setStroke("yellow");
                break;
            case 5:
                tile.setStroke("pink");
                break;
            case 6:
                tile.setStroke("cyan");
                break;
            default:
                break;
        }

        if (!(playerID > 6)) {
            tile.setStrokeOpacity(1f);
            //tile.setStrokeWidth(3f);
        }
        tile.addChild(new DrawableTileComponent(BiomeToPresetTileComponentCenter(getCenter()), 0));

        for (int i = 0; i < 6; i++) {
            tile.addChild(new DrawableTileComponent(BiomeToPresetTileComponentEdge(edges.get(i)), i*60));
        }
        tile.transform().rotate(tilePlacement.getRotation()*60,DrawableTileComponent.WIDTH/2,DrawableTileComponent.HEIGHT/2);
        aDT = tile;
        return tile;
    }

    /**
     * Resets the booleans if the edges were already counted or not
     */
    public void resetCountedEdge()
    {
        for( int i = 0; i < 6; i++ )
        {
            countedEdge[i] = false;
        }
    }

    /**
     * Returns an array of the booleans if the Edges were counted or not
     * @return the array of booleans if the Edges were counted or not
     */
    public boolean[] getCountedEdge()
    {
        return countedEdge;
    }

    /**
     * Saves, if a biome in a specific direction has already been counted
     * @param direction The direction (0..5) in which the biome lays, which was counted.
     */
    public void countEdge( int direction )
    {
        countedEdge[direction] = true;
    }
}
