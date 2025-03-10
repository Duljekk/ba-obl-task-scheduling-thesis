package org.cloudbus.cloudsim.examples;

public class Bat {
    private int id; // Unique identifier for the bat
    private int[] chromosome; // Chromosome representing the solution
    private double fitness; // Fitness value of the bat
    private double[] velocity; // Velocity of the bat

    // Constructor
    public Bat(int id, int[] chromosome) {
        this.id = id;
        this.chromosome = chromosome;
        this.fitness = 0.0;
        this.velocity = new double[chromosome.length];
    }

    // Constructor with chromosome length
    public Bat(int chromosomeLength) {
        this.id = -1; // Default ID if not provided
        this.chromosome = new int[chromosomeLength];
        this.fitness = 0.0;
        this.velocity = new double[chromosomeLength];
    }

    // Getter and setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getChromosome() {
        return chromosome;
    }

    public void setChromosome(int[] chromosome) {
        this.chromosome = chromosome;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double[] getVelocity() {
        return velocity;
    }

    // Set the velocity for a specific index
    public void setVelocity(int index, double value) {
        if (index >= 0 && index < velocity.length) {
            velocity[index] = value;
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds for velocity array.");
        }
    }

    // Get a specific gene from the chromosome
    public int getGene(int index) {
        return chromosome[index];
    }

    // Set a specific gene in the chromosome
    public void setGene(int index, int value) {
        chromosome[index] = value;
    }

    // Get the length of the chromosome
    public int getChromosomeLength() {
        return chromosome.length;
    }
}