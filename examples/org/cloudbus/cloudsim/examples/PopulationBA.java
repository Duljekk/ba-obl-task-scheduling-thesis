package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PopulationBA {
    private List<Bat> bats; // List of bats in the population
    private int populationSize; // Size of the population
    private int chromosomeLength; // Length of the chromosome (solution representation)
    private int dataCenterIterator; // Identifier for the data center being processed

    // Constructor
    public PopulationBA(int populationSize, int chromosomeLength, int dataCenterIterator) {
        this.populationSize = populationSize;
        this.chromosomeLength = chromosomeLength;
        this.dataCenterIterator = dataCenterIterator;
        this.bats = new ArrayList<>();

        // Initialize the population with random bats
        initializePopulation();
    }

    // Initialize the population with random bats
    private void initializePopulation() {
        Random random = new Random();
        for (int i = 0; i < populationSize; i++) {
            int[] chromosome = new int[chromosomeLength];
            for (int j = 0; j < chromosomeLength; j++) {
                // Assign random values within the valid range for the data center
                int minPosition = (dataCenterIterator - 1) * 9;
                int maxPosition = (dataCenterIterator * 9) - 1;
                chromosome[j] = minPosition + random.nextInt(maxPosition - minPosition + 1);
            }
            bats.add(new Bat(i, chromosome));
        }
    }

    public Bat getBat(int index) {
        return bats.get(index);
    }

    public void setBat(int index, Bat bat) {
        bats.set(index, bat);
    }

    // Get the list of bats in the population
    public List<Bat> getBats() {
        return bats;
    }

    // Set the list of bats in the population
    public void setBats(List<Bat> bats) {
        this.bats = bats;
    }

    // Get the size of the population
    public int getPopulationSize() {
        return populationSize;
    }

    // Set the size of the population
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    // Get the length of the chromosome
    public int getChromosomeLength() {
        return chromosomeLength;
    }

    // Set the length of the chromosome
    public void setChromosomeLength(int chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
    }

    // Get the data center iterator
    public int getDataCenterIterator() {
        return dataCenterIterator;
    }

    // Set the data center iterator
    public void setDataCenterIterator(int dataCenterIterator) {
        this.dataCenterIterator = dataCenterIterator;
    }

    public void sortByFitness() {
        bats.sort((b1, b2) -> Double.compare(b2.getFitness(), b1.getFitness())); // Mengurutkan berdasarkan fitness
    }
}