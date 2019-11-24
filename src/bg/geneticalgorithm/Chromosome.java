package bg.geneticalgorithm;

import java.io.IOException;
import java.util.Arrays;

import bg.dataset.ReadExcel;

/**
 * The chromosome for the genetic algorithm that represent the arrangement of
 * soldiers and castles in the Colonel Blotto competition
 * 
 * @author Qiyu Chen
 *
 */
public class Chromosome {
	private static final int MAX_GENES = 100; // a total of 100 soldiers
	private static final int GENE_SIZE = 10; // # of castles
	private static final int MIN_CROSS_SIZE = 2; // # of min crosses for crossover
	
	private static final int[][] SCORES = ReadExcel.getArray();
	
	private int[] genes = new int[GENE_SIZE]; // store the arrangement as int array
	private double adaptability; // store the score of this arrangement
	
	/**
	 * default constructor: create a random arrangement
	 */
	public Chromosome() {
		int random;
		
		for (int i = 0; i < MAX_GENES; i ++) {
			random = (int)(Math.random()*GENE_SIZE);
			genes[random] ++;
		}
		
		quickCalcAdap();
	}
	
	/**
	 * Initialize a Chromosome/arrangement based on an input array and 
	 * calculate its adaptability
	 * @param genes
	 */
	public Chromosome(int[] genes) {
		// clone genes
		for (int i = 0; i < genes.length; i++) {
			this.genes[i] = genes[i];
		}
		
		quickCalcAdap();
	}
	
	/**
	 * calculate the adaptability of this chromosome 
	 */
	public void calcAdap() {
		try {
			this.adaptability = ReadExcel.compare(genes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * a quicker way to calculate the adaptability of this chromosome 
	 */
	public void quickCalcAdap() {
		this.adaptability = ReadExcel.compare(genes, SCORES);
	}
	
	/**
	 * return a copied chromosome of the input chromosome
	 * @param chr - chromosome to be copied
	 * @return a copied chromosome
	 */
	public static Chromosome clone(Chromosome chr) {
//		// check validity
//		if (chr == null || chr.getGenes() == null) {
//			System.err.println("Chromosome.clone: invalid chromosome to be copied");
//			return null;
//		}
//		
		// create a copy of the input chromosome
		Chromosome copy = new Chromosome(chr.getGenes());
		
		return copy;
	}
	
	/**
	 * randomly choose num soldiers to be reassigned a random castle
	 * @param num - number of soldiers to change position
	 */
	public void mutation(int num) {
		// check validity
		if (num <= 0) {
			return;
		}
		
		int pos; // the index of gene to mutate
		for (int i = 0; i < num; i ++) {
			pos = (int)(Math.random() * GENE_SIZE); // randomly locate a castle
			
			// skip if the soldier is 0 at this castle
			if (genes[pos] <= 0) {
				continue;
			} else {
				genes[pos]--;
				pos = (int)(Math.random() * GENE_SIZE);
				genes[pos]++;
			}
		}
		
		quickCalcAdap();
	}
	
	/**
	 * One chromosome reproduces a new chromosome by randomly interchanging
	 * genes/soldiers at two index/castles by a random number of times 
	 * @return a child chromosome for the next generation
	 */
	public Chromosome crossover() {
		Chromosome child = clone(this); // copy the parent first
		
		int time = Math.abs((int)(Math.random()*GENE_SIZE) - (int)(Math.random()*GENE_SIZE))
				+ MIN_CROSS_SIZE; // a random time of interchanges that is at least MIN_CROSS_SIZE
		int pos1; // the index of the genes to be interchanged
		int pos2; // the index of the genes to be interchanged
		int temp; // store the temp gene
		
		for (int i = 0; i < time; i++) {
			pos1 = (int)(Math.random()*GENE_SIZE);
			pos2 = (int)(Math.random()*GENE_SIZE);
			temp = child.genes[pos1];
			child.genes[pos1] = child.genes[pos2];
			child.genes[pos2] = temp;
		}
		
		child.quickCalcAdap();
		
		return child;
	}
	
	/**
	 * return the sum of genes
	 * @return
	 */
	public int getGenesSum() {
		return Arrays.stream(genes).sum();
	}
	
	public int[] getGenes() {
		return genes;
	}
	
	public double getAdaptability() {
		return adaptability;
	}

	public static void main(String[] args) {
		Chromosome a = new Chromosome();
		System.out.println(Arrays.toString(a.getGenes()));
		System.out.println(a.adaptability);
//		a.mutation(3);
//		System.out.println(Arrays.toString(a.getGenes()));
//		Chromosome b = a.crossover();
//		System.out.println(Arrays.toString(b.getGenes()));
//		b.mutation(3);
//		System.out.println(Arrays.toString(b.getGenes()));
		Chromosome b = clone(a);
		System.out.println(Arrays.toString(b.getGenes()));
		System.out.println(b.adaptability);
		a.genes[0] = 100;
		System.out.println(Arrays.toString(a.getGenes()));
		System.out.println(a.adaptability);
		System.out.println(Arrays.toString(b.getGenes()));
		System.out.println(b.adaptability);
		
		
		
//		for (int i = 0; i< 10; i++) {
//			
////			System.out.println((int)(Math.random()*GENE_SIZE) - (int)(Math.random()*GENE_SIZE));
//			System.out.println((int)(Math.random()*GENE_SIZE)+" "+(int)(Math.random()*GENE_SIZE));
//		}
		
//		a.genes = null;
//		Chromosome b = clone(a);
	}
}
