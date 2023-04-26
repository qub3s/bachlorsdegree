package app.exercise.testing;
import app.exercise.algebra.*;
import app.exercise.adt.*;
import java.util.*;

/** testclass */
public class test<E extends Comparable<E>>{
    /**main */
    public static void main(String args[]){
        CompRational<Fractional> A = new CompRational<Fractional>(1,4);
        CompRational<Fractional> B = new CompRational<Fractional>(1,2);
        CompRational<Fractional> C = new CompRational<Fractional>(1,8);
        CompRational<Fractional> D = new CompRational<Fractional>(1,1);
        CompRational<Fractional> E = new CompRational<Fractional>(1,3);
        CompRational<Fractional> F = new CompRational<Fractional>(1,6);
        CompRational<Fractional> G = new CompRational<Fractional>(1,10);

        CompRational<Fractional> H = new CompRational<Fractional>(1,11);


        //System.out.println(B.compareTo(A));

        BSTree a = new BSTree();
        BSTree b = new BSTree();

        b.add(B);
        b.add(A);
        b.add(H);

        System.out.println(G.compareTo(G));

        System.out.println(a.add(A));   // 1/4
        System.out.println(a.add(B));   // 1/2
        System.out.println(a.add(C));   // 1/8
        System.out.println(a.add(D));   // 1
        System.out.println(a.add(E));   // 1/3
        System.out.println(a.add(F));   // 1/6
        System.out.println(a.add(G));   // 1/10

        System.out.println();


        System.out.println(a.remove(A));
        
        Iterator it = a.iterator();

        System.out.println(it.next());   // 1
        System.out.println(it.next());   // 1
        System.out.println(it.next());   // 1
        System.out.println(it.next());   // 1
        System.out.println(it.next());   // 1
        System.out.println(it.next());   // 1
        System.out.println(it.hasNext());   // 1
        System.out.println(it.next());   // 1
        System.out.println(it.hasNext());   // 1
        System.out.println(a.containsall(b));

        //  Object[] b = a.toArray();
        //System.out.println(b[0]);

        /*
        for(int x = 0; x < 1; x++){
            System.out.println(b[x]);
        }
        */
        
    }
}