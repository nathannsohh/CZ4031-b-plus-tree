import java.util.List;
import java.util.ArrayList;

public class BPTree {

    private int order;
    private Node root;
    private int height;

    public BPTree(int order) {
        this.order = order;
        this.root = null;
        this.height = 0;
    }

    public void insertKey(int key, Record record) {

        // When the tree is not empty
        if (root != null) {

            int newKey = recursiveInsert(root, key, record);

            // Add new key in root node
            if (newKey != -1) {
                // TO BE CONTINUED..................................
            }

        // When the tree is empty
        } else {

            // Create root node
            root = new LeafNode();
            LeafNode node = (LeafNode) root;

            // Insert key
            node.addElement(0, key);
            node.addRecord(0, record);
        }
    }

    private int recursiveInsert(Node node, int key, Record record) {
        int upperKey = -1;
        int newKey = -1;

        // If node is internal node
        if (node instanceof NonLeafNode) {
            NonLeafNode currentNode = (NonLeafNode) node;
            Node childNode = null;
            int index;

            List<Integer> elements = currentNode.getElements();

            // Find bucket containing key
            for (index = 0; index < elements.size(); index++) {
                if (key <= elements.get(index)) {
                    childNode = currentNode.getChild(index);
                    break;
                }
            }

            // Otherwise use last bucket
            if (childNode == null) {
                childNode = currentNode.getChild(index);
            }

            newKey = recursiveInsert(childNode, key, record);

        // Otherwise if node is leaf node
        } else {
            LeafNode currentNode = (LeafNode) node;
            int index;

            // Find the location to insert the key
            for (index = 0; index < currentNode.numElements(); index++) {
                if (key < currentNode.getElement(index)) {
                    break;
                }
            }

            // If there is vacancy in the node
            if (currentNode.numKeyRecordEntries() < order) {

                // If the key is not already in the node
                if (currentNode.contains(key) == false) {

                    // Add key to the node
                    currentNode.addElement(index, key);

                    // Create List for SameKeyRecords
                    currentNode.createRecordList(index);
                }

                // Add record to recordList
                currentNode.addRecord(index, record);

            // Otherwise if there is no vacancy
            } else {
                // TO BE CONTINUED...............................
            }

        }

        // If new key needs to be inserted at this level
        if (newKey != -1) {
            // TO BE CONTINUED....................................
        }

        return upperKey;
    }

    public void deleteKey(int Key) {
        // CODE TO DELETE KEY
    }

    public List<Record> searchRecords(int minKey, int maxKey) {
        List<Record> records = new ArrayList<>();

        // If the tree is not empty
        if (root != null) {

            // Find the leaf node
            LeafNode currentNode = findLeafNode(root, minKey);

            // Find specified keys
            int currentIndex = 0;
            int currentKey = currentNode.getElement(0);

            // Find the lower bound
            while (currentKey <= maxKey) {

                // If key is in range, add records
                if (currentKey >= minKey) {
                    List<Record> sameKeyRecords = currentNode.getSameKeyRecords(currentIndex);
                    for (Record record : sameKeyRecords) {
                        records.add(record);
                    }
                }

                // If not last element, go to the next element
                if (currentIndex < currentNode.numElements() - 1) {
                    currentIndex += 1;

                    // If at last element, go to next node
                } else {
                    currentNode = (LeafNode) currentNode.getNextNode();
                    currentIndex = 0;

                    // If there is no next node, stop search
                    if (currentNode == null) {
                        return records;
                    }
                }
                currentKey = currentNode.getElement(currentIndex);
            }
        }

        return records;
    }

    private LeafNode findLeafNode(Node root, int key) {
        Node node = root;

        // While the current node is an internal node
        while (node instanceof NonLeafNode) {
            NonLeafNode currentNode = (NonLeafNode) node;
            Node nextNode = null;
            int index;

            List<Integer> elements = currentNode.getElements();

            // Find bucket containing key
            for (index = 0; index < elements.size(); index++) {
                if (key <= elements.get(index)) {
                    nextNode = currentNode.getChild(index);
                    break;
                }
            }

            // Otherwise use last bucket or go to next node
            if (nextNode == null) {
                nextNode = currentNode.getChild(index);
            }

            node = nextNode;
        }

        return (LeafNode)node;
    }

    public static void main(String[] args) {
        BPTree tree = new BPTree(3);
    }
}
