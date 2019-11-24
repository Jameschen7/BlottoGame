package bg.geneticalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The class applies genetic algorithm to find the best arrangement of 100
 * soldiers for 10 castles for the Colonel Blotto challenge against the
 * imported 78 students' arrangement. 
 * Due to the limit that the sum of solders must be 100, the genetic algorithm
 * is revised such that the process of crossover will just be interchanges
 * of soldiers in a single parent arrangement and then mutate. 
 * 
 * @author Qiyu chen
 */
public class GeneticAlgorithm {
	private final double CROSSOVER_POP_RATE = 0.95; // percent of chromosomes for crossover
	private final double CROSSOVEE_MIN_CHANGE = 1; // percent of average to allow be a parent
	private final double MUTATION_RATE = 0.3; // percent of mutation
	private final int MUTATION_NUM =40; // # of max genes mutating for mutation
	private final int MUTATION_MIN_NUM =5; // # of min genes mutating for special case mutation
	
	private int generation; // # of generations
	private int size; 	// population size
	private List<Chromosome> population; // the population
	
	private double total; // the total score for this generation used for getParent() 
	private Chromosome best; // the best chromosome for this generation 
	private Chromosome bestEver; // the best chromosome in all generations
	
	/**
	 * The constructor to set up the fields and the initial population
	 * @param size
	 */
	public GeneticAlgorithm(int size) {
		if (size <=0) {
			System.err.println("invalid initial size");
			return;
		}
		
		generation = 0;
		this.size = size;
		randomInitialization();
		best = Chromosome.clone(population.get(0));
		bestEver = Chromosome.clone(population.get(0));
		findBestAndTotal();
	}
	
	/**
	 * generate a random population
	 */
	private void randomInitialization() {
		population = new ArrayList<Chromosome>();
		
		for (int i = 0; i<size; i++) {
			population.add(new Chromosome());
		}
	}

	/**
	 * find the total score and the chromosome with the highest adaptability of 
	 * this generation and if it is better than the best chromosome before, 
	 * replace the bestever.
	 */
	private void findBestAndTotal() {
		total = 0; // clear total
		
		// find the best and toal
		Chromosome newBest = population.get(0);
		for (Chromosome chr: population) {
			total += chr.getAdaptability();
			if (newBest.getAdaptability() < chr.getAdaptability()) {
				newBest = chr;
			}
		}
		this.best = Chromosome.clone(newBest);
		
		// check if it is the best ever
		if (best.getAdaptability() > bestEver.getAdaptability()) {
//			System.out.println("best: " +best.getAdaptability()+
//					" best ever: " + bestEver.getAdaptability());
			this.bestEver = Chromosome.clone(best);
		}
	}

	/**
	 * print information of this generation 
	 */
	public void printInfo() {
		System.out.println("~~~~~~~~~~~~~~~~~~~~");
		System.out.println("\tGeneration: " + generation);
		System.out.println("\tBest generation: " + Arrays.toString(best.getGenes()) 
				+ " total: " + best.getGenesSum());
		System.out.println("\tBest score: " + best.getAdaptability());
		System.out.println("\tAverage score: " + (total/size));
		
		System.out.println("\tBest generation so far: " + Arrays.toString(bestEver.getGenes())
				+ " total: " + best.getGenesSum());
		System.out.println("\tBest score so far: " + bestEver.getAdaptability());
		System.out.println("~~~~~~~~~~~~~~~~~~~~");
	}
	
	/**
	 * Make the population evolve for one generation by crossover of parents, 
	 * mutation, and then reproduction 
	 */
	public void evolve() {
		Chromosome parent, child; 
		List<Chromosome> newPopulation = new ArrayList<Chromosome>();
		
		// Crossover
		for (int i = 0; i < (int)(size*CROSSOVER_POP_RATE); i++) {
			parent = getParent();
			child = parent.crossover();
			newPopulation.add(child);
		}
		
		// Mutation
		mutation(newPopulation);
		
		// Reproduction 
		reproduction(newPopulation);
		
		// reset the population and relevant info
		population = newPopulation;
		findBestAndTotal();
	}
	
	/**
	 * select a parent from this generation through the Roulette Wheel Selection
	 * and eliminate the bad ones
	 * @return the selected parent
	 */
	private Chromosome getParent() {
		double target = Math.random() * total; // The target cumulative adaptability
		double sum = 0; // the cumulative adaptability
		
		for (Chromosome chr: population) {
			sum += chr.getAdaptability();
			if (sum >= target && sum>= (total/size)*CROSSOVEE_MIN_CHANGE) {
				return chr;
			}
		}
		
		return null;
	}
	
	/**
	 * Make a list of chromosomes to mutate by MUTATION_NUM with MUTATION_RATE
	 * @param pop - the list of chromosomes to mutate
	 */
	private void mutation(List<Chromosome> pop) {
		for (Chromosome chr: pop) {
			if (Math.random() < MUTATION_RATE) {
				int mutationSize = (int)(Math.random()*(MUTATION_NUM+1));
				chr.mutation(mutationSize);
			}
		}
	}
	
	/**
	 * reproduce the top chromosomes from the current population into the new
	 * population 
	 * @param pop
	 * @param newPop
	 */
	private void reproduction(List<Chromosome> newPop) {
		if (CROSSOVER_POP_RATE == 1) {
			return;
		}
		
		// sort population
		Collections.sort(population, new Comparator<Chromosome>(){

			public int compare(Chromosome o1, Chromosome o2) {
				if (o1.getAdaptability() > o2.getAdaptability()) {
					return -1;
				} else if (o1.getAdaptability() < o2.getAdaptability()) {
					return 1;
				} else {
					return 0;
				}
			}
			
		});
		
		// reproduce the top chromosomes
		int initSize = newPop.size();
		for(int i = 1; i + initSize <= size; i++) {
			if (! newPop.contains(population.get(i-1))) {
				newPop.add(Chromosome.clone(population.get(i-1)));
			} else {
				Chromosome mutatedBest = Chromosome.clone(population.get(i-1));
				mutatedBest.mutation(MUTATION_MIN_NUM);
				newPop.add(mutatedBest);
				
			}
//			System.out.println("i: " + i + " score: " + population.get(i-1).getAdaptability()
//					+ " :" + Arrays.toString(population.get(i-1).getGenes()));
		}
	}
	
	/**
	 * the method to start the simulation of natural evolution of genetic algorithm
	 * @param gen - number of generations for the population to simulate
	 */
	public void simulate(int gen) {
		while (generation < gen) {
			evolve();
			generation++;
			printInfo();
		}
	}
	
	/**
	 * the method to repeat the simulation of natural evolution for a given number
	 * of times, and only show the highest score and its combination
	 * @param gen - number of generations for the population in each simulation
	 * @param repeat - the number of times to simulate 
	 */
	public void multiSimulate(int gen, int repeat) {
		Chromosome bestOfAll = new Chromosome(); // store the best chromosome 
		
		for (int i = 0; i<repeat; i++) {
			while (generation < gen) {
				evolve();
				generation++;
			}
			
			if (bestOfAll.getAdaptability() <= this.bestEver.getAdaptability()) {
				bestOfAll = Chromosome.clone(bestEver);
				
				System.out.println("new best: " + bestOfAll.getAdaptability() 
						+ " : " + Arrays.toString(bestOfAll.getGenes()));
			}
			
			if (i % 25 == 0) {
				System.out.println("---------------------------------------------------------------------------------------------");
				System.out.println("simulation: " + i
						+ " best so far: " + bestOfAll.getAdaptability() 
						+ " : " + Arrays.toString(bestOfAll.getGenes()));
				System.out.println("---------------------------------------------------------------------------------------------");
			}
			
			generation = 0;
			randomInitialization();
			best = Chromosome.clone(population.get(0));
			bestEver = Chromosome.clone(population.get(0));
			findBestAndTotal();
		}
		
		System.out.println("---------------------------------------------------------------------------------------------");
		System.out.println("Result for " + repeat + " simulations of "+ gen + "generations:");
		System.out.println("best score: " + bestOfAll.getAdaptability());
		System.out.println("arrangement: " + Arrays.toString(bestOfAll.getGenes()));
		System.out.println("---------------------------------------------------------------------------------------------");
	}
	
}
