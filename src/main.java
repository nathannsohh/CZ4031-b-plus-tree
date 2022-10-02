import java.io.File;
import java.util.List;
import java.util.Scanner;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String dir = System.getProperty("user.dir");
		// String file = dir + "\\data\\data.tsv";
		String file = dir + "/data/data.tsv";
		
		File inputFile = new File(file);
		
		boolean exit = false;
		Database db = null;
		BPTree tree = null;
		while(!exit) {
			System.out.println("============================Select Byte Size============================");
			System.out.println("1: Select 200 Bytes");
			System.out.println("2: Select 500 Bytes");
			System.out.println("3: Quit");
			
			Scanner scan = new Scanner(System.in);
			int choice = scan.nextInt();
			
			if (choice == 1) {
				db = new Database(500000000, 200);
				tree = new BPTree((200 - 8)/(8 + 4));
				exit = true;
			} else if (choice == 2) {
				db = new Database(500000000, 500);
				tree = new BPTree((500 - 8)/(8 + 4));
				exit = true;
			} else {
				System.out.println("Invalid input");
			}
		}
		
		try {
			//start loading data
			Scanner sc = new Scanner(inputFile);
			
			sc.nextLine(); //skip the first line (the column line)
			while(sc.hasNextLine()) {
				String newLine = sc.nextLine();
				String[] record = newLine.split("\t");
				
				Record rec = new Record(record[0], Float.parseFloat(String.valueOf(record[1])), Integer.parseInt(String.valueOf(record[2])));
				
				db.writeRecord(rec);
				RecordBlock rb = new RecordBlock(rec, db.getBlock()); // need the block
				int key = rec.getNumVotes();
				tree.insertKey(key, rb);
			}
			
			sc.close();
			// finish loading data
			
			exit = false;
			while(!exit) {
				System.out.println("\n=========================Experiments======================");
				System.out.println("1: Experiment 1");
				System.out.println("2: Experiment 2");
				System.out.println("3: Experiment 3");
				System.out.println("4: Experiment 4");
				System.out.println("5: Experiment 5");
				System.out.println("6: Quit");
				
				Scanner scan = new Scanner(System.in);
				int choice = scan.nextInt();
				
				if (choice == 1) {
					System.out.println("\n[Starting experiment 1]\n");
					
					db.printDatabaseInfo();
				
				} else if (choice == 2) {
					System.out.println("\n[Starting experiment 2]\n");
					
					tree.printExp2();

				} else if (choice == 3) {
					System.out.println("\n[Starting experiment 3]\n");
					
					List<RecordBlock> results = tree.searchRecords(500, 500);

					tree.printNodesAccessed();

					System.out.println();

					printSearchStatistics(results);
					
				} else if (choice == 4) {
					System.out.println("\n[Starting experiment 4]\n");
					
					List<RecordBlock> results = tree.searchRecords(30000, 40000);

					tree.printNodesAccessed();

					System.out.println();

					printSearchStatistics(results);
					
				} else if (choice == 5) {
					System.out.println("\n[Starting experiment 5]\n");
					tree.deleteKey(1000);
					tree.printExp5();
					
				} else if (choice == 6) {
					System.out.println("Quiting...");
					scan.close();
					exit = true;
					
				} else {
					System.out.println("Input invalid!");
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void printSearchStatistics(List<RecordBlock> results) {
		int count = 0;
        int index = 0;
		float totalRating = 0;

		System.out.printf("Number of data blocks accessed: %d\n\n", results.size());

		for (RecordBlock rb: results) {
			Block block = rb.getBlock();
			List<Record> RecordList = block.getRecords();

            System.out.printf("Data block content: ");

            for (index = 0; index < RecordList.size() - 1; index++) {
				Record record = RecordList.get(index);
                System.out.printf("%s, ", record.getTconst());
            }
			Record record = RecordList.get(index);
            System.out.printf("%s", record.getTconst());

            System.out.println();
			count++;
            if (count == 5) {
                break;
            }
        }

		System.out.println();

		for (RecordBlock rb: results) {
			Record record = rb.getRecord();

			totalRating += record.getAverageRating();
		}

		float avgRating = totalRating / results.size();

		System.out.printf("Average of 'averageRating's: %f\n\n", avgRating);
	}

}
