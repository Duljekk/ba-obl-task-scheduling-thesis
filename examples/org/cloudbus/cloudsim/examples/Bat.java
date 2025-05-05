package org.cloudbus.cloudsim.examples;

public class Bat {
    private int id; // ID unik untuk setiap kelelawar
    private int[] chromosome; // Kromosom sebagai representasi solusi
    private double fitness; // Nilai fitness kelelawsar
    private double[] velocity; // Kecepatan kelelawar

    // Constructor
    public Bat(int id, int chromosomeLength) {
        this.id = id;
        this.chromosome = new int[chromosomeLength];
        this.velocity = new double[chromosomeLength];
        this.fitness = 0.0;
    }

    // Getter and setter methods
    public int getId() {
        return id;
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
        velocity[index] = value;
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