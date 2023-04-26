package app.exercise.testing;
import java.util.Stack;                 // muesste noch implementiert werden
import app.exercise.algebra.*;      

public class rpn{
    public static void main(String[] args){                 // bei der eingabe muss "*" verwendet werden
        int lengthb = 0;                                  // laenge buchstaben
        Rational[] z = new Rational[args.length];
        Rational result = new Rational();

        for(int n1 = 0; n1 < args.length; n1++){
            if(args[n1].equals("+")){
                if(lengthb < 2){
                    System.out.println("Das ist keine gültige Eingabe!");
                }
                else{
                    lengthb = lengthb -2;
                    z[lengthb].add(z[lengthb+1]);
                    result = z[lengthb].clone();
                    lengthb += 1;
                    System.out.println("zwischenresult: "+result.toString());
                }
            }
            else if(args[n1].equals("-")){
                if(lengthb < 2){
                    System.out.println("Das ist keine gültige Eingabe!");
                }
                else{
                    lengthb = lengthb -2;
                    z[lengthb].sub(z[lengthb+1]);
                    result = z[lengthb].clone();
                    lengthb += 1;
                    System.out.println("zwischenresult: "+result.toString());
                }
            }
            else if(args[n1].equals("*")){
                if(lengthb < 2){
                    System.out.println("Das ist keine gültige Eingabe!");
                }
                else{
                    lengthb = lengthb -2;
                    z[lengthb].mul(z[lengthb+1]);
                    result = z[lengthb].clone();
                    lengthb += 1;
                    System.out.println("zwischenresult: "+result.toString());
                }
            }
            else if(args[n1].equals("/")){
                if(lengthb < 2){
                    System.out.println("Das ist keine gültige Eingabe!");
                }
                else{
                    lengthb = lengthb -2;
                    z[lengthb].div(z[lengthb+1]);
                    result = z[lengthb].clone();
                    lengthb += 1;
                    System.out.println("zwischenresult: "+result.toString());
                }
            }
            else{
                z[lengthb] = new Rational(Integer.parseInt(args[n1]),1);
                lengthb = lengthb+1;
            }
        }
        System.out.println("result: "+result.toString());
    }
}