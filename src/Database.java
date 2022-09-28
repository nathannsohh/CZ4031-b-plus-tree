import java.util.*;

public class Database {
    // Size of Memory
    private int poolSize;
    // Size of Block
    private int blkSize;
    // Number of blocks allocated
    private int numAllocatedBlk;
    // Number of blocks remaining
    private int numRemainingBlk;
    // A counter to check if a block is full
    private int recordCounter = 0;
    // Size of a record in bytes
    private int recordSize;

    // A counter to check if a block worth of records has been deleted
    private int deletedRecords = 0;

    // Number of records per block
    public int recordsPerBlk;
    // Total number of records in the DB
    public int totalNumRecords = 0;
    
    // A list containing all the allocated blocks
    private List<Block> blkList;
    // Current block that is being filled. Once this block is filled, it will pushed into the blkLis and will be reset
    private Block blk;

    public Database(int poolSize, int blkSize) {
        this.poolSize = poolSize;
        this.blkSize = blkSize;
        this.numAllocatedBlk = 0;
        this.numRemainingBlk = poolSize/blkSize;
        this.recordSize = (Float.SIZE / 8) + (Integer.SIZE / 8) + 10;
        this.recordsPerBlk = (int) Math.floor(blkSize/((Float.SIZE / 8) + (Integer.SIZE / 8) + 10));
        this.blk = new Block();
        this.blkList = new ArrayList<Block>();
    }

    public boolean allocateBlock() {
        if (numRemainingBlk < 0) {
            System.out.println("MEMORY FULL");
            return false;
        }

        blkList.add(blk);
        blk = new Block();
        numAllocatedBlk++;
        numRemainingBlk--;
        return true;
    }

    public void writeRecord(Record rec) {
        if (numRemainingBlk < 0) {
            System.out.println("No more space available!");
            return;
        }

        blk.getRecords().add(rec);
        recordCounter++;
        totalNumRecords++;


        if (recordCounter == recordsPerBlk) {
            allocateBlock();
        }
    }

    public boolean deleteRecord(Record rec) {
        if (totalNumRecords <= 0) {
            System.out.println("DATABASE IS EMPTY");
            return false;
        }

        totalNumRecords--;
        deletedRecords++;

        if (deletedRecords == recordsPerBlk) {
            numAllocatedBlk--;
            numRemainingBlk++;
            deletedRecords = 0;
        }
        return true;
    }

    public void printDatabaseInfo() {
        System.out.println("Total Memory Size: " + poolSize + " bytes");
        System.out.println("Total Size Used: " + totalNumRecords*recordSize + " bytes");
        System.out.println("Block Size: " + blkSize + " bytes");
        System.out.println("Record Size: " + recordSize + " bytes");
        System.out.println("Number of Blocks Allocated: " + numAllocatedBlk + " blocks");
        System.out.println("Number of Blocks Remaining: " + numRemainingBlk + " blocks");
        System.out.println("Total Number of Records: " + totalNumRecords);
    }
}