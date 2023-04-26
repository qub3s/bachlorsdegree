package app.exercise.testing;
import app.exercise.algebra.*;

/**testing of rational */
public class RationalTesting {
    /**main */
    public static void main(String[] args) {

        System.out.println();
        System.out.println("Testklasse!");
        Rational a = new Rational(1,2);
        Rational b = new Rational(4,-2);

        System.out.println("a: "+a.toString());
        System.out.println("b: "+b.toString());

        Rational c = a.clone();
        Rational d = b.clone();

        System.out.println("clone a: "+c.toString());
        System.out.println("clone b: "+d.toString());

        a.add(d);
        b = new Rational(0,-2);
        c.add(b);
        
        System.out.println();
        System.out.println("Add:");
        System.out.println("-2 + 1/3 = "+a.toString());
        System.out.println("1/3 + 0 = "+c.toString());

        a = new Rational(-2,1);
        b = new Rational(1,3);
        c = new Rational(1,3);
        d = new Rational(0,1);

        a.sub(b);
        c.sub(d);

        System.out.println();
        System.out.println("Sub:");
        System.out.println("-2 - 1/3 = "+a.toString());
        System.out.println("1/3 - 0 = "+c.toString());

        a = new Rational(-2,1);
        b = new Rational(1,3);
        c = new Rational(1,3);
        d = new Rational(0,1);

        a.mul(b);
        c.mul(d);
        
        System.out.println();
        System.out.println("Mult:");
        System.out.println("-2 * 1/3 = "+a.toString());
        System.out.println("1/3 * 0 = "+c.toString());

        a = new Rational(-2,1);
        b = new Rational(1,3);
        c = new Rational(1,3);
        d = new Rational(0,1);

        a.div(b);
        c.div(d);

        System.out.println();
        System.out.println("Div:");
        System.out.println("-2 / 1/3 = "+a.toString());
        System.out.println("1/3 / 0 = "+c.toString());

        System.out.println();
        System.out.println("hashCode:");
        System.out.println("-6/1 = "+a.hashCode());
        System.out.println("0/1 = "+c.hashCode());

        System.out.println();

    }
}