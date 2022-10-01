import java.util.List;
import java.util.ArrayList;

public class BPTree {

    private Node root;

    // parameter n
    private int order;
    // number of nodes
    private int numNodes;
    // height of tree
    private int height;
    // number of deleted nodes
    private int delNodes;

    // list of searched nodes
    private List<Node> searchedNodes;
    private List<Block> accessedBlocks;

    public BPTree(int order) {
        this.root = null;
        this.order = order;
        this.numNodes = 0;
        this.height = 0;
        this.delNodes = 0;
        this.searchedNodes = new ArrayList<>();
        this.accessedBlocks = new ArrayList<>();
    }



    /* --------------------------- INSERT KEY --------------------------- */



    public void insertKey(int key, RecordBlock record) {

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

                // Update parent node
                root.setParentNode(newRoot);
                newPair.getNode().setParentNode(newRoot);

                // Set as new root
                this.root = newRoot;

                // Increase numNodes
                this.numNodes += 1;

                // Increase height
                this.height += 1;
            }

        // When the tree is empty
        } else {

            // Create root node
            root = new LeafNode();
            LeafNode node = (LeafNode) root;

            // Insert key
            node.addElement(0, key);
            node.createRecordList(0);
            node.addRecord(0, record);

            // Increase numNodes
            this.numNodes += 1;

            // Increase height
            this.height += 1;
        }
    }

    private KeyNodePair recursiveInsert(Node node, int key, RecordBlock record) {
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
                if (key < elements.get(index)) {
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
                currentNode.addChild(index + 1, newPair.getNode());

                // If size of children exceeds order
                if (currentNode.numElements() > order) {

                    // Create new internal node
                    upperPair = createInternalNode(currentNode, index, newPair);

                    // Increase numNodes
                    this.numNodes += 1;
                }
            }

        // Otherwise if node is leaf node
        } else {
            LeafNode currentNode = (LeafNode) node;
            int index;

            // Find the location to insert the key
            for (index = 0; index < currentNode.numElements(); index++) {
                if (key <= currentNode.getElement(index)) {
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

                // Increase numNodes
                this.numNodes += 1;
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
        int elementSize = currentNode.numElements();
        int keyMidpoint = (int)Math.floor(elementSize / 2);
        int childrenSize = currentNode.numChildren();
        int childMidpoint = (int)Math.floor((childrenSize + 1) / 2);

        // Return new (key and node) to parent node
        KeyNodePair upperPair = new KeyNodePair(currentNode.getElement(keyMidpoint), newNode);

        // Split Key List
        List<Integer> firstElementsList = new ArrayList<>();

        for (int i = 0; i < keyMidpoint; i++) {
            firstElementsList.add(currentNode.getElement(i));
        }

        List<Integer> secondElementsList = new ArrayList<>();

        for (int i = keyMidpoint+1; i < elementSize; i++) {
            secondElementsList.add(currentNode.getElement(i));
        }

        currentNode.setElements(firstElementsList);
        newNode.setElements(secondElementsList);

        // Split Children List
        List<Node> firstChildrenList = new ArrayList<>();

        for (int i = 0; i < childMidpoint; i++) {
            firstChildrenList.add(currentNode.getChild(i));
        }

        List<Node> secondChildrenList = new ArrayList<>();

        for (int i = childMidpoint; i < childrenSize; i++) {
            secondChildrenList.add(currentNode.getChild(i));
        }

        currentNode.setChildren(firstChildrenList);
        newNode.setChildren(secondChildrenList);

        // Update Parent Node
        newNode.setParentNode(currentNode.getParentNode());

        // Return the new node and its corresponding key as a (key, node) pair
        return upperPair;
    }

    private KeyNodePair createLeafNode(LeafNode currentNode) {

        // Create a new node
        LeafNode newNode = new LeafNode();

        int entrySize = currentNode.numKeyRecordEntries();
        int midpoint = (int)Math.floor((entrySize + 1) / 2);

        // Split Key List
        List<Integer> firstElementsList = new ArrayList<>();

        for (int i = 0; i < midpoint; i++) {
            firstElementsList.add(currentNode.getElement(i));
        }

        List<Integer> secondElementsList = new ArrayList<>();

        for (int i = midpoint; i < entrySize; i++) {
            secondElementsList.add(currentNode.getElement(i));
        }

        currentNode.setElements(firstElementsList);
        newNode.setElements(secondElementsList);

        // Split Record List
        List<List<RecordBlock>> firstRecordsList = new ArrayList<>();

        for (int i = 0; i < midpoint; i++) {
            firstRecordsList.add(currentNode.getRecords().get(i));
        }

        List<List<RecordBlock>> secondRecordsList = new ArrayList<>();

        for (int i = midpoint; i < entrySize; i++) {
            secondRecordsList.add(currentNode.getRecords().get(i));
        }

        currentNode.setRecords(firstRecordsList);
        newNode.setRecords(secondRecordsList);

        // Set prevNode for nextNode
        LeafNode nextNode = (LeafNode)currentNode.getNextNode();
        if (nextNode != null) {
            nextNode.setPrevNode(newNode);
        }

        // Set nextNode for newNode
        newNode.setNextNode(currentNode.getNextNode());

        // Set prevNode for newNode
        newNode.setPrevNode(currentNode);

        // Set nextNode for currentNode
        currentNode.setNextNode(newNode);

        // Update Parent Node
        newNode.setParentNode(currentNode.getParentNode());

        // Return the new node and its corresponding key as a (key, node) pair
        return (new KeyNodePair(newNode.getElement(0), newNode));
    }



    /* --------------------------- DELETE KEY --------------------------- */



    public void deleteKey(int key) {

        int index = 0;

        // Minimum number of keys in a Non-Leaf Node
        int minInternalNodeKeys = (int)Math.floor(order / 2);

        // Minimum number of children in a Non-Leaf Node
        int minInternalChildren = minInternalNodeKeys+1;

        // Minimum number of keys in a Leaf Node
        int minLeafNodeKeys = (int)Math.floor((order + 1) / 2);
            
        // Find the leaf node which contains the key
        LeafNode targetNode = findLeafNode(root, key);

        // Find target key in elements
        for (index = 0; index < targetNode.numElements(); index++) {

            // If target key is the same as the element key, remove
            if (key == targetNode.getElement(index)) {

                // Remove from the element list
                targetNode.removeElement(index); 
                targetNode.removeSameKeyRecords(index);
                System.out.println("key: " + key + " deleted");
                break;
                }
        }

        // If leaf node is of valid size
        if (targetNode.numElements() >= minLeafNodeKeys) {

            // If key was removed at the first index, update the parent node
            if (index == 0) {
                int newKey = targetNode.getElement(0);
                updateParent(key, newKey, key);
            }

        // If size of target node is less than minLeafNode
        } else {
            LeafNode prev = (LeafNode) targetNode.getPrevNode();
            LeafNode next = (LeafNode) targetNode.getNextNode();

            if (prev == null) System.out.println("prev is null");
            if (next == null) System.out.println("next is null");

            // If size of left sibling node > minimum number of keys
            if (prev != null && prev.numElements() > minLeafNodeKeys) {

                // Borrow a key from left sibling node
                int prevSize = prev.numElements();
                int borrowedKey = prev.removeElement(prevSize-1);
                List<RecordBlock> borrowedRecord = prev.removeSameKeyRecords(prevSize-1);

                // Insert borrowed key into target node
                targetNode.addElement(0, borrowedKey);
                targetNode.addSameKeyRecords(0, borrowedRecord);

                int newKey = targetNode.getElement(0);

                // Update the parent node
                updateParent(key, newKey, key);

            // If size of right sibling node > minimum number of keys
            } else if (next != null && next.numElements() > minLeafNodeKeys) {

                // Borrow a key from right sibling node
                int targetSize = targetNode.numElements();
                int borrowedKey = next.removeElement(0);
                List<RecordBlock> borrowedRecord = prev.removeSameKeyRecords(0);

                // Insert borrowed key into target node
                targetNode.addElement(targetSize, borrowedKey);
                targetNode.addSameKeyRecords(targetSize, borrowedRecord);

                int newKey = next.getElement(0);

                // Update the parent node
                updateParent(borrowedKey, newKey, borrowedKey);

            // Cannot borrow key from sibling nodes
            // } else {
            //     //if merging, less than equals to the order value
            //     //merge with left node, delete target node and update parent
            //     if(prev.numElements()==minInternalNodeKeys){
            //         int firstKey = elements.get(0);
            //         for (int i=0; i<elements.size();i++){
            //             //add the key into the previous node
            //             prev.addElement(prev.numElements(),elements.get(i));
            //             //remove key from the target node
            //             elements.remove(0);
            //             targetNode.removeElement(0);
            //         }
            //         updateParent(firstKey, -1 , key);
            //     }

            //     //merge with right node, delete target node and update parent
            //     else{
            //         if(prev.numElements()==minInternalNodeKeys){
            //             for (int i=0; i<elements.size();i++){
            //                 //add the key into the previous node
            //                 prev.addElement(prev.numElements(),elements.get(i));
            //                 //remove key from the target node
            //                 elements.remove(0);
            //                 targetNode.removeElement(0);
            //             }
            //         }
            //     }
            //     this.delNodes += 1;
            }
        }
    }

    //oldKey - previous key at index 0 of leaf node
    //newKey - new key at index 0 of leaf node
    //targetKey - key that was removed
    public void updateParent(int oldKey, int newKey, int targetKey) {
        Node node = root;
        NonLeafNode currentNode = null;

        int childIndex = -1;
        int index = 0;

        while (node instanceof NonLeafNode) {
            currentNode = (NonLeafNode)node;

            // Find index of child node containing oldKey
            for (index = 0; index < node.numElements(); index++) {

                if (oldKey < node.getElement(index)) {
                    childIndex = index;
                    break;
                }
            }

            // If not found, use last child node
            if (childIndex == -1) {
                childIndex = index;
            }
            
            // Find oldKey in current node
            for (index = 0; index < node.numElements(); index++) {

                // If found, replace oldKey with newKey
                if (node.getElement(index) == targetKey || node.getElement(index) == oldKey) {

                    node.removeElement(index);
                    node.addElement(index, newKey);
                }
            }

            // Continue search for oldKey in child node
            node = currentNode.getChild(childIndex);
        }
    }



    /* --------------------------- SEARCH KEY --------------------------- */



    public List<RecordBlock> searchRecords(int minKey, int maxKey) {
        this.searchedNodes.clear();
        this.accessedBlocks.clear();

        List<RecordBlock> records = new ArrayList<>();

        // If the tree is not empty
        if (root != null) {

            // Find the leaf node
            LeafNode currentNode = findLeafNode(root, minKey);

            // Add leaf node to searched nodes
            this.searchedNodes.add(currentNode);

            // Find specified keys
            int currentIndex = 0;
            int currentKey = currentNode.getElement(0);

            // Find the lower bound
            while (currentKey <= maxKey) {

                // If key is in range, add records
                if (currentKey >= minKey) {
                    List<RecordBlock> sameKeyRecords = currentNode.getSameKeyRecords(currentIndex);
                    for (RecordBlock record : sameKeyRecords) {
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

                    // Add leaf node to searched nodes
                    this.searchedNodes.add(currentNode);
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

            // Add node to list of searched nodes
            this.searchedNodes.add(node);

            // Find bucket containing key
            for (index = 0; index < currentNode.numElements(); index++) {
                if (key < currentNode.getElement(index)) {
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



    /* --------------------------- PRINT INFO --------------------------- */
    


    public void printExp2() {
        // System.out.println("-----Experiment 2-----");
        System.out.printf("n = %d\n", this.order);
        System.out.printf("Number of nodes = %d\n", this.numNodes);
        System.out.printf("Height of tree = %d\n", this.height);
        System.out.println();

        System.out.println("Content of root node");
        for (int element : this.root.getElements()) {
            System.out.printf("%d ", element);
        }
        System.out.println("\n");
        
        if (this.root instanceof NonLeafNode) {
            System.out.println("Content of first child node");
            for (int element : ((NonLeafNode)this.root).getChild(0).getElements()) {
                System.out.printf("%d ", element);
            }
            System.out.println("\n");
        } else {
            System.out.println("There is no child node");
        }
    }

    public void printNodesAccessed() {
        int count = 0;
        int index = 0;

        System.out.printf("Number of index nodes accessed: %d\n\n", this.searchedNodes.size());

        for (Node node : searchedNodes) {

            System.out.printf("Index node content: ");

            for (index = 0; index < node.numElements() - 1; index++) {
                System.out.printf("%d, ", node.getElement(index));
            }
            System.out.printf("%d", node.getElement(index));

            System.out.println();
            count++;
            if (count == 5) {
                break;
            }
        }
    }

    public void printExp5() {
        System.out.println("-----Experiment 5-----");
        System.out.printf("Number of deleted nodes = %d\n", this.delNodes);
        System.out.printf("Number of nodes = %d\n", this.numNodes);
        System.out.printf("Height of tree = %d\n", this.height);
        System.out.println();

        System.out.println("Content of root node");
        for (int element : this.root.getElements()) {
            System.out.printf("%d ", element);
        }
        System.out.println("\n");
        
        if (this.root instanceof NonLeafNode) {
            System.out.println("Content of first child node");
            for (int element : ((NonLeafNode)this.root).getChild(0).getElements()) {
                System.out.printf("%d ", element);
            }
            System.out.println("\n");
        } else {
            System.out.println("There is no child node");
        }
    }



    /* --------------------------- TESTING --------------------------- */



    public static void main(String[] args) {
        // Exp = 2, 5
        int expNum = 5;

        // Exp 2
        if (expNum == 2) {
            BPTree tree = new BPTree(3);

            tree.insertKey(1, new RecordBlock(new Record("1", 1, 1), null));
            tree.insertKey(4, new RecordBlock(new Record("4", 4, 4), null));
            tree.insertKey(7, new RecordBlock(new Record("7", 7, 7), null));
            tree.insertKey(10, new RecordBlock(new Record("10", 10, 10), null));
            tree.insertKey(17, new RecordBlock(new Record("17", 17, 17), null));
            tree.insertKey(21, new RecordBlock(new Record("21", 21, 21), null));
            tree.insertKey(31, new RecordBlock(new Record("31", 31, 31), null));
            tree.insertKey(25, new RecordBlock(new Record("25", 25, 25), null));
            tree.insertKey(19, new RecordBlock(new Record("19", 19, 19), null));
            tree.insertKey(20, new RecordBlock(new Record("20", 20, 20), null));
            tree.insertKey(28, new RecordBlock(new Record("28", 28, 28), null));
            tree.insertKey(42, new RecordBlock(new Record("42", 42, 42), null));
            tree.insertKey(20, new RecordBlock(new Record("20A", 20, 20), null));
            tree.insertKey(21, new RecordBlock(new Record("21A", 21, 21), null));
            // tree.insertKey(4, new RecordBlock(new Record("4A", 4, 4), null));
            tree.insertKey(17, new RecordBlock(new Record("17A", 17, 17), null));
            tree.insertKey(19, new RecordBlock(new Record("19A", 19, 19), null));

            tree.print();
            System.out.println();

            tree.printExp2();

            System.out.println("Search results for key 3-22");
            List<RecordBlock> results = tree.searchRecords(3, 22);

            if (results.size() == 0) {
                System.out.println("No records found");
            }

            for (RecordBlock rb : results) {
                Record r = rb.getRecord();
                System.out.printf("%s %f %d\n", r.getTconst(), r.getAverageRating(), r.getNumVotes());
            }
        }

        // Exp 5
        if (expNum == 5) {
            BPTree tree = new BPTree(3);

            tree.insertKey(1, new RecordBlock(new Record("1", 1, 1), null));
            tree.insertKey(4, new RecordBlock(new Record("4", 4, 4), null));
            tree.insertKey(7, new RecordBlock(new Record("7", 7, 7), null));
            tree.insertKey(10, new RecordBlock(new Record("10", 10, 10), null));
            tree.insertKey(17, new RecordBlock(new Record("17", 17, 17), null));
            tree.insertKey(19, new RecordBlock(new Record("19", 19, 19), null));
            tree.insertKey(20, new RecordBlock(new Record("20", 20, 20), null));
            tree.insertKey(21, new RecordBlock(new Record("21", 21, 21), null));
            tree.insertKey(31, new RecordBlock(new Record("31", 31, 31), null));
            tree.insertKey(25, new RecordBlock(new Record("25", 25, 25), null));
            tree.insertKey(5, new RecordBlock(new Record("5", 5, 5), null));

            tree.insertKey(30, new RecordBlock(new Record("30", 30, 30), null));
            tree.insertKey(22, new RecordBlock(new Record("22", 22, 22), null));

            tree.print();
            System.out.println();

            tree.deleteKey(7);

            tree.print();
            System.out.println();

            tree.deleteKey(25);

            tree.print();
            System.out.println();

            tree.deleteKey(20);

            tree.print();
            System.out.println();

            tree.printExp5();

            // System.out.println("Search results for key 3-8");
            // List<RecordBlock> results = tree.searchRecords(3, 8);

            // if (results.size() == 0) {
            //     System.out.println("No records found");
            // }

            // for (RecordBlock rb : results) {
            //     Record r = rb.getRecord();
            //     System.out.printf("%s %f %d\n", r.getTconst(), r.getAverageRating(), r.getNumVotes());
            // }

            // tree.printNodesAccessed();
        }
    }

    public void print() {
        List<Node> nodes = new ArrayList<>();
        int numNodes = 1;
        int count = 0;
        Node cur;

        nodes.add(root);

        if (root == null) {
            System.out.println("-----Tree is empty-----");
            return;
        }

        System.out.println("-----Printing Tree-----");

        while (nodes.isEmpty() == false) {
            cur = nodes.get(0);

            // System.out.printf("size of node: %d\n", cur.numElements());
            for (int e : cur.getElements()) {
                System.out.printf("%d ", e);

            }

            if (cur instanceof NonLeafNode) {
                for (Node n : ((NonLeafNode)cur).getChildren()) {
                    nodes.add(n);
                }
            }

            count += 1;

            if (count < numNodes) {
                System.out.printf("| ");
            } else {
                System.out.printf("\n");
                numNodes = nodes.size() - 1;
                count = 0;
            }

            nodes.remove(0);
        }
    }
}
