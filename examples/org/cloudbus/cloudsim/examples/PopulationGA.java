package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a population of chromosomes in the Genetic Algorithm.
 * This class manages the collection of chromosomes and provides methods
 * for population operations like selection, crossover, and mutation.
 */
public class PopulationGA {
    private List<Chromosome> chromosomes; // List of chromosomes in the population
    private int populationSize; // Size of the population
    private int chromosomeLength; // Length of each chromosome
    private int dataCenterIterator; // Index of the datacenter being processed

    /**
     * Constructor
     * @param populationSize The size of the population
     * @param chromosomeLength The length of each chromosome
     * @param dataCenterIterator The index of the datacenter being processed
     */
    public PopulationGA(int populationSize, int chromosomeLength, int dataCenterIterator) {
        this.populationSize = populationSize;
        this.chromosomeLength = chromosomeLength;
        this.dataCenterIterator = dataCenterIterator;
        this.chromosomes = new ArrayList<>(); // Initialize the list of chromosomes

        // Initialize the population
        initializePopulation();
    }

    /**
     * Initialize the population with random chromosomes
     */
    private void initializePopulation() {
        Random random = new Random();
        for (int i = 0; i < populationSize; i++) {
            int[] genes = generateRandomChromosome(random);
            chromosomes.add(new Chromosome(i, genes));
            
            // Create an opposite chromosome for diversity
            int[] oppositeGenes = generateOppositeChromosome(genes);
            chromosomes.add(new Chromosome(i + populationSize / 2, oppositeGenes));
        }
    }

    /**
     * Generate a random chromosome
     * @param random Random number generator
     * @return A randomly generated chromosome
     */
    private int[] generateRandomChromosome(Random random) {
        int[] genes = new int[chromosomeLength];
        int minPosition = (dataCenterIterator - 1) * 9;
        int maxPosition = (dataCenterIterator * 9) - 1;
        for (int j = 0; j < chromosomeLength; j++) {
            genes[j] = minPosition + random.nextInt(maxPosition - minPosition + 1);
        }
        return genes;
    }

    /**
     * Generate an opposite chromosome (for diversity)
     * @param genes The original chromosome genes
     * @return An opposite chromosome
     */
    private int[] generateOppositeChromosome(int[] genes) {
        int minPosition = (dataCenterIterator - 1) * 9;
        int maxPosition = (dataCenterIterator * 9) - 1;
        int[] oppositeGenes = new int[chromosomeLength];
        for (int j = 0; j < chromosomeLength; j++) {
            oppositeGenes[j] = maxPosition + minPosition - genes[j];
        }
        return oppositeGenes;
    }

    /**
     * Apply opposition-based learning to improve diversity
     */
    public void applyOppositionBasedLearning() {
        for (int i = 0; i < chromosomes.size(); i++) {
            Chromosome chromosome = chromosomes.get(i);
            int[] oppositeGenes = generateOppositeChromosome(chromosome.getGenes());
            Chromosome oppositeChromosome = new Chromosome(i + chromosomes.size(), oppositeGenes);
            
            if (oppositeChromosome.getFitness() > chromosome.getFitness()) {
                chromosomes.set(i, oppositeChromosome);
            }
        }
    }

    /**
     * Sort chromosomes by fitness (descending order)
     */
    public void sortByFitness() {
        chromosomes.sort((c1, c2) -> Double.compare(c2.getFitness(), c1.getFitness()));
    }

    /**
     * Get the list of chromosomes
     * @return The list of chromosomes
     */
    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    /**
     * Set the list of chromosomes
     * @param chromosomes The new list of chromosomes
     */
    public void setChromosomes(List<Chromosome> chromosomes) {
        this.chromosomes = chromosomes;
    }

    /**
     * Get a chromosome at a specific index
     * @param index The index of the chromosome
     * @return The chromosome at the specified index
     */
    public Chromosome getChromosome(int index) {
        return chromosomes.get(index);
    }

    /**
     * Set a chromosome at a specific index
     * @param index The index of the chromosome
     * @param chromosome The new chromosome
     */
    public void setChromosome(int index, Chromosome chromosome) {
        chromosomes.set(index, chromosome);
    }

    /**
     * Get the size of the population
     * @return The population size
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Set the size of the population
     * @param populationSize The new population size
     */
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    /**
     * Get the length of each chromosome
     * @return The chromosome length
     */
    public int getChromosomeLength() {
        return chromosomeLength;
    }

    /**
     * Set the length of each chromosome
     * @param chromosomeLength The new chromosome length
     */
    public void setChromosomeLength(int chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
    }

    /**
     * Get the datacenter iterator
     * @return The datacenter iterator
     */
    public int getDataCenterIterator() {
        return dataCenterIterator;
    }

    /**
     * Set the datacenter iterator
     * @param dataCenterIterator The new datacenter iterator
     */
    public void setDataCenterIterator(int dataCenterIterator) {
        this.dataCenterIterator = dataCenterIterator;
    }
} 