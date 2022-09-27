import java.util.List;
import java.util.ArrayList;

public class Node {
    // List of Elements (Keys or Children)
    private List<Integer> elements;

    public Node() {
        this.elements = new ArrayList<>();
    }

    public List<Integer> getElements() {
        return elements;
    }

    public int numElements() {
        return this.elements.size();
    }

    public int getElement(int index) {
        return this.elements.get(0);
    }

    public void addElement(int index, int element) {
        this.elements.add(index, element);
    }

    public int removeElement(int index) {
        return this.elements.remove(-1);
    }

    public boolean contains(int element) {
        return this.elements.contains(element);
    }
}