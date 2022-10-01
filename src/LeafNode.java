import java.util.List;
import java.util.ArrayList;

public class LeafNode extends Node{
    // List of Pointers to Records
    private List<List<RecordBlock>> records;
    private Node prevNode;
    private Node nextNode;
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
        for (List<RecordBlock> sameKeyRecords : records) {
            total += sameKeyRecords.size();
        }
        return total;
    }

    public int totalSameKeyRecords(int index) {
        return this.records.get(index).size();
    }

    public void addRecord(int index, RecordBlock record) {
        this.records.get(index).add(record);
    }
    
    public void removeRecord(int index, RecordBlock record) {
        this.records.get(index).remove(record);
        // Missing check for empty list of sameKeyRecords
    }

    public List<List<RecordBlock>> getRecords() {
        return this.records;
    }

    public void setRecords(List<List<RecordBlock>> records) {
        this.records = records;
    }

    public void createRecordList(int index) {
        this.records.add(index, new ArrayList<>());
    }

    public List<RecordBlock> getSameKeyRecords(int index) {
        return this.records.get(index);
    }

    public void addSameKeyRecords(int index, List<RecordBlock> recordsList) {
        this.records.add(index, recordsList);
    }

    public List<RecordBlock> removeSameKeyRecords(int index) {
        return this.records.remove(index);
    }

    public Node getNextNode() {
        return this.nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public Node getPrevNode(){
        return this.prevNode;
    }

    public void setPrevNode(Node prevNode){
        this.prevNode = prevNode;
    }

    public NonLeafNode getParentNode(){
        return this.parentNode;
    }

    public void setParentNode(NonLeafNode parentNode){
        this.parentNode = parentNode;
    }
}
