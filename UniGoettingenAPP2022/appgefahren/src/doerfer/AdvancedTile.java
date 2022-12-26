package doerfer;
import doerfer.preset.Biome;
import doerfer.preset.Tile;
import doerfer.preset.graphics.DrawableTileComponent;
import doerfer.preset.graphics.GPanel;
import doerfer.preset.graphics.PresetTileComponent;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Class AdvancedTile, extends Tile.
 * Saves the player who sets the Tile, the types of biomes at the edges given in a LinkedList[Biome].
 * Is able to determine which biome is the center from the given biomes
 *
 * @author Jannis Fischer
 */
public class AdvancedTile extends Tile {

    /**
     * Save of the advancedDrawableTile
     */
    AdvancedDrawableTile aDT;

    /**
     * Constructor. Gets a list of the biomes and calls the the super-constructor. Throws an error, if the given list is
     * null or contains not exact six elements.
     * @param ls List of the biomes
     */
    public AdvancedTile(List<Biome> ls) {
        super(ls);
        if (ls == null) {
            System.out.println("TileCreationError: Someone tried to create a tile with null biomes");
            System.exit(0);
        }
        else if (ls.size() != 6) {
            System.out.println("TileCreationError: Someone tried to create a tile with "+ls.size()+ " != 6 biomes");
            System.exit(0);
        }
    }

    /**
     * Returns the Biome, which is in the center depending on the given biomes on the edges
     * @return Biome (Biome.HOUSES or Biome.FIELDS or etc...)
     */
    @Override
    public Biome getCenter() {
        //First condition
        if (edges.contains(Biome.TRAINTRACKS)) {
            return Biome.TRAINTRACKS;
        }
        //Check, which biome is the most common
        int count, maxCount = 0;
        boolean unique = true;
        LinkedList<Biome> maxBiomes = new LinkedList<>();
        //list of the most common objects. If unique, then maxBiomes.len == 1
        for (Biome b : Biome.values()) { //iterates over all biomes
            count = 0;
            for (int i = 0; i < 6; i++) {
                if (edges.get(i).equals(b)) {
                    count++;             //and counts
                }
            }
            if (count == maxCount) {    //is max number already reached, it is not unique
                unique = false;
                maxBiomes.add(b);
            } else if(count > maxCount) {//if current biome is more common than previous one
                maxCount = count;
                unique = true;
                maxBiomes = new LinkedList<>();
                maxBiomes.add(b);
            }
        }
        if (unique) {
            return maxBiomes.get(0);    //first and only biome is returned, second condition
        }
        else {
            //third to sixth condition
            if (maxBiomes.contains(Biome.WATER)) {
                return Biome.WATER;
            }
            else if (maxBiomes.contains(Biome.PLAINS)) {
                return Biome.PLAINS;
            }
            else if (maxBiomes.contains(Biome.FOREST)) {
                return Biome.FOREST;
            }
            else if (maxBiomes.contains(Biome.HOUSES)) {
                return Biome.HOUSES;
            }
            //last condition
            else{
                return Biome.FIELDS;
            }
        }
    }

    /**
     * Returns a AdvancedDrawableTile with center and edges of the current tile. All six edges and the center are added to a new DrawableTile
     * @param p GPanel which is used
     * @return DrawableTile
     * @throws IOException from addChild
     */
    public AdvancedDrawableTile createVisualTile(GPanel p) throws IOException{
        AdvancedDrawableTile tile = new AdvancedDrawableTile(p,this);
        tile.addChild(new DrawableTileComponent(BiomeToPresetTileComponentCenter(getCenter()), 0));
        for (int i = 0; i < 6; i++) {
            tile.addChild(new DrawableTileComponent(BiomeToPresetTileComponentEdge(edges.get(i)), i*60));
        }
        aDT = tile;
        return tile;
    }

    /**
     * Returns for a given biome the corresponding PresentTileComponent on the edge
     * @param b Biome for which the PresentTileComponent should be returned
     * @return PresentTileComponent of an edge of a biome
     */
    public PresetTileComponent BiomeToPresetTileComponentEdge (Biome b) {
        if (b.equals(Biome.HOUSES)) {
            return PresetTileComponent.HOUSES_EDGE;
        } else if (b.equals(Biome.FIELDS)) {
            return PresetTileComponent.FIELDS_EDGE;
        } else if (b.equals(Biome.TRAINTRACKS)) {
            return PresetTileComponent.TRAINTRACKS_EDGE;
        } else if (b.equals(Biome.FOREST)) {
            return PresetTileComponent.FOREST_EDGE;
        } else if (b.equals(Biome.PLAINS)) {
            return PresetTileComponent.PLAINS_EDGE;
        } else {
            return PresetTileComponent.WATER_EDGE;
        }
    }

    /**
     * Returns for a given biome the corresponding PresentTileComponent at the center
     * @param b Biome for which the PresentTileComponent should be returned
     * @return PresentTileComponent at the center of a biome
     */
    public PresetTileComponent BiomeToPresetTileComponentCenter (Biome b) {
        if (b.equals(Biome.HOUSES)) {
            return PresetTileComponent.HOUSES_CENTER;
        } else if (b.equals(Biome.FIELDS)) {
            return PresetTileComponent.FIELDS_CENTER;
        } else if (b.equals(Biome.TRAINTRACKS)) {
            return PresetTileComponent.TRAINTRACKS_CENTER;
        } else if (b.equals(Biome.FOREST)) {
            return PresetTileComponent.FOREST_CENTER;
        } else if (b.equals(Biome.PLAINS)) {
            return PresetTileComponent.PLAINS_CENTER;
        } else {
            return PresetTileComponent.WATER_CENTER;
        }
    }

    /**
     * Returns a List with the different biomes at the edges
     * @return List[Biome] with the biomes at the edges
     */
    public List<Biome> getBiomes () {
        return edges;
    }

    /**
     * Returns the visual Tile of the AdvancedTile. If this doesn't exists, a new one is created with createVisualTile()
     * @param p the panel, where something should be shown
     * @return AdvancedDrawableTile of the AdvancedTile
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
}