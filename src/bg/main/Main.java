package bg.main;

import java.io.IOException;

import bg.dataset.ReadExcel;
import bg.geneticalgorithm.GeneticAlgorithm;

/**
 * This is the class to start the search of the best combination
 * in the colonel blotto game consisting of 78 data through
 * genetic algorithm.
 *   
 * @author Qiyu Chen
 *
 */
public class Main {

	/**
	 * To test/start the algorithm
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ReadExcel readExcel = new ReadExcel(); // read in the data from an excel file
		GeneticAlgorithm ga = new GeneticAlgorithm(1500); // initialize ga with a population size 

		// Test the score of individual arrangement of resources
		int[] arr = {2, 1, 16, 17, 20, 19, 17, 2, 3, 3};
//		int[] arr = {16, 1, 21, 16, 17, 17, 3, 3, 3, 3};
//		int[] arr = {11, 4, 19, 11, 16, 5, 11, 16, 5, 2};
		
		readExcel.compareAndPrint(arr);
		
		
		// Use the genetic algorithm once to search for the best
//		ga.simulate(400); // simulate the population for a given number of the evolutions
		
		
		// Use the genetic algorithm for multiple times to search for the best
		ga.multiSimulate(400, 1000); // first arg: # of evolution; second arg: # of simulations 
		
		
		// Use the brute force method to exhaust every possibility to find the best 
//		readExcel.findBest();
	}

}
