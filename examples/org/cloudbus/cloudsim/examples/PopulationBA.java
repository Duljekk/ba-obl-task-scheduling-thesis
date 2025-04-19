package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PopulationBA {
    private List<Bat> bats; // Daftar kelelawar
    private int populationSize; // Jumlah kelelawar
    private int chromosomeLength; // Panjang kromosom
    private int dataCenterIterator; // Indeks datacenter yang sedang diproses

    // Constructor
    public PopulationBA(int populationSize, int chromosomeLength, int dataCenterIterator) {
        this.populationSize = populationSize;
        this.chromosomeLength = chromosomeLength;
        this.dataCenterIterator = dataCenterIterator;
        this.bats = new ArrayList<>(); // Mengiisiasi list daftar kelelawar

        // Memanggil fungsi untuk inisialisasi
        initializePopulation();
    }

    // // Initialize the population with random bats
    // private void initializePopulation() {
    //     Random random = new Random();
    //     for (int i = 0; i < populationSize; i++) {
    //         int[] chromosome = new int[chromosomeLength];
    //         for (int j = 0; j < chromosomeLength; j++) {
    //             // Assign random values within the valid range for the data center
    //             int minPosition = (dataCenterIterator - 1) * 9;
    //             int maxPosition = (dataCenterIterator * 9) - 1;
    //             chromosome[j] = minPosition + random.nextInt(maxPosition - minPosition + 1);
    //         }
    //         bats.add(new Bat(i, chromosome));
    //     }
    // }

    private void initializePopulation() {
        Random random = new Random();
        for (int i = 0; i < populationSize / 2; i++) {
            int[] chromosome = generateRandomChromosome(random);
            bats.add(new Bat(i, chromosome));
            
            int[] oppositeChromosome = generateOppositeChromosome(chromosome);
            bats.add(new Bat(i + populationSize / 2, oppositeChromosome));
        }
    }

    private int[] generateRandomChromosome(Random random) {
        int[] chromosome = new int[chromosomeLength];
        int minPosition = (dataCenterIterator - 1) * 9;
        int maxPosition = (dataCenterIterator * 9) - 1;
        for (int j = 0; j < chromosomeLength; j++) {
            chromosome[j] = minPosition + random.nextInt(maxPosition - minPosition + 1);
        }
        return chromosome;
    }

    private int[] generateOppositeChromosome(int[] chromosome) {
        int minPosition = (dataCenterIterator - 1) * 9;
        int maxPosition = (dataCenterIterator * 9) - 1;
        int[] oppositeChromosome = new int[chromosomeLength];
        for (int j = 0; j < chromosomeLength; j++) {
            oppositeChromosome[j] = maxPosition + minPosition - chromosome[j];
        }
        return oppositeChromosome;
    }
    
    public void applyOppositionBasedLearning() {
        for (int i = 0; i < bats.size(); i++) {
            Bat bat = bats.get(i);
            int[] oppositeChromosome = generateOppositeChromosome(bat.getChromosome());
            Bat oppositeBat = new Bat(i + bats.size(), oppositeChromosome);
            
            if (oppositeBat.getFitness() > bat.getFitness()) {
                bats.set(i, oppositeBat);
            }
        }
    }

    public void sortByFitness() {
        bats.sort((b1, b2) -> Double.compare(b2.getFitness(), b1.getFitness()));
    }

    public List<Bat> getBats() {
        return bats;
    }

    // // Method to generate the opposite population [OBL]
    // public PopulationBA generateOppositePopulation() {
    //     PopulationBA oppositePopulation = new PopulationBA(this.populationSize, this.chromosomeLength, this.dataCenterIterator);
    //     Random random = new Random();

    //     for (int i = 0; i < this.populationSize; i++) {
    //         Bat originalBat = this.getBat(i);
    //         int[] oppositeChromosome = new int[chromosomeLength];

    //         for (int j = 0; j < chromosomeLength; j++) {
    //             // Calculate the opposite value
    //             int minPosition = (dataCenterIterator - 1) * 9;
    //             int maxPosition = (dataCenterIterator * 9) - 1;
    //             int originalValue = originalBat.getGene(j);
    //             int oppositeValue = minPosition + maxPosition - originalValue; // OBL calculation

    //             // Clamp the opposite value to ensure it remains within bounds
    //             oppositeValue = Math.max(minPosition, Math.min(maxPosition, oppositeValue));
    //             oppositeChromosome[j] = oppositeValue;
    //         }

    //         oppositePopulation.setBat(i, new Bat(i, oppositeChromosome));
    //     }

    //     return oppositePopulation;
    // }

        // // Method to combine original and opposite populations and select the fittest [OBL]
        // public void combineAndSelectFittest() {
        //     PopulationBA oppositePopulation = generateOppositePopulation();
        //     List<Bat> combinedBats = new ArrayList<>(this.bats);
        //     combinedBats.addAll(oppositePopulation.getBats());
    
        //     // Sort combined population by fitness
        //     combinedBats.sort((b1, b2) -> Double.compare(b2.getFitness(), b1.getFitness()));
    
        //     // Select the fittest individuals to form the new population
        //     this.bats = new ArrayList<>(combinedBats.subList(0, this.populationSize));
        // }

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