package doerfer;
import java.util.LinkedList;
import com.kitfox.svg.SVGElementException;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import doerfer.preset.ArgumentParser;
import doerfer.preset.OptionalFeature;
import doerfer.preset.Settings;
import java.util.Scanner;
/**
 * MainClass (testClass)
 * Should create a first view on the project and/or on the first tile
 *
 * @author Jannis Fischer
 * @author Georg Eckardt
 */
public class MainFrame {
    /** argument parser given in the preset 
    @param args the commandline arguments
    @return The settings parsed be the parser*/
    static Settings parse(String[] args) {
        List<String> authors = new LinkedList<>(){{add("Jannis"); add("Philipp");add("Georg");}};
        List<OptionalFeature> features = null;

        ArgumentParser p = new ArgumentParser(args,"appgefahren","1.0",authors,null);    // Only mandatory Argument is the gameConfigurationFile with --config
        // the path is mandatory so program terminates and shows help without
        if(p.gameConfigurationFile == null){
            System.out.println("Please use at least the --config Option");
            p.showHelp();
        }
        
        Scanner in;
        
        try
        {
            File file = p.gameConfigurationFile;
            in = new Scanner( file );
        }
        catch( FileNotFoundException ex )               //evtl später thrown
        {
            System.out.println( "The Location of the configurationfile is not correct please use a valid path to a configuration file." );
            p.showHelp();
        }
        // Die überprüfung, ob es sich um einen validen file handelt hier vorgenommen werden ?
        return p;
    }
/**Kommentar
*   @param args Die Konsolen Eingabe
*   @throws SVGElementException Kommentarp.getOptions();
*   @throws IOException Kommentar
*/
    public static void main(String[] args) throws Exception {
        System.out.println("start");
        
        Settings sets = null;
        try{
            sets = parse(args);
            System.out.println(sets.playerNames);
        }
        catch(Exception e){
            System.out.println( "Please start the game with some parameters from above, atleast --config" );
            System.exit( 0 );
        }


        System.out.println("---------------------------------------------------");



        AdvancedGameConfiguration gameconf;
        try
        {
          gameconf = new AdvancedGameConfiguration(sets);
        }
        catch( IllegalArgumentException ex )
        {
          System.out.println( "Error: " + ex.getMessage() );
          return;
        }

        Observer o = new Observer(gameconf);
        o.startgame();
        System.out.println(o.getNames());
        // o.startturnament ???

    }
}
