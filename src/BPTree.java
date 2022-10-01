import java.util.List;
import java.util.ArrayList;

public class BPTree {

    private int order;
    private Node root;
    private int height;
    private int BlockAccess=0;
    private int deletedNode=0;
    private int mergedNode = 0;

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
            node.createRecordList(0);
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
                currentNode.addChild(index + 1, newPair.getNode());

                // If size of children exceeds order
                if (currentNode.numElements() > order) {

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
        List<List<Record>> firstRecordsList = new ArrayList<>();

        for (int i = 0; i < midpoint; i++) {
            firstRecordsList.add(currentNode.getRecords().get(i));
        }

        List<List<Record>> secondRecordsList = new ArrayList<>();

        for (int i = midpoint; i < entrySize; i++) {
            secondRecordsList.add(currentNode.getRecords().get(i));
        }

        currentNode.setRecords(firstRecordsList);
        newNode.setRecords(secondRecordsList);

        // Set Next Nodes
        newNode.setNextNode(currentNode.getNextNode());
        currentNode.setNextNode(newNode);
        //set Prev Node
        newNode.setPrevNode(currentNode);

        // Return the new node and its corresponding key as a (key, node) pair
        return (new KeyNodePair(newNode.getElement(0), newNode));
    }



    /* --------------------------- DELETE KEY --------------------------- */



    public void deleteKey(int Key) {
        // CODE TO DELETE KEY
        
        Node cur = this.root;
        int indexRemoved = -1;
        //minimum number of keys in a Non-Leaf Node
        int minInternalKeys = (int)Math.floor((order)/2);


        //minimum number of keys for a Leaf Node
        int minLeafNodeKeys = (int)Math.floor((order+1)/2);

        //iteration 1

        System.out.println(findLeafNode(root, Key).getElements());

        //while there are nodes that contain the key value
        LeafNode leaf = findLeafNode(root , Key);
        //while(leaf!=null){
        
        //find if the leaf node with the key
        LeafNode target = findLeafNode(root, Key);
        
        //get all keys in target node
        List<Integer> elements = target.getElements();
        System.out.print("before removal: " + target.getElements());

        // Find bucket containing key
        for (int i = 0; i < elements.size(); i++) {
            //if key value is the same as the element value, remove
            if (Key == elements.get(i)) {
                indexRemoved=i;
                elements.remove(i);
                }
        }

        System.out.println("after removal: " + target.getElements());

        //instance - key was removed from the first index of the node
        
        if(elements.size()>=minLeafNodeKeys){
            //if key was removed at the first index, update the parent node
            if(indexRemoved!=1 || target.getPreNode()==null)
                System.out.println("safe delete");
            else{
                int newKey = elements.get(0);
                updateParent(Key, newKey, Key);
            }
            
        }

        //instance - size of current node is less than minLeafNode
            else{
            LeafNode prev = (LeafNode) target.getPreNode();
            LeafNode next = (LeafNode) target.getNextNode();
            if(prev!=null)
                System.out.println("total nodes with left sibling: " + (prev.getElements().size()+elements.size()));
            if(next!=null)
                System.out.println("total nodes with right sibling: " + (next.getElements().size()+elements.size()));

            //check if can borrow nodes from left sibling... if ok proceed
            if( prev!=null && (prev.getElements().size() + elements.size()) <= 2*order &&  order<(prev.getElements().size() + elements.size())){
                int firstKey = elements.get(0); //old key
                int keyMidpoint = (int)Math.floor((prev.getElements().size() + elements.size()) / 2);
                int prevSize = prev.getElements().size();

                System.out.println("left sibling: " + prev.getElements());
                System.out.println("target elements: " + target.getElements());

                for(int i=prevSize-1; i>=keyMidpoint ; i--){
                    System.out.println("prev element:" + prev.getElement(i));
                    //balance out the sibling nodes
                    target.addElement(0, prev.getElement(i));
                    prev.removeElement(i);
                    System.out.println("prev after adding" + prev.getElements());
                    System.out.println("target after adding" + elements);
                    //proceed to adjust the parent node
                    //oldKey == firstKey
                    //newkey is the first index of the new target node
                }
                //System.out.println("New Left Sibling: " + prev.getElements());
                //System.out.println("New Target Sibling: " + target.getElements());
                int newKey = elements.get(0);
                //update the parent node
                updateParent(firstKey, newKey, Key);
            }

             //else check with the right sibling... if ok proceed
            else if(next!=null && (next.getElements().size() + elements.size())<= 2*order && order<(next.getElements().size() + elements.size()) ){
                int firstKey = next.getElements().get(0);
                int keyMidpoint = (int)Math.floor((next.getElements().size() + elements.size()) / 2);
                int nextSize = next.getElements().size();

                System.out.println("right sibling: " + next.getElements());
                System.out.println("target elements: " + target.getElements());
                for(int i=0; i<keyMidpoint-1 ; i++){
                    //balance out the sibling nodes
                   // System.out.println("next element here:" + next.getElement(i));
                    target.addElement(elements.size(), next.getElement(i));
                    next.removeElement(i);
                
                    //System.out.println("target after adding" + elements);
                   // System.out.println("next after adding" + next.getElements());
                    //proceed to adjust the parent node
                    //move upwards to find the parent with the old key, update the key
                }
                int newKey = next.getElement(0);
                //update the parent node
                updateParent(firstKey, newKey, Key);
            }

            //cannot borrow from siblings
            else{
                //if merging, less than equals to the order value
                //merge with left node, delete target node and update parent
                System.out.println("Merging of nodes");
                System.out.println("prev: " + prev.getElements());

                if(prev!=null && prev.numElements()==minLeafNodeKeys){
                    int firstKey = elements.get(0);
                    System.out.println(target.getElements());
                    for (int i=0; i<elements.size();i++){
                        //add the key into the previous node
                        prev.addElement(prev.numElements(), target.getElement(0));
                        //remove key from the target node
                        target.removeElement(0);
                    }
                    System.out.println(prev.getElements());
                    updateParent(firstKey, -1 , Key);
                }

                //merge with right node, delete target node and update parent
                else{
                    if(next!=null && next.numElements()==minLeafNodeKeys){
                        for (int i=0; i<elements.size();i++){
                            //add the key into the previous node
                            prev.addElement(prev.numElements(),elements.get(i));
                            //remove key from the target node
                            elements.remove(0);
                            target.removeElement(0);
                        }
                    }
                }
                mergedNode++;

            }
            

        }
        //leaf=findLeafNode(root, Key);
    //}
    }

    //oldKey - previous key the index 0 of leaf node
    //newKey - new key at index 0 of leaf node
    //targetKey - key that was removed
    public void updateParent(int oldKey , int newKey , int targetKey){
            Node node = root;
            System.out.println("root node: " + node.getElements().toString());
            int minInternalKeys = (int)Math.floor((order)/2);

            while(node instanceof NonLeafNode){
                NonLeafNode currentNode = (NonLeafNode) node;
                System.out.println("currentNode: " + currentNode.getElements().toString());
                for(int index=0; index<node.numElements(); index++){
                    //if key to be deleted is in root
                    if(node.getElement(index)==targetKey && node==root){
                        //check if node size less than min , if no, proceed to delete
                        if(node.numElements()-1>=minInternalKeys){

                        }
                    }


                }
            }




       
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
            BlockAccess++;
        }

        return (LeafNode)node;
    }

    

    /* --------------------------- TESTING --------------------------- */



    public static void main(String[] args) {
        BPTree tree = new BPTree(3);

        /*tree.insertKey(1, new Record("1", 1, 1));
        tree.insertKey(4, new Record("4", 4, 4));
        tree.insertKey(7, new Record("7", 7, 7));
        tree.insertKey(10, new Record("10", 10, 10));
        tree.insertKey(17, new Record("17", 17, 17));
        tree.insertKey(21, new Record("21", 21, 21));
        tree.insertKey(31, new Record("31", 31, 31));
        tree.insertKey(25, new Record("25", 25, 25));
        tree.insertKey(26, new Record("26", 26, 26));
        tree.insertKey(19, new Record("19", 19, 19));
        tree.insertKey(20, new Record("20", 20, 20));
        tree.insertKey(28, new Record("28", 28, 28));
        tree.insertKey(42, new Record("42", 42, 42));
        tree.insertKey(43, new Record("44", 43, 43));
        tree.insertKey(27, new Record("27", 27, 27));*/
        tree.insertKey(1, new Record("1", 1, 1));
        tree.insertKey(4, new Record("4", 4, 4));
        tree.insertKey(7, new Record("7", 7, 7));
        tree.insertKey(10, new Record("10", 10, 10));
        tree.insertKey(21, new Record("21", 21, 21));
        tree.insertKey(20, new Record("20", 20, 20));
        tree.insertKey(25, new Record("25", 25, 25));
        tree.insertKey(31, new Record("31", 31, 31));
        tree.insertKey(19, new Record("19", 19, 19));
        tree.insertKey(17, new Record("17", 17, 17));


        System.out.println("-----After insertion-----");
        tree.print();

        System.out.println();

        System.out.println("Search results for key 3-5");
        List<Record> results = tree.searchRecords(3, 21);

        if (results.size() == 0) {
            System.out.println("No records found");
        }

        for (Record r : results) {
            System.out.printf("%s %f %d\n", r.getTconst(), r.getAverageRating(), r.getNumVotes());
        }

        
        tree.deleteKey(18);
        //System.out.println("-----After Deletion-----");
        tree.print();
       
    
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
