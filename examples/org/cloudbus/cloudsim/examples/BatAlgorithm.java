package org.cloudbus.cloudsim.examples;

import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class BatAlgorithm {
    private int maxIterations; // Jumlah iterasi maksimal
    private int populationSize; // Ukuran populasi kelelawar (jumlah kelelawar)
    private double[] frequency; // Frekuensi pulsa ultrasonik untuk setiap kelelawar
    private double[] loudness; // Intensitas suara untuk setiap kelelawar
    private double[] pulseRate; //  Jumlah pulsa suara untuk setiap kelelawar          
    private double alpha; // Koefisien untuk memperbarui intensitas suara
    private double gamma; // Koefisien untuk memperbarui pulsa ultrasonik

    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;

    private int numberOfDataCenters = 6;
    private double[] globalBestFitnesses; // Nilai fitness terbaik pada setiap iterasi
    private int[][] globalBestPositions; // Posisi terbaik kelelawar dalam bentuk matriks

    public BatAlgorithm(int maxIterations, int populationSize, double alpha, double gamma,
                        List<Cloudlet> cloudletList, List<Vm> vmList, int chromosomeLength) {
        this.maxIterations = maxIterations;
        this.populationSize = populationSize;
        this.alpha = alpha;
        this.gamma = gamma;
        this.cloudletList = cloudletList; // Daftar tugas (Cloudlets)
        this.vmList = vmList; // Daftar virtual machine (VM)

        frequency = new double[populationSize];
        loudness = new double[populationSize];
        pulseRate = new double[populationSize];

        globalBestFitnesses = new double[numberOfDataCenters];
        globalBestPositions = new int[numberOfDataCenters][];

        for (int i = 0; i < numberOfDataCenters; i++) {
            // Mengatur nilai fitness awal sebagai nilai terendah
            globalBestFitnesses[i] = Double.NEGATIVE_INFINITY; 
            // Array untuk menyimpan posisi terbaik setiap data center
            globalBestPositions[i] = new int[chromosomeLength]; 
        }
    }

    // public void applyOBL(PopulationBA population, int dataCenterIterator, int iteration, int maxIterations) {
    //     int dcIndex = dataCenterIterator - 1;
    //     int[] eliteSolution = globalBestPositions[dcIndex];
    //     int minVM = (dataCenterIterator - 1) * 9;
    //     int maxVM = dataCenterIterator * 9 - 1;

    //     Random random = new Random();

    //     for (Bat bat : population.getBats()) {
    //         int[] dynamicOppositeSolution = new int[eliteSolution.length];
    //         int[] quasiOppositeSolution = new int[eliteSolution.length];

    //         // Calculate dynamic and quasi-opposite solutions
    //         for (int i = 0; i < eliteSolution.length; i++) {
    //             int gene = bat.getGene(i);

    //             // Dynamic opposition
    //             double dynamicFactor = random.nextDouble() * (maxVM - minVM) * (1 - (double) iteration / maxIterations);
    //             dynamicOppositeSolution[i] = (int) (minVM + maxVM - gene + dynamicFactor);
    //             dynamicOppositeSolution[i] = clampVM(dynamicOppositeSolution[i], dataCenterIterator);

    //             // Quasi-opposition
    //             double quasiFactor = random.nextDouble() * ((minVM + maxVM) / 2.0 - gene);
    //             quasiOppositeSolution[i] = (int) ((minVM + maxVM) / 2.0 + quasiFactor);
    //             quasiOppositeSolution[i] = clampVM(quasiOppositeSolution[i], dataCenterIterator);
    //         }

    //         // Evaluate dynamic opposite solution
    //         Bat dynamicOppositeBat = new Bat(-1, dynamicOppositeSolution);
    //         double dynamicOppositeFitness = calcFitness(dynamicOppositeBat, dataCenterIterator, 0);

    //         // Evaluate quasi-opposite solution
    //         Bat quasiOppositeBat = new Bat(-1, quasiOppositeSolution);
    //         double quasiOppositeFitness = calcFitness(quasiOppositeBat, dataCenterIterator, 0);

    //         // Keep the better solution
    //         if (dynamicOppositeFitness > bat.getFitness() || quasiOppositeFitness > bat.getFitness()) {
    //             if (dynamicOppositeFitness > quasiOppositeFitness) {
    //                 bat.setChromosome(dynamicOppositeSolution);
    //                 bat.setFitness(dynamicOppositeFitness);
    //             } else {
    //                 bat.setChromosome(quasiOppositeSolution);
    //                 bat.setFitness(quasiOppositeFitness);
    //             }
    //         }
    //     }
    // }

    // // Helper method to clamp VM index within the valid range for the data center
    // private int clampVM(int vmIndex, int dataCenterIterator) {
    //     int minVM = (dataCenterIterator - 1) * 9;
    //     int maxVM = dataCenterIterator * 9 - 1;
    //     return Math.min(Math.max(vmIndex, minVM), maxVM);
    // }

    // // Generalized [OBL] function
    // private Bat applyOBL(Bat bat, int dataCenterIterator) {
    //     Bat oppositeBat = new Bat(bat.getChromosomeLength());
    //     for (int j = 0; j < bat.getChromosomeLength(); j++) {
    //         double lowerBound = 0; // Replace with actual lower bound for parameter j
    //         double upperBound = 8; // Replace with actual upper bound for parameter j
    //         double originalValue = bat.getGene(j);

    //         // Ensure originalValue is within bounds
    //         originalValue = Math.max(lowerBound, Math.min(upperBound, originalValue));

    //         // Compute opposite value
    //         double oppositeValue = lowerBound + upperBound - originalValue;
    
    //         // Clamp and round
    //         int newGene = (int) Math.round(Math.max(lowerBound, Math.min(upperBound, oppositeValue)));
            
    //         oppositeBat.setGene(j, newGene);

    //         // double oppositeValue = lowerBound + upperBound - originalValue;
    //         // oppositeBat.setGene(j, (int) Math.round(oppositeValue));
    //     }
    //     return oppositeBat;
    // }

    // // Method to implement [OBL]
    // private void applyOBL() {
    //     // Generate the opposite population
    //     PopulationBA oppositePopulation = population.generateOppositePopulation();

    //     // Combine and select the fittest bats
    //     population.combineAndSelectFittest();
    // }

    public void initializeGlobalBest(PopulationBA population, int dataCenterIterator) {
        int dcIndex = dataCenterIterator - 1;
        // Calculate fitness for each bat in the initial population
        for (Bat bat : population.getBats()) {
            calcFitness(bat, dataCenterIterator, 0);
        }
        // Sort to find the best bat and set as global best
        sortBatsAndFindBest(population, dataCenterIterator);
    }

    // [OBL] (Opposition-Based Learning) - Arfan
    public void applyOBL(PopulationBA population, int dataCenterIterator) {
        int dcIndex = dataCenterIterator - 1;
        int[] eliteSolution = globalBestPositions[dcIndex];
        int minVM = (dataCenterIterator - 1) * 9;
        int maxVM = dataCenterIterator * 9 - 1;
        
        for (Bat bat : population.getBats()) {
            int[] oppositeSolution = new int[bat.getChromosomeLength()];
            
            // Calculate opposite solution using OBL formula
            for (int i = 0; i < bat.getChromosomeLength(); i++) {
                oppositeSolution[i] = minVM + maxVM - bat.getGene(i);
                oppositeSolution[i] = clampVM(oppositeSolution[i], minVM, maxVM);
            }
            
            // Evaluate opposite solution
            Bat oppositeBat = new Bat(oppositeSolution.length);
            oppositeBat.setChromosome(oppositeSolution);
            double oppositeFitness = calcFitness(oppositeBat, dataCenterIterator, 0);
            
            // Replace if better
            if (oppositeFitness > bat.getFitness()) {
                bat.setChromosome(oppositeSolution);
                bat.setFitness(oppositeFitness);
            }
        }
    }

    private int clampVM(int value, int minVM, int maxVM) {
        return Math.min(Math.max(value, minVM), maxVM);
    }

//    Step 2: Initialize population
    public PopulationBA initPopulation(int chromosomeLength, int dataCenterIterator) {
        // // Step 1: Generate the original population
        // PopulationBA originalPopulation = new PopulationBA(this.populationSize, chromosomeLength, dataCenterIterator);
        
        // // Step 2: Generate the opposite population
        // PopulationBA oppositePopulation = originalPopulation.generateOppositePopulation();
        
        // // Step 3: Combine original and opposite populations and select the fittest
        // originalPopulation.combineAndSelectFittest();
        
        // return originalPopulation; // Return the final population

        // Membuat populasi kelelawar baru
        PopulationBA population = new PopulationBA(this.populationSize, chromosomeLength, dataCenterIterator);
         applyOBL(population, dataCenterIterator);  // New OBL step
         initializeGlobalBest(population, dataCenterIterator);
        return population;
    }

//         // Step 2: Initialize population using [OBL]
//         public PopulationBA initPopulation(int chromosomeLength, int dataCenterIterator) {
//         // Step 1: Generate the original population
//         PopulationBA population = new PopulationBA(this.populationSize, chromosomeLength, dataCenterIterator);
    
//         // Step 2: Generate the opposite population
//         PopulationBA oppositePopulation = new PopulationBA(this.populationSize, chromosomeLength, dataCenterIterator);
//         for (int i = 0; i < this.populationSize; i++) {
//             Bat originalBat = population.getBat(i);
//             Bat oppositeBat = new Bat(chromosomeLength);
    
//             for (int j = 0; j < chromosomeLength; j++) {
//                 // Assuming lowerBound[j] and upperBound[j] are defined for each parameter
//                 double lowerBound = 0; // Replace with actual lower bound for parameter j
//                 double upperBound = 8; // Replace with actual upper bound for parameter j
//                 double originalValue = originalBat.getGene(j);
//                 double oppositeValue = lowerBound + upperBound - originalValue;
//                 oppositeBat.setGene(j, (int) Math.round(oppositeValue)); // Konversi ke int
//                 // oppositeBat.setGene(j, oppositeValue);
//             }
//             oppositePopulation.setBat(i, oppositeBat);
//         }
    
//         // Step 3: Combine the original and opposite populations
//         PopulationBA combinedPopulation = new PopulationBA(2 * this.populationSize, chromosomeLength, dataCenterIterator);
//         for (int i = 0; i < this.populationSize; i++) {
//             combinedPopulation.setBat(i, population.getBat(i));
//             combinedPopulation.setBat(i + this.populationSize, oppositePopulation.getBat(i));
//         }
    
//         // Step 4: Select the fittest individuals
//         combinedPopulation.sortByFitness();
//         PopulationBA finalPopulation = new PopulationBA(this.populationSize, chromosomeLength, dataCenterIterator);
//         for (int i = 0; i < this.populationSize; i++) {
//             finalPopulation.setBat(i, combinedPopulation.getBat(i));
//         }
//         return finalPopulation;
//     }

    // Step 3: Define frequency
    public void defineFrequency() {
        Random random = new Random();
        for (int i = 0; i < populationSize; i++) {
            // Menghasilkan nilai frekuensi acak dari rentang 0 sampai 2
            frequency[i] = random.nextDouble() * 2; 
        }
    }

    // Step 4: Initialize loudness and pulse rate
    public void initLoudnessAndPulseRate() {
        Random random = new Random();
        for (int i = 0; i < populationSize; i++) {
            // Menghasilkan nilai intensitas suara dari rentang 0 sampai 1
            loudness[i] = random.nextDouble(); 
            // Menghasilkan nilai jumlah pulsa suara dari rentang 0 sampai 1
            pulseRate[i] = random.nextDouble();
        }
    }

    // Step 6: Generate new solutions
    public void generateNewSolutions(PopulationBA population, int iteration, int dataCenterIterator) {
        Random random = new Random();
        int dcIndex = dataCenterIterator - 1;
    
        // Ensure globalBestPositions is initialized before accessing
        if (globalBestPositions[dcIndex] == null) {
            globalBestPositions[dcIndex] = new int[population.getBats().get(0).getChromosomeLength()];
        }

        // Ensure new position is within bounds
        int minPosition = (dataCenterIterator - 1) * 9;
        int maxPosition = ((dataCenterIterator) * 9) - 1;
    
        for (Bat bat : population.getBats()) {
            // Update frequency, velocity, and position
            for (int i = 0; i < bat.getChromosomeLength(); i++) {
                double newFrequency = frequency[bat.getId()];
                double newVelocity = bat.getVelocity()[i] + (bat.getGene(i) - globalBestPositions[dcIndex][i]) * newFrequency;
                int newPosition = bat.getGene(i) + (int) Math.round(newVelocity);
    
                // if (newPosition < minPosition) {
                //     newPosition = minPosition;
                // } else if (newPosition > maxPosition) {
                //     newPosition = maxPosition;
                // }

                // Clamp new position
                newPosition = clampVM(newPosition, minPosition, maxPosition);
    
                bat.setVelocity(i, newVelocity);
                bat.setGene(i, newPosition);
            }
    
            // Step 7: Local search around the best solution
            if (random.nextDouble() > pulseRate[bat.getId()]) {
                int[] localSolution = globalBestPositions[dcIndex].clone();
                for (int i = 0; i < localSolution.length; i++) {
                    localSolution[i] += random.nextGaussian() * 0.1; // Small random perturbation
                }
                bat.setChromosome(localSolution);

                // // Apply [OBL] after local search
                // Bat oppositeLocalBat = applyOBL(bat, dataCenterIterator); // Assuming bounds are 0 and 9
                // double localOriginalFitness = calcFitness(bat, dataCenterIterator, 0);
                // double localOppositeFitness = calcFitness(oppositeLocalBat, dataCenterIterator, 0);

                // // Replace the bat with the opposite if it has better fitness
                // if (localOppositeFitness > localOriginalFitness) {
                //     bat.setChromosome(oppositeLocalBat.getChromosome());
                // }

            } else {
                // Generate a new solution randomly
                for (int i = 0; i < bat.getChromosomeLength(); i++) {
                    bat.setGene(i, random.nextInt(maxPosition - minPosition + 1) + minPosition);
                }

                // // Apply [OBL] after random generation
                // Bat oppositeBat = applyOBL(bat, dataCenterIterator);
                // double originalFitness = calcFitness(bat, dataCenterIterator, 0);
                // double oppositeFitness = calcFitness(oppositeBat, dataCenterIterator, 0);

                // if (oppositeFitness > originalFitness) {
                //     bat.setChromosome(oppositeBat.getChromosome());
                // }

            }
        }
        // applyOBL(population, dataCenterIterator);
    }
    

    // Step 12: Accept new solutions
    public void acceptNewSolutions(PopulationBA population, int dataCenterIterator) {
        Random random = new Random();
        int dcIndex = dataCenterIterator - 1;
    
        for (Bat bat : population.getBats()) {
            double fitness = calcFitness(bat, dataCenterIterator, 0); // Calculate fitness for the bat
    
            if (random.nextDouble() < loudness[bat.getId()] && fitness > globalBestFitnesses[dcIndex]) {
                globalBestFitnesses[dcIndex] = fitness;
                globalBestPositions[dcIndex] = bat.getChromosome().clone();
    
                // Update loudness and pulse rate
                loudness[bat.getId()] *= alpha; // Reduce loudness
                pulseRate[bat.getId()] += 0.1; // Increase pulse rate (ensure it doesn't exceed 1)
                if (pulseRate[bat.getId()] > 1) {
                    pulseRate[bat.getId()] = 1;
                }
            } 
            // else {
            //     // Apply [OBL] if the new solution is not accepted
            //     Bat oppositeBat = applyOBL(bat, dataCenterIterator);
            //     double originalFitness = calcFitness(bat, dataCenterIterator, 0);
            //     double oppositeFitness = calcFitness(oppositeBat, dataCenterIterator, 0);
    
            //     if (oppositeFitness > originalFitness) {
            //         bat.setChromosome(oppositeBat.getChromosome());
            //     }
            // }
        }
        // applyOBL(population, dataCenterIterator);
    }
    

    // Step 16: Sort bats and find the current best solution
    public void sortBatsAndFindBest(PopulationBA population, int dataCenterIterator) {
        population.getBats().sort((b1, b2) -> Double.compare(b2.getFitness(), b1.getFitness())); // Sort by fitness
        int dcIndex = dataCenterIterator - 1;
        globalBestFitnesses[dcIndex] = population.getBats().get(0).getFitness();
        globalBestPositions[dcIndex] = population.getBats().get(0).getChromosome().clone();
    }

    // Fitness calculation
    public double calcFitness(Bat bat, int dataCenterIterator, int cloudletIteration) {
        double totalExecutionTime = 0;
        double totalCost = 0;
        int iterator = 0;
        dataCenterIterator = dataCenterIterator - 1;

        for (int i = 0 + dataCenterIterator * 9 + cloudletIteration * 54;
             i < 9 + dataCenterIterator * 9 + cloudletIteration * 54; i++) {
            int gene = bat.getGene(iterator);
            double mips = calculateMips(gene % 9);

            totalExecutionTime += cloudletList.get(i).getCloudletLength() / mips;
            totalCost += calculateCost(vmList.get(gene % 9), cloudletList.get(i));
            iterator++;
        }

        double makespanFitness = calculateMakespanFitness(totalExecutionTime);
        double costFitness = calculateCostFitness(totalCost);

        double fitness = makespanFitness + costFitness;

        bat.setFitness(fitness);
        return fitness;
    }

    // Helper methods
    private double calculateMips(int vmIndex) {
        double mips = 0;
        if (vmIndex % 9 == 0 || vmIndex % 9 == 3 || vmIndex % 9 == 6) {
            mips = 400;
        } else if (vmIndex % 9 == 1 || vmIndex % 9 == 4 || vmIndex % 9 == 7) {
            mips = 500;
        } else if (vmIndex % 9 == 2 || vmIndex % 9 == 5 || vmIndex % 9 == 8) {
            mips = 600;
        }
        return mips;
    }

    private double calculateCost(Vm vm, Cloudlet cloudlet) {
        double costPerMips = vm.getCostPerMips();
        double cloudletLength = cloudlet.getCloudletLength();
        double mips = vm.getMips();
        double executionTime = cloudletLength / mips;
        return costPerMips * executionTime;
    }

    private double calculateMakespanFitness(double totalExecutionTime) {
        return 1.0 / totalExecutionTime;
    }

    private double calculateCostFitness(double totalCost) {
        return 1.0 / totalCost;
    }

    public int[] getBestVmAllocationForDatacenter(int dataCenterIterator) {
        return globalBestPositions[dataCenterIterator - 1];
    }

    public double getBestFitnessForDatacenter(int dataCenterIterator) {
        return globalBestFitnesses[dataCenterIterator - 1];
    }
}