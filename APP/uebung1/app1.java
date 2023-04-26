class PolynominalGF2{
    final boolean zero;
    final boolean one;
    int laenge;
    private boolean[] polynom; 

    public PolynominalGF2(){
        one = true;
        zero = false;
        laenge = 1;
        polynom = new boolean[1];
        polynom[0] = true;
    }

    public PolynominalGF2(boolean[] a){
        boolean[] tempbool = trim(a);

        laenge = tempbool.length;
        polynom = new boolean[laenge];
        
        for(int n1 = 0; n1 < laenge; n1++){
            polynom[n1] = a[n1];
        }
        
        if(laenge == 0){
            one = false;
            zero = true;
        }
        else if(laenge == 1){
            one = true;
            zero = false;
        }
        else{
            one = false;
            zero = false;
        }

    }

    public boolean[] toArray(){
        boolean[] a = new boolean[laenge];
        for(int n1 = 0; n1 < laenge; n1++){
            a[n1] = polynom[n1];
        }
        return a;
    }

    public boolean isZero(){
        return zero;
    }

    public boolean isOne(){
        return one;
    }

    public PolynominalGF2 clone(){
        return new PolynominalGF2(polynom);
    }

    public boolean equals(PolynominalGF2 a){        // reflextive symetric transitive consistent        
        boolean[] poly1 = a.toArray();
        boolean[] poly2 = toArray();

        if(poly1.length == laenge){
            for(int n1 = 0; n1 < laenge; n1++){
                if(poly1[n1] != poly2[n1]){
                    return false;
                }
            }
        }
        else{
            return false;
        }
        return true;
    }
    
    private boolean[] trim(boolean[] arr){
        int reallength = arr.length-1;
        
        if(!arr[reallength]){
            while(reallength >= 0 && !arr[reallength]){
                reallength -= 1;
            }

            reallength = reallength + 1;
        }
        else{
            reallength = reallength + 1;
        }

        boolean[] a = new boolean[reallength];

        for(int n1 = 0; n1 < reallength; n1++){
            a[n1] = arr[n1];
        }
        return a;
    }

    public int hashCode(){
        int result = 0;
        int potenz = 1;

        for(int n1 = 0; n1 < laenge; n1++){
            if(polynom[n1]){
                result = result + potenz;
            }
            potenz = potenz *2;
        }
        return result;
    }

    public int degree(){
        if (laenge > 0){
            return laenge-1;
        }
        return laenge;
    }

    //Kein return eines objektes da dies nur unnötige koplikationen erzeugt
    public boolean[] shift(int k){
        boolean[] a = new boolean[laenge+k];

        for(int n1 = 0; n1 < laenge+k; n1++){
            a[n1] = false;
        }

        for(int n1 = 0; n1 < laenge; n1++){
            if(polynom[n1]){
                a[n1+k] = true;
            }
        }
        return a;
    }

    public PolynominalGF2 mod(PolynominalGF2 modpoly){
        if(modpoly.degree() <= degree()){
            int shiftlenght = laenge - 1 - modpoly.degree();
            boolean[] shiftmodpoly = modpoly.shift(shiftlenght);
            boolean[] result = new boolean[laenge];
            for(int n1 = 0; n1 < laenge; n1++){
                //System.out.println("real: "+polynom[n1]+"     %:"+shiftmodpoly[n1]);
                if(polynom[n1] && !shiftmodpoly[n1]){
                    result[n1] = true;
                }
                else if(!polynom[n1] && shiftmodpoly[n1]){
                    result[n1] = true;
                }
                else if(polynom[n1] && shiftmodpoly[n1]){
                    result[n1] = false;
                }
                else if(!polynom[n1] && !shiftmodpoly[n1]){
                    result[n1] = false;
                }
            }
            PolynominalGF2 newobjekt = new PolynominalGF2(result);

            if(newobjekt.degree() >= modpoly.degree()){
                return newobjekt.mod(modpoly);
            }
            else{
                return newobjekt;
            }
        }
        return new PolynominalGF2(polynom);
    }

    //https://en.wikipedia.org/wiki/GF%282%29
    public PolynominalGF2 plus(PolynominalGF2 pluspoly){
        int maxlenght;
        boolean[] plusarr = pluspoly.toArray();
        
        if(plusarr.length > laenge){
            maxlenght = plusarr.length;
        }
        else{
            maxlenght = laenge;
        }

        boolean[] result = new boolean[laenge];
        for(int n1 = 0; n1 < laenge; n1++){
            if(plusarr[n1] == polynom[n1]){
                result[n1] = false;
            }
            else{
                result[n1] = true;
            }
        }
        return new PolynominalGF2(result);
    }

    public PolynominalGF2 times(PolynominalGF2 pluspoly){
        boolean[] plusarr = pluspoly.toArray();
        int lenghtarr = laenge + plusarr.length;

        boolean[] result = new boolean[lenghtarr];
        
        for(int n1 = 0; n1 < lenghtarr; n1++){
            result[n1] = false;
        }

        for(int n1 = 0; n1 < laenge; n1++){
            if(polynom[n1]){
                for(int n2 = 0; n2 < plusarr.length; n2++){
                    if(plusarr[n2]){
                        if(result[n1+n2]){
                            result[n1+n2] = false;
                        }
                        else{
                            result[n1+n2] = true;
                        }
                    }
                }
            }
        }
        return new PolynominalGF2(result);
    }

    public String toString(){
        String str = "";
        boolean strset = false;
        if(polynom[0]){
            str = "1";
            strset = true;
        }

        if(laenge > 1 && polynom[1] && strset){
            str = "x + "+str;
            strset = true;
        }
        else if(laenge > 1 && polynom[1]){
            str = "x";
            strset = true;
        }

        for(int n1 = 2; n1 < laenge; n1++){
            if(polynom[n1] && strset){
                str = "x^" + n1 +"+ "+str;
            }
            else if(polynom[n1]){
                str = "x^" + n1 +str;
                strset = true;
            }
        }

        return str;
    }
}

public class app1{
    public static void main(String[] args){
        System.out.println("Testklasse 1");
        System.out.println("i   |   hash    |   x^i");
        System.out.println("-----------------------------");
        PolynominalGF2 modpoly = new PolynominalGF2(new boolean[]{true,true,false,true});
        for(int n1 = 0; n1 < 7; n1++){
            boolean[] temparray = new boolean[n1+1];
            for(int n2 = 0; n2 < n1+1; n2++){
                temparray[n2] = false;
            }
            temparray[n1] = true;
            PolynominalGF2 a = new PolynominalGF2(temparray);
            PolynominalGF2 b = a.mod(modpoly);
            System.out.println(n1+"         "+b.hashCode()+"        "+b.toString());
        }
        
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("Testklasse 2");

        PolynominalGF2 a = new PolynominalGF2(new boolean[]{true,true});
        PolynominalGF2 b = new PolynominalGF2(new boolean[]{true,true});
        PolynominalGF2 modpoly2 = new PolynominalGF2(new boolean[]{true,true,false,true,true,false,false,false,true});
        
        System.out.println(" | 0          1        2         3         4         5         6         7         8         9         a        b         c         d         e        f");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.print("0| 01        ");
        for(int n1 = 2; n1 < 257; n1++){
            System.out.print(String.format("%02x", a.mod(modpoly2).hashCode())+"        ");
            a = a.times(b);
            if(n1 %16 == 0){
                System.out.print("\n"+String.format("%01x", n1/16)+"| ");
            }
        }
    }
}