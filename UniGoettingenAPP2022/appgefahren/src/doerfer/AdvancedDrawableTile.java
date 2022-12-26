package doerfer;

import doerfer.preset.graphics.DrawableTile;
import doerfer.preset.graphics.GPanel;

/**
 * Class AdvancedDrawableTile
 *
 * Extends DrawableTile. Is used to get the AdvancedTile, the DrawableTile was made of
 * @author Jannis Fischer
 */
public class AdvancedDrawableTile extends DrawableTile {
    /**
     * The AdvancedTile, the DrawableTile was made of
     */
    AdvancedTile tile;

    /**
     * Constructor
     * Calls the super constructor and remembers the tile, the drawable tile was made of
     * @param p GPanel, the shown panel
     * @param t AdvancedTile, the DrawableTile was made of
     */
    public AdvancedDrawableTile(GPanel p, AdvancedTile t) {
        super(p);
        tile = t;
    }

    /**
     * Returns the AdvancedTile, the DrawableTile was made of
     * @return AdvancedTile
     */
    public AdvancedTile getAdvancedTile() {
        return tile;
    }

    /**
     * Checks, if the AdvancedDrawableTile already has an MouseListener
     * @return true, if MouseListener is empty, else false
     */
    public boolean isMouseListenerEmpty() {
        return (mouseListener == null);
    }
}
