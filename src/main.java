import java.io.File;
import java.util.Scanner;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String dir = System.getProperty("user.dir");
		String file = dir + "\\data\\data.tsv";
		// String file = dir + "/data/data.tsv";
		
		File inputFile = new File(file);
		
		Database db = new Database(500000000, 200);
		
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
			
			boolean exit = false;
			while(!exit) {
				System.out.println("=========================Experiments======================");
				System.out.println("1: Experiment 1");
				System.out.println("2: Experiment 2");
				System.out.println("3: Experiment 3");
				System.out.println("4: Experiment 4");
				System.out.println("5: Experiment 5");
				System.out.println("6: Quit");
				
				Scanner scan = new Scanner(System.in);
				int choice = scan.nextInt();
				
				if (choice == 1) {
					System.out.println("[Starting experiment 1]");
					
					db.printDatabaseInfo();
				
				} else if (choice == 2) {
					System.out.println("[Starting experiment 2]");
					
				
				} else if (choice == 3) {
					System.out.println("[Starting experiment 3]");
					
					
				} else if (choice == 4) {
					System.out.println("[Starting experiment 4]");
					
					
				} else if (choice == 5) {
					System.out.println("[Starting experiment 5]");
					
					
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
