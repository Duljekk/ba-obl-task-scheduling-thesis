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

        // Initialize bats
        for (int i = 0; i < populationSize; i++) {
            bats.add(new Bat(i, chromosomeLength));
        }
    }

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