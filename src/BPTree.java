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



    /* --------------------------- INSERT KEY --------------------------- */



    public void insertKey(int key, Record record) {

        // When the tree is not empty
        if (root != null) {

            // Find key and insert starting from root node
            KeyNodePair newPair = recursiveInsert(root, key, record);

            // If root node is overloaded
            if (newPair != null) {

                // Create new root node
                NonLeafNode newRoot = new NonLeafNode();

                newRoot.addChild(0, root);
                newRoot.addElement(0, newPair.getKey());
                newRoot.addChild(1, newPair.getNode());

                // Set as new root
                this.root = newRoot;
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

    private KeyNodePair recursiveInsert(Node node, int key, Record record) {
        KeyNodePair upperPair = null;
        KeyNodePair newPair = null;

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

            // Find key and insert in child node
            newPair = recursiveInsert(childNode, key, record);

            // If child node is overloaded
            if (newPair != null) {

                // Add key to curent node
                currentNode.addElement(index, newPair.getKey());

                // Add child to current node
                currentNode.addChild(index, newPair.getNode());

                // If size of children exceeds order
                if (currentNode.numChildren() > order) {

                    // Create new internal node
                    upperPair = createInternalNode(currentNode, index, newPair);
                }
            }

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

            // If the key is not already in the node
            if (currentNode.contains(key) == false) {

                // Add key to the node
                currentNode.addElement(index, key);

                // Create List for SameKeyRecords
                currentNode.createRecordList(index);
            }

            // Add record to recordList
            currentNode.addRecord(index, record);

            // If size of records exceeds order
            if (currentNode.numKeyRecordEntries() > order) {

                // Create new leaf node
                upperPair = createLeafNode(currentNode);
            }
        }

        // Return a new (key, node) pair to parent node if a new node is created
        return upperPair;
    }

    private KeyNodePair createInternalNode(NonLeafNode currentNode, int index, KeyNodePair newPair) {

        // Create a new node
        NonLeafNode newNode = new NonLeafNode();

        // Examples:
        // order = 3; 3key, 4ptr; +1key/ptr -> 2key, 3ptr, 1key, 2ptr 
        // p k1 p k2 p k3 p k4 p -> p k1 p k2 p : p k4 p
        // order = 4; 4key, 5ptr; +1key/ptr -> 2key, 3ptr; 2key, 3ptr
        // p k1 p k2 p k3 p k4 p k5 p -> p k1 p k2 p : p k4 p k5 p
        int keyMidpoint = (int)Math.floor(currentNode.numElements() / 2);
        int childMidpoint = (int)Math.floor((currentNode.numChildren() + 1) / 2);

        // Return new (key and node) to parent node
        KeyNodePair upperPair = new KeyNodePair(currentNode.getElement(keyMidpoint+1), newNode);

        // Split Key List
        List<Integer> firstElementsList = currentNode.getElements().subList(0, keyMidpoint+1);
        List<Integer> secondElementsList = currentNode.getElements().subList(keyMidpoint+2, -1);

        newNode.setElements(firstElementsList);
        currentNode.setElements(secondElementsList);

        // Split Children List
        List<Node> firstChildrenList = currentNode.getChildren().subList(0, childMidpoint+1);
        List<Node> secondChildrenList = currentNode.getChildren().subList(childMidpoint+1, -1);

        newNode.setChildren(firstChildrenList);
        currentNode.setChildren(secondChildrenList);

        // Return the new node and its corresponding key as a (key, node) pair
        return upperPair;
    }

    private KeyNodePair createLeafNode(LeafNode currentNode) {

        // Create a new node
        LeafNode newNode = new LeafNode();

        int midpoint = (int)Math.floor((currentNode.numKeyRecordEntries() + 1) / 2);

        // Split Key List
        List<Integer> firstElementsList = currentNode.getElements().subList(0, midpoint+1);
        List<Integer> secondElementsList = currentNode.getElements().subList(midpoint+1, -1);

        newNode.setElements(firstElementsList);
        currentNode.setElements(secondElementsList);

        // Split Record List
        List<List<Record>> firstRecordsList = currentNode.getRecords().subList(0, midpoint+1);
        List<List<Record>> secondRecordsList = currentNode.getRecords().subList(midpoint+1, -1);

        newNode.setRecords(firstRecordsList);
        currentNode.setRecords(secondRecordsList);

        // Set Next Nodes
        newNode.setNextNode(currentNode.getNextNode());
        currentNode.setNextNode(newNode);

        // Return the new node and its corresponding key as a (key, node) pair
        return (new KeyNodePair(newNode.getElement(0), newNode));
    }



    /* --------------------------- DELETE KEY --------------------------- */



    public void deleteKey(int Key) {
        // CODE TO DELETE KEY
    }



    /* --------------------------- SEARCH KEY --------------------------- */



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
                if (key < elements.get(index)) {
                    nextNode = currentNode.getChild(index);
                    break;
                }
            }

            // Otherwise use last bucket
            if (nextNode == null) {
                nextNode = currentNode.getChild(index);
            }

            node = nextNode;
        }

        return (LeafNode)node;
    }



    /* --------------------------- TESTING --------------------------- */



    public static void main(String[] args) {
        BPTree tree = new BPTree(3);
    }
}
