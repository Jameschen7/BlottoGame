package bg.dataset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * a class that read in an excel file of Colonel Blotto scores
 *
 */
public class ReadExcel {
	private static String excelName = "./src/bg/dataset/blottoscores.xls";
	
	private final static int START_ROW_INDEX = 1;
	private final static int END_ROW_INDEX = 78;
	private final static int START_COL_INDEX = 6;
	private final static int END_COL_INDEX = 15;
	
	
	public ReadExcel() {
	}
	
	public ReadExcel(String excelName) {
		ReadExcel.excelName = excelName;
	}
	
	/**
	 *  read into an excel file and print the score
	 * @throws IOException
	 */
	public void read() throws IOException {
		File file = new File(excelName);
		Workbook wb;
		
		try {
			wb = Workbook.getWorkbook(file);
			Sheet sheet = wb.getSheet(0); // get sheet 0
			
			// read in the score in each cell and print
			for (int row = START_ROW_INDEX; row <= END_ROW_INDEX; row++) {
				System.out.println("|" +row + "|");

				for (int col = START_COL_INDEX; col <= END_COL_INDEX; col++) {
					Cell cell = sheet.getCell(col, row);
					if (cell.getType() == CellType.NUMBER) {
						System.out.print(cell.getContents() + " ");
					} else {
						System.err.println("the input cell is not numeric");
					}
				}
				System.out.println("||");
			}
			
		} catch (BiffException e){
			e.printStackTrace();
		} catch (IOException e) {
			throw new IOException("cannot get workbook");
		}
	}
	
	// compare the arr to the scores in the excel file
	public void compareAndPrint(int[] arr) throws IOException {
		System.out.println("scores: " + compare(arr));
	}
	
	// compare the arr to the scores in the excel file
	public static double compare(int[] arr) throws IOException {
		int win=0, tie=0, countwin, countlose;

		File file = new File(excelName);
		Workbook wb;

		try {
			wb = Workbook.getWorkbook(file);
			Sheet sheet = wb.getSheet(0); // get sheet 0

			// read in the score in each cell and print
			for (int row = START_ROW_INDEX; row <= END_ROW_INDEX; row++) {
				countwin = 0; // clear
				countlose = 0; // clear

				for (int col = START_COL_INDEX; col <= END_COL_INDEX; col++) {
					Cell cell = sheet.getCell(col, row);
					if (Integer.parseInt(cell.getContents()) < arr[col-START_COL_INDEX]){
						countwin++;
					} else if (Integer.parseInt(cell.getContents()) > arr[col-START_COL_INDEX]) {
						countlose++;
					}
				}

				if (countwin > countlose) {
					win++;
				} else if (countwin == countlose) {
					tie ++;
				} 
			}

		} catch (BiffException e){
			e.printStackTrace();
		} catch (IOException e) {
			throw new IOException("cannot get workbook");
		}

		return win*1+(double)tie/2;
	}
	
	/**
	 * compare this arrangement with the data arrangement arrays
	 * @param arr
	 * @param dataset - the data arrangement to be compared with
	 * @return
	 */
	public static double compare(int[] arr, int[][] dataset) {
		int win=0, tie=0, countwin, countlose; // temp variables

		// compare the input array with each arrangement
		for (int row = 0; row < dataset.length; row++) {
			countwin = 0; // clear
			countlose = 0; // clear

			for (int col = 0; col < dataset[0].length; col++) {
				if (dataset[row][col] < arr[col]){
					countwin++;
				} else if (dataset[row][col] > arr[col]) {
					countlose++;
				}
			}

			if (countwin > countlose) {
				win++;
			} else if (countwin == countlose) {
				tie ++;
			} 
		}
		
		return win*1+(double)tie/2;
	}
	
	/**
	 * load the arrangements as int array
	 * @return
	 */
	public static int[][] getArray(){
		int[][] arrangementArr = new int[END_ROW_INDEX-START_ROW_INDEX+1][END_COL_INDEX-START_COL_INDEX+1];
		File file = new File(excelName);
		Workbook wb;
		Cell cell;
		
		try {
			wb = Workbook.getWorkbook(file);
			Sheet sheet = wb.getSheet(0); // get sheet 0
			
			// read in the score in each cell and print
			for (int row = START_ROW_INDEX; row <= END_ROW_INDEX; row++) {
				for (int col = START_COL_INDEX; col <= END_COL_INDEX; col++) {
					cell = sheet.getCell(col, row);
					
					arrangementArr[row-START_ROW_INDEX][col-START_COL_INDEX]=
							Integer.parseInt(cell.getContents());
				}
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return arrangementArr;
	}
	
	/**
	 * the brute force way to get the global maximum score
	 * @throws IOException
	 */
	public void findBest() throws IOException {
		int[][] arrangementArr = getArray(); // get data
		
		double best = 0, score;
		int[] arr = new int[10];
		int[] bestArr = new int[10];
		int total = 100, j;
		long iteration = 0;
		
		for (int a = 0; a<=22; a++) {
			for (int b = 0;b<=22; b++) {
				for (int c = 0; c<=22; c++) {
					for (int d = 0; d<=22; d++) {
						for (int e = 0; (e<=22) && (e+d+c+b+a <= total); e++) {
							for (int f = 0; (f<=22) && (f+e+d+c+b+a <= total); f++) {
								for (int g = 0; (g<=22) && (g+f+e+d+c+b+a <= total); g++) {
									for (int h = 0; (h<=22) && (h+g+f+e+d+c+b+a <= total); h++) {
										for (int i = 0; (i<=22) && (i+h+g+f+e+d+c+b+a )<= total; i++) {
											j = total-a-b-c-d-e-f-g-h-i;
											if (j > 22) {
												continue;
											}
											
											arr = new int[] {a,b,c,d,e,f,g,h,i,j};
											score = compare(arr, arrangementArr);
											
											if (score > best) {
												best = score;
												bestArr = arr;
												System.out.println("new best score:" + score);
												System.out.println(" :" + Arrays.toString(bestArr));
											}
											
											iteration++;
											if (iteration % 10000000 == 0) { // print current iteration for every 5,000,000 trials
												System.out.print("--iteration: " + iteration );
												System.out.print(", current:" + score);
												System.out.print(" :" + Arrays.toString(arr));
												System.out.print(", best:" + best);
												System.out.println(" :" + Arrays.toString(bestArr));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

}
