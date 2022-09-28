import java.io.File;
import java.util.Scanner;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String dir = System.getProperty("user.dir");
		String file = dir + "\\data\\data.tsv";
		
		File inputFile = new File(file);
		
		Database db = new Database(500000000, 200);
		
		try {
			//start loading data
			Scanner sc = new Scanner(inputFile);
			
			int recordCounter = 0;
			
			Block newBlock = null;
			
			sc.nextLine(); //skip the first line (the column line)
			while(sc.hasNextLine()) {
				String newLine = sc.nextLine();
				String[] record = newLine.split("\t");
				
				Record rec = new Record(record[0], Float.parseFloat(String.valueOf(record[1])), Integer.parseInt(String.valueOf(record[2])));
				
				db.writeRecord(rec);
				recordCounter += 1;
			}
			
			db.totalNumRecords = recordCounter;
			System.out.println(recordCounter);
			sc.close();
			// finish loading data
			
			System.out.println("Experiment 1");
			db.printDatabaseInfo();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
