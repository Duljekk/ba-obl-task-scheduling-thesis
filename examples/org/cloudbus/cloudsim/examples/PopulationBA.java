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
}

// package org.cloudbus.cloudsim.examples;

// import java.util.ArrayList;
// import java.util.List;

// public class PopulationBA {
//     private List<Bat> bats; // List to store the bats
//     private int populationSize;
//     private int chromosomeLength;
//     private int dataCenterIterator;

//     // Constructor
//     public PopulationBA(int populationSize, int chromosomeLength, int dataCenterIterator) {
//         this.populationSize = populationSize;
//         this.chromosomeLength = chromosomeLength;
//         this.dataCenterIterator = dataCenterIterator;
//         this.bats = new ArrayList<>(populationSize);

//         // Initialize the population with bats
//         for (int i = 0; i < populationSize; i++) {
//             bats.add(new Bat(chromosomeLength, dataCenterIterator));
//         }
//     }

//     // Method to get a bat at a specific index
//     public Bat getBats(int index) {
//         if (index >= 0 && index < bats.size()) {
//             return bats.get(index);
//         } else {
//             throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for population size " + bats.size());
//         }
//     }

//     // Method to set a bat at a specific index
//     public void setBat(int index, Bat bat) {
//         if (index >= 0 && index < bats.size()) {
//             bats.set(index, bat);
//         } else {
//             throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for population size " + bats.size());
//         }
//     }

//     // Method to sort the population by fitness
//     public void sortByFitness() {
//         bats.sort((bat1, bat2) -> Double.compare(bat1.getFitness(), bat2.getFitness()));
//     }

//     // Method to get the population size
//     public int getPopulationSize() {
//         return populationSize;
//     }
// }