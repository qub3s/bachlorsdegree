package doerfer;

import doerfer.preset.Biome;
import doerfer.preset.TilePlacement;
import doerfer.preset.graphics.DrawableTileComponent;
import doerfer.preset.graphics.GPanel;
import doerfer.preset.graphics.PresetTileComponent;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class DummyTile
 * Implements a dummy-tile. It is used around already placed tile to indicate, if one could place
 * a tile. Also, one should be able to click on them to place a tile.
 * Is able to return a DrawableTile, which is just grey.
 * Rotation in the tilePlacement of the DummyTile is always 0, as the system is setting this kind of tiles
 * and its symmetric
 *
 * @author Jannis Fischer
 */
public class DummyTile extends AdvancedTile {

    /**
     * TilePlacement to save the position of the DummyTile, so be able to show it easily.
     * As there isn't a player setting this kind of tile, the playerID is -1
     */
    private final TilePlacement tilePlacement;

    /**
     * Boolean to save if the DummyTile can be clicked or not and shown or not
     */
    private boolean activated;

    /**
     * Constructor
     * Just creates a Dummy with the given position and the irrelevant default-element Water
     * (Because water is the best element ~ Katara) (not true but ok (Zuko ftw))
     * @param x x coordinate
     * @param y y coordinate
     */
    public DummyTile (int x, int y) {
        super(new ArrayList<>(List.of(Biome.WATER,Biome.WATER,Biome.WATER,Biome.WATER,Biome.WATER,Biome.WATER)));
        tilePlacement = new TilePlacement(x,y,0);
        activated = true;
        //random Biome, because a DummyTiles Biomes are irrelevant.
    }

    /**
     * Returns a DrawableTile, which is a hexagon shaped grey tile
     * @param p GPanel which is used
     * @return AdvancedDrawableTile - a grey hexagon
     * @throws IOException from addChild
     */
    public AdvancedDrawableTile createVisualTile(GPanel p) throws IOException {
        AdvancedDrawableTile tile = new AdvancedDrawableTile(p,this);
        tile.addChild(new DrawableTileComponent(PresetTileComponent.GREY,0));
        tile.setStroke(Color.GRAY);
        tile.setStrokeWidth(3);
        tile.setOpacity(0.6f);
        if(!activated) {
            tile.setOpacity(0);
        }
        aDT = tile;
        return tile;
    }

    /**
     * Returns the visual Tile of the DummyTile. If there hasn't been one created yet, this will be done now.
     * @param p panel, on which the drawableTile is shown
     * @return the AdvancedDrawableTile
     * @throws IOException from addChild
     */
    public AdvancedDrawableTile getVisualTile(GPanel p) throws IOException {
        if (aDT == null) {
            return createVisualTile(p);
        }
        else {
            return aDT;
        }
    }

    /**
     * Returns the tilePlacement
    * @return tilePlacement
    */
    public TilePlacement getTilePlacement() {
        return tilePlacement;
    }

    /**
     * Toggles the activation of the DummyTile
     */
    public void toggle() {
        if (activated) {
            aDT.setOpacity(0);
        }
        else {
            aDT.setOpacity(0.6f);
        }
        activated = !activated;
    }

    /**
     * Returns the activation-status of the DummyTile
     * If it is activated, the Tile can be placed there
     * @return true, if activated, else false
     */
    public boolean isActivated()
    {
        return activated;
    }
}
