 package app.exercise.testing;
import app.exercise.algebra.*;
import app.exercise.adt.*;
import app.exercise.visualtree.*;
import java.util.*;
//import app.rbdraw.RedBlackTreeDrawer.*;       // !!! wie importiert man das jar archiv !!!

/**testklasse */
public class BSTreeTester<E extends Comparable<E>>{
    /**main */
    public static void main(String args[]){

        RedBlackTreeDrawer visual = new RedBlackTreeDrawer();
        // alle zahlen gerade ??
        if(args.length % 2 == 1){
            System.out.println("Bitte gerade Anzahl an Zahlen angeben");
            return;
        }

        BSTree maintree = new BSTree();
        BSTree sek1 = new BSTree();
        BSTree sek2 = new BSTree();
        Random ran = new Random();
        int numberofints = 1000;
        int[][] arrayofrandomints = new int[numberofints][2];



        int fistzaehler = Integer.parseInt(args[0]);
        int fistnenner = Integer.parseInt(args[1]);

        int lastzaehler = Integer.parseInt(args[args.length - 2]);
        int lastnenner = Integer.parseInt(args[args.length-1]);

        for(int n1 = 0; n1 < args.length; n1 = n1+2){
            int i1 = Integer.parseInt(args[n1]);
            int i2 = Integer.parseInt(args[n1+1]);

            System.out.println(args[n1]);
            System.out.println(args[n1+1]);

            CompRational<Fractional> a = new CompRational<Fractional>(i1,i2);

            if(!maintree.add(a)){
                System.out.println("doppelt");
            }
            
            if(n1 % 4 == 0){
                sek1.add(a);
            }
            else{
                sek2.add(a);
            }

            visual.draw(maintree.root());
        }



        for(int n1 = 0; n1 < numberofints; n1++){
            int randomezaehler = ran.nextInt(100);
            int randomenenner = ran.nextInt(100);
            
            
            CompRational<Fractional> a = new CompRational<Fractional>(randomezaehler,randomenenner);
            while(/*!(maintree.min().compareTo(a) < 0) || */!(maintree.max().compareTo(a) > 0)){
                randomezaehler = ran.nextInt(100);
                randomenenner = ran.nextInt(100);
                a = new CompRational<Fractional>(randomezaehler,randomenenner);
            }
        
            arrayofrandomints[n1][0] = randomezaehler;
            arrayofrandomints[n1][1] = randomenenner;
        }

        Iterator itmain = maintree.iterator();
        Iterator itsek1 = sek1.iterator();
        Iterator itsek2 = sek2.iterator();

        System.out.println("Ausgabe, des Main Baumes in order:");
        while (itmain.hasNext()){
            System.out.print(itmain.next()+" ");
        }
        System.out.println();
        System.out.println();

        
        System.out.println("Ausgabe, des sek1 Baumes in order:");
        while (itsek1.hasNext()){
            System.out.print(itsek1.next()+" ");
        }
        System.out.println();
        System.out.println();

        System.out.println("Ausgabe, des sek2 Baumes in order:");
        while (itsek2.hasNext()){
            System.out.print(itsek2.next()+" ");
        }
        System.out.println();
        System.out.println();



        System.out.println("Containsall:");
        System.out.println(maintree.containsall(sek1));     // funktioniert nicht
        System.out.println(maintree.containsall(sek2));

        System.out.println();
        
        System.out.println("containsfist:");
        System.out.println(maintree.contains(new CompRational<Fractional>(fistzaehler,fistnenner)));

        System.out.println();

        System.out.println("containslast:");
        System.out.println(maintree.contains(new CompRational<Fractional>(lastzaehler,lastnenner)));
        System.out.println();

        System.out.println("containsmax:"+maintree.max());
        System.out.println(maintree.contains(maintree.max()));
        System.out.println();

        System.out.println("containsmin:"+maintree.min());
        System.out.println(maintree.contains(maintree.min()));
        System.out.println();

        System.out.println("containsrandoms:");
        for(int n1 = 0; n1 < numberofints; n1++){
            if(maintree.contains(new CompRational<Fractional>(arrayofrandomints[n1][0],arrayofrandomints[n1][1])))
            System.out.println(new CompRational<Fractional>(arrayofrandomints[n1][0],arrayofrandomints[n1][1])+"  -  ("+arrayofrandomints[n1][0]+"/"+arrayofrandomints[n1][1]+")");
        }

        System.out.println();
        System.out.println("done");
    }
}