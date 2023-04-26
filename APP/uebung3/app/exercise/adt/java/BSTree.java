package app.exercise.adt;
import java.util.*;
import app.exercise.visualtree.*;

/** Klasse der node */
class Node<E extends Comparable<E>> implements DrawableTreeElement<E>{     // WIESO <E> nach comparable (Stack overflow) !!!NACHFRAGEN!!!
    E data;    
    Node<E> left;    
    Node<E> right;

    /** konstruktor */
    public Node(E x){    
        data = x;    
        left = null;    
        right = null;    
    }

    /**drawableimplementation */
    public Node<E> getLeft(){
        return left;
    }

    /**drawableimplementation */
    public Node<E> getRight(){
        return right;
    }

    /**drawableimplementation */
    public boolean isRed(){
        return false;
    }

    /**drawableimplementation */
    public E getValue(){
        return data;
    }
} 

/** bstree klasse */
public class BSTree<E extends Comparable<E>> extends AbstractCollection<E>{
    Node<E> root = null;
    int size = 0;

    /**returns root node*/
    public Node<E> root(){
        return root;
    }

    /**returns min */
    public E min(){
        Node<E> min = root;
        while(min.left != null){
            min = min.left;
        }
        return min.data;
    }

    /** retrns max */
    public E max(){
        Node<E> max = root;
        while(max.right != null){
            max = max.right;
        }
        return max.data;
    }

    /**groeße in nodes */
    public int size(){
        return size;
    }

    /**gibt an ob es eine root gibt */
    public boolean isEmpty(){
        if(size == 0){
            return true;
        }
        else{
            return false;
        }
    }

    /**ist element inhalten */
    public boolean contains(E o){
        if(isEmpty()){
            return false;
        }

        Node<E> a = root;
        while(true){
            if(a.data.compareTo(o) == 0){
                return true;
            }
            else if(a.data.compareTo(o) < 0){
                if(a.right == null){
                    return false;
                }
                else{
                    a = a.right;
                }
            }
            else if(a.data.compareTo(o) > 0){
                if(a.left == null){
                    return false;
                }
                else{
                    a = a.left;
                }
            }
        }
    }

    /** sind alle elemente eines Baumes enthalten */
    public boolean containsall(Collection<E> c){
        Iterator<E> it = c.iterator();

        while(it.hasNext()){ 
            if(!contains(it.next())){
                return false;
            }
        }
        return true;
    }

    /**baum in array der groesse nach aufsteigend */
    public Object[] toArray(){
        if(isEmpty()){
            return null;
        }
        else{
            System.out.println("lol");
            Object[] a = new Object[size];
            a[0] = root.data;
            return a;
        }
    }

    /**umwandlung in string (1/2) */
    public String toString(){
        return "lol";
    }

    /**erstellung eines iterators welcher von klein nach gross druchlauft */
    public Iterator<E> iterator(){
        if(isEmpty()){
            return null;
        }
        return new BSTreeIterator();
    }
    
    /**element hinzufügen */
    public boolean add(E e){
        if(contains(e)){
            return false;
        }
        if(isEmpty()){
            root = new Node<E>(e);
            size = size + 1;
            return true;
        }
        else{
            Node<E> a = root;
            while(true){
                if(a.left == null && a.data.compareTo(e) > 0){
                    a.left = new Node<E>(e);
                    size = size + 1;
                    //System.out.println("add"+a.left.data);
                    return true;
                }
                else if(a.right == null && a.data.compareTo(e) < 0){
                    a.right = new Node<E>(e);
                    size = size + 1;
                    return true;
                }
                else if(a.data.compareTo(e) < 0){
                    a = a.right;
                }
                else{
                    a = a.left;
                }
            }
        }
        //return true;
    }
    
    /**element loeschen */
    public boolean remove(E o){         // funktioniert nicht nach idee fragen
        if(isEmpty()){
            return false;
        }
        if(contains(o) == false){
            return false;
        }

        Node<E> runner = root;
        Node<E> follower = runner;
        Node<E> followersmallest;
        Node<E> smallest;

        if(o.compareTo(root.data) == 0){
            if(root.right == null && root.left == null){
                root = null;
                size = size - 1;
                return true;
            }
            else{
                root = root.left;
                size = size - 1;
                return true;
            }
        }

        while(true){
            if(runner.data.compareTo(o) == 0){
                if(runner.data.compareTo(follower.data) < 0){
                    if(runner.left == null && runner.right == null){
                        follower.left = null;
                        size = size - 1;
                        return true;
                    }
                    else if(runner.right == null){
                        follower.left = runner.left;
                        size = size - 1;
                        return true;
                    } 
                    else{
                        System.out.println("start");
                        smallest = runner.right;
                        followersmallest = runner;

                        System.out.println("smallest:"+smallest.data);
                        System.out.println("followersmallest:"+followersmallest.data);

                        while(smallest.left != null){              // kleinstes Element finden
                            followersmallest = smallest;
                            smallest = smallest.left;
                        }

                        if(followersmallest.data.compareTo(runner.data) == 0){
                            runner.right = null;
                        }
                        else{
                            followersmallest.left = null; 
                        } 
                        follower.left = smallest;                   // smallest als das neue element einfügen
                        smallest.right = runner.right;              // smallest die nachfolger des ursprünglichen knoten geben
                        smallest.left = runner.left;
                        size = size - 1;
                        return true;
                    }
                }
                else{
                    if(runner.left == null && runner.right == null){
                        follower.right = null;
                        size = size - 1;
                        return true;
                    }
                    else if(runner.right == null){
                        follower.left = runner.left;
                        size = size - 1;
                        return true;
                    } 
                    else{
                        System.out.println("start");
                        smallest = runner.right;
                        followersmallest = runner;

                        System.out.println("smallest:"+smallest.data);
                        System.out.println("followersmallest:"+followersmallest.data);

                        while(smallest.left != null){              // kleinstes Element finden
                            followersmallest = smallest;
                            smallest = smallest.left;
                        }

                        if(followersmallest.data.compareTo(runner.data) == 0){
                            runner.right = null;
                        }
                        else{
                            followersmallest.left = null; 
                        } 
                        follower.right = smallest;                   // smallest als das neue element einfügen
                        smallest.right = runner.right;              // smallest die nachfolger des ursprünglichen knoten geben
                        smallest.left = runner.left;
                        size = size - 1;
                        return true;
                    }
                }
            }
            else if(runner.data.compareTo(o) < 0){
                follower = runner;
                runner = runner.right;
            }
            else if(runner.data.compareTo(o) > 0){
                follower = runner;
                runner = runner.left;
            }

        }
    }

    /**iterator klasse */
    class BSTreeIterator implements Iterator<E>{
        Node<E> smallest;
        Node<E> runner = root;
        Node<E> follower = root;
        int count = 0;

        public BSTreeIterator(){
            smallest = root;

            while(smallest.left != null){              // kleinstes Element finden
                smallest = smallest.left;
            }
        }

        /** gibt es ein naechstes element */
        public boolean hasNext(){
            if(count < size){
                return true;
            }
            else{
                return false;
            }
        }

        /** ausgabe des naechsten element */
        public E next(){

            if(count == 0){
                count = count + 1;
                return smallest.data;
            }
            //newSystem.out.println("\nstartvaluesmallest:"+smallest.data);
            count = count + 1;
            boolean schonvorbei = false;        // ob der wert schon darüber liegt
            runner = root;
            follower = root;
            while(true){
                //System.out.println(runner.data);
                //System.out.println(follower.data);
                if(runner.data.compareTo(smallest.data) == 0){
                    if(smallest.right == null){
                        smallest = follower;
                        return smallest.data;
                    }
                    else{
                        smallest = runner.right;
                        while(smallest.left != null){              // kleinstes Element finden
                            smallest = smallest.left;
                        }
                        return smallest.data;

                    }
                }
                /*
                System.out.println("runner"+runner.data);
                System.out.println("follower"+follower.data);
                System.out.println("smallest"+smallest.data);
                */

                if(runner.data.compareTo(smallest.data) > 0){
                    follower = runner;
                    runner = runner.left;
                    //System.out.println("goneleft");

                }
                else{
                    //System.out.println("goneright");
                    runner = runner.right;
                }

            }
        }

        public void remove(){
            throw new UnsupportedOperationException("");
        }
    }
}
