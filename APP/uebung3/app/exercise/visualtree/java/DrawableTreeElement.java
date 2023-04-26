package app.exercise.visualtree;

public interface DrawableTreeElement<T> {
    public DrawableTreeElement<T> getLeft();	
    public DrawableTreeElement<T> getRight();
    public boolean isRed();
    public T getValue();
}
