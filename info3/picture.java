import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner;

class region{
    public char value;
    public int numberof;
    public int bezeichner;
    public region parent;
}

public class picture{

    static String[] readandwritefile(){
        String[] strArray = new String[90];
        try {
          File myObj = new File("data");
          Scanner myReader = new Scanner(myObj); 
          for(int y = 0; y < 90; y++) {
            String data = myReader.nextLine();
            strArray[y] = data;
            for(int x = 45; x < data.length()-45; x++){
                System.out.print(data.charAt(x));
            }
            System.out.println("");
          }

          myReader.close();

        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
        return strArray;
    }

    static region compress(region a){
        while( a != a.parent ){
            a = a.parent;
        }
        return a;
    }

    static region[][] condense(region[][] regions){
        for(int x = 0; x < 90; x++){
            for(int y = 1; y < 199; y++){
                region temp = regions[x][y];
                region tempv;
                if( temp.value == regions[x][y+1].value ){
                    temp = compress(temp);
                    tempv = compress(regions[x][y+1]);
                    if( temp != tempv ){
                        temp.numberof += tempv.numberof;
                        tempv.parent = temp;
                    }
                }
                if( temp.value == regions[x][y-1].value ){
                    temp = compress(temp);
                    tempv = compress(regions[x][y-1]);
                    if( temp != tempv ){
                        temp.numberof += tempv.numberof;
                        tempv.parent = temp;
                    }
                }
                if( x < 89 ){
                    if( temp.value == regions[x+1][y].value ){
                        temp = compress(temp);
                        tempv = compress(regions[x+1][y]);
                        if( temp != tempv ){
                            temp.numberof += tempv.numberof;
                            tempv.parent = temp;
                        }
                    }
                    if( y-1 > 0 ){
                        if( temp.value == regions[x+1][y-1].value ){
                            temp = compress(temp);
                            tempv = compress(regions[x+1][y-1]);
                            if( temp != tempv ){
                                temp.numberof += tempv.numberof;
                                tempv.parent = temp;
                            }
                        }
                    }
                    if ( y+1 < 199 ){
                        if( temp.value == regions[x+1][y+1].value ){
                            temp = compress(temp);
                            tempv = compress(regions[x+1][y+1]);
                            if( temp != tempv ){
                                temp.numberof += tempv.numberof;
                                tempv.parent = temp;
                            }
                        }
                    }



                }
            }
        }
        return regions;
    }

    public static void main(String[] args){
        String[] strArray = readandwritefile();                         // Katze
                                                                          
        region[][] regions = new region[90][200];

        for(int x = 0; x < 90; x++){
            for(int y = 0; y < 200; y++){
                regions[x][y] = new region();
                regions[x][y].value = strArray[x].charAt(y);
                regions[x][y].numberof = 1;
                regions[x][y].parent = regions[x][y];
            }
        }
        regions = condense(regions);

        int max = 0;
        int smax = 0;

        for(int x = 0; x < 90; x++){
            for(int y = 0; y < 200; y++){
                int temp = compress(regions[x][y]).numberof;
                if( temp < 10 ){
                    regions[x][y].value = ' ';
                }
                if(temp > max){ 
                    max = temp;
                }
                else if(temp < max && temp > smax ){
                    smax = temp;
                }
            }
        }
        for(int x = 0; x < 90; x++){
            for(int y = 50; y < 200-50; y++){
                System.out.print(regions[x][y].value);
            }
            System.out.println("");
        }

        System.out.println("Die größte Region       :  "+max);
        System.out.println("Die zweitgrößte Region  :  "+smax);
    }
}
