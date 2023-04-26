package app.exercise.algebra;

public class CompRational<T extends Fractional> extends Rational implements Comparable<T>{

    public CompRational(long a, long b){
        zaehler = a;
        nenner = b;
        normalform();
        return;
    }

    public int compareTo(T a){
        return (int)(zaehler*a.getD()-a.getN()*nenner);
    }

}

