import java.util.List;
import java.util.ArrayList;

public class LeafNode extends Node{
    // List of Pointers to Records
    private List<List<Record>> records;
    private Node nextNode;
    //prevNode are nodes of same height
    private Node prevNode;
    private NonLeafNode parentNode;

    public LeafNode() {
        this.records = new ArrayList<>();
        this.nextNode = null;
        this.prevNode = null;
        this.parentNode = null;
    }

    public int numKeyRecordEntries() {
        return records.size();
    }

    public int totalRecords() {
        int total = 0;
        for (List<Record> sameKeyRecords : records) {
            total += sameKeyRecords.size();
        }
        return total;
    }

    public int totalSameKeyRecords(int index) {
        return this.records.get(index).size();
    }

    public void addRecord(int index, Record record) {
        this.records.get(index).add(record);
    }
    
    public void removeRecord(int index, Record record) {
        this.records.get(index).remove(record);
        // Missing check for empty list of sameKeyRecords
    }

    public List<List<Record>> getRecords() {
        return this.records;
    }

    public void setRecords(List<List<Record>> records) {
        this.records = records;
    }

    public void createRecordList(int index) {
        this.records.add(index, new ArrayList<>());
    }

    public List<Record> getSameKeyRecords(int index) {
        return this.records.get(index);
    }

    public List<Record> removeSameKeyRecords(int index) {
        return this.records.remove(index);
    }

    public Node getNextNode() {
        return this.nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void setPrevNode(Node prevNode){
        this.prevNode = prevNode;
    }

    public void setParentNode(NonLeafNode parentNode){
        this.parentNode = parentNode;
    }

    public Node getPreNode(){
        return this.prevNode;
    }

    public NonLeafNode getParentNode(){
        return this.parentNode;
    }


}
