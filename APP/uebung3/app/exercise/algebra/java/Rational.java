package app.exercise.algebra;

abstract class BasisFraction implements Fractional{
    protected abstract void setND(long numerator, long denominator);

    /** addiert zwei zahlen */
    public void add(Fractional operand){
        long d1 = getD();
        long d2 = operand.getD();
        long n = getN()*d2 + operand.getN()*d1; 
        long d = d1*d2;
        setND(n,d);
        return;
    }

    /** multipliziert zwei zahlen */
    public void mul(Fractional operand){
        setND(getN()* operand.getN(),getD()*operand.getD());
        return;
    }
    
    /** subtrahiert eine zahl von einer anderen */
    public void sub(Fractional operand){
        Fractional a = operand.negation();                  //this.BasisFraction.add(operand.negation());
        long d1 = getD();
        long d2 = a.getD();

        long n = getN()*d2 + a.getN()*d1; 
        long d = d1*d2;
        setND(n,d);
        return;
    }
    
    /** teilt eine zahl durch eine andere */
    public void div(Fractional operand){
        Fractional a = operand.reciprocal();
        setND(getN()*a.getN(),getD()*a.getD());
        return;
    }
    
}

public class Rational extends BasisFraction{
    long nenner;            //oben
    long zaehler;           //unten

    /** konstruktor */
    public Rational(long a, long b){
        zaehler = a;
        nenner = b;
        normalform();
        return;
    }

    /** basis konstruktor */
    public Rational(){
        nenner = 0;
        zaehler = 0;
        return;
    }

    /** tauscht nenner und zaehler */
    public Rational reciprocal(){
        Rational a = new Rational();
        a.zaehler = getD();
        a.nenner = getN();
        return a;
    }

    /** kuerze einen bruch */
    public void kuerze(){
        while(true){
            long l = Math.abs(getN());    // large
            long s = Math.abs(getD());    // small
            long r;             // rest

            if(s > l){
                r = l;       // t ist immer die größere Zahl
                l = s;
                s = r;
            }

            r = s;

            while(r != 0){
                s = r;
                r = l%s;
                l = s;
            }

            if(s == 1 || s == 0){
                return;
            }
            else{
                setND(getN()/s,getD()/s);
            } 
        }
    }

    /** multipliert den zaehler mit -1 */
    public Rational negation(){
        Rational a = new Rational(getN(),getD());
        a.setND(getN()*-1,getD());
        return a;
    }

    /** return zaehler */
    public long getN(){
        return zaehler;
    }

    /** return nenner */
    public long getD(){
        return nenner;
    }

    /** schaut ob zwei brüche gleich sind */
    public boolean equals(Rational a){
        return ((a.getN() == getN()) && (a.getD() == getD()));
    }

    /** ueberschreibt die toString methode */
    public String toString(){
        return zaehler+"/"+nenner;
    }

    /** uebersetzt einen bruch in einen Integer duch teilen und wegstreichen der nachkommastellen */
    public int hashCode(){
        if(nenner != 0){
            return (int) (int) zaehler/ (int) nenner;
        }
        return 0;
    }

    /** returns ein objekt welches die gleiche ist wie dieses  */
    public Rational clone(){
        Rational a = new Rational();
        a.setND(getN(),getD());
        return a;
    }

    /** schaut ob ein bruch sich in der normalform befindet und kuerzt diesen */
    void normalform(){
        if(getD() < 0){
            nenner = nenner * -1;
            zaehler = zaehler * -1;
        }

        if(getD() == 0){
            System.out.println("Nicht definiert!!!");
        }

        kuerze();
        return;
    }

    /** setzt die werte zeahler und nenner */
    protected void setND(long numerator, long denominator){
        zaehler = numerator;
        nenner = denominator;
        normalform();
    }
}
