import java.util.List;
import java.util.ArrayList;

public class LeafNode extends Node{
    // List of Pointers to Records
    private List<Record> records;
    private Node nextNode;

    public LeafNode() {
        this.records = new ArrayList<>();
        this.nextNode = null;
    }

    public int numRecords() {
        return this.records.size();
    }

    public void addRecord(int index, Record record) {
        this.records.add(index, record);
    }

    public Record getRecord(int index) {
        return this.records.get(index);
    }

    public Record removeRecord(int index) {
        return this.records.remove(index);
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }
}
