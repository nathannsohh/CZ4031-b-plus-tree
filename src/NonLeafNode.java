import java.util.List;
import java.util.ArrayList;

public class NonLeafNode extends Node {
    // List of Pointers to Child Nodes
    private List<Node> children;

    public NonLeafNode() {
        this.children = new ArrayList<>();
    }

    public int numChildren() {
        return this.children.size();
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public void addChild(int index, Node child) {
        this.children.add(index, child);
    }

    public Node getChild(int index) {
        return this.children.get(index);
    }

    public Node removeChild(int index) {
        return this.children.remove(index);
    }
}
