import java.io.File;
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
		while(!exit) {
			System.out.println("============================Select Byte Size============================");
			System.out.println("1: Select 200 Bytes");
			System.out.println("2: Select 500 Bytes");
			System.out.println("3: Quit");
			
			Scanner scan = new Scanner(System.in);
			int choice = scan.nextInt();
			
			if (choice == 1) {
				db = new Database(500000000, 200);
				exit = true;
			} else if (choice == 2) {
				db = new Database(500000000, 500);
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
					
				
				} else if (choice == 3) {
					System.out.println("\n[Starting experiment 3]\n");
					
					
				} else if (choice == 4) {
					System.out.println("\n[Starting experiment 4]\n");
					
					
				} else if (choice == 5) {
					System.out.println("\n[Starting experiment 5]\n");
					
					
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

}
