import java.util.List;
import java.util.ArrayList;

public class Node {
    // List of Elements (Keys or Children)
    private List<Integer> elements;
    private NonLeafNode parentNode;

    public Node() {
        this.elements = new ArrayList<>();
        this.parentNode = null;
    }

    public List<Integer> getElements() {
        return this.elements;
    }

    public void setElements(List<Integer> elements) {
        this.elements = elements;
    }

    public int numElements() {
        return this.elements.size();
    }

    public int getElement(int index) {
        return this.elements.get(index);
    }

    public void addElement(int index, int element) {
        this.elements.add(index, element);
    }

    public int removeElement(int index) {
        return this.elements.remove(index);
    }

    public boolean contains(int element) {
        return this.elements.contains(element);
    }

    public NonLeafNode getParentNode(){
        return this.parentNode;
    }

    public void setParentNode(NonLeafNode parentNode){
        this.parentNode = parentNode;
    }
}