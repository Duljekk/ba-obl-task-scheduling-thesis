package org.cloudbus.cloudsim.examples;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

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
    private double fmin = 0.0; // Minimum frequency
    private double fmax = 2.0; // Maximum frequency
    private boolean useOBL = true; // Flag to enable/disable OBL
    private double oblProbability = 1; // Probability of applying OBL at each stage

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

    // Step 2: Initialize population
    public PopulationBA initPopulation(int chromosomeLength, int dataCenterIterator) {
        // Membuat populasi kelelawar baru
        PopulationBA population = new PopulationBA(this.populationSize, chromosomeLength, dataCenterIterator);
        
        // Apply Opposition-Based Learning if enabled
        if (useOBL) {
            population = applyOppositionBasedLearning(population, dataCenterIterator);
        }
        
        return population;
    }

    /**
     * Applies Opposition-Based Learning to improve initial population
     * @param population Original population
     * @param dataCenterIterator Current data center index
     * @return Improved population after OBL
     */
    private PopulationBA applyOppositionBasedLearning(PopulationBA population, int dataCenterIterator) {
        // Create opposite population
        PopulationBA oppositePopulation = new PopulationBA(this.populationSize, 
            population.getBats().get(0).getChromosomeLength(), dataCenterIterator);
        
        // Calculate bounds for each gene
        int minPosition = (dataCenterIterator - 1) * 9;
        int maxPosition = ((dataCenterIterator) * 9) - 1;
        
        // Generate opposite solutions
        for (int i = 0; i < populationSize; i++) {
            Bat originalBat = population.getBats().get(i);
            Bat oppositeBat = oppositePopulation.getBats().get(i);
            
            // Generate opposite chromosome
            for (int j = 0; j < originalBat.getChromosomeLength(); j++) {
                int originalGene = originalBat.getGene(j);
                int oppositeGene = minPosition + maxPosition - originalGene;
                
                // Ensure the opposite gene is within bounds
                if (oppositeGene < minPosition) {
                    oppositeGene = minPosition;
                } else if (oppositeGene > maxPosition) {
                    oppositeGene = maxPosition;
                }
                
                oppositeBat.setGene(j, oppositeGene);
            }
            
            // Calculate fitness for opposite solution
            calcFitness(oppositeBat, dataCenterIterator, 0);
        }
        
        // Combine original and opposite populations
        List<Bat> combinedBats = new ArrayList<>();
        combinedBats.addAll(population.getBats());
        combinedBats.addAll(oppositePopulation.getBats());
        
        // Sort by fitness and select best Np solutions
        combinedBats.sort((b1, b2) -> Double.compare(b2.getFitness(), b1.getFitness()));
        
        // Create new population with best solutions
        PopulationBA improvedPopulation = new PopulationBA(this.populationSize, 
            population.getBats().get(0).getChromosomeLength(), dataCenterIterator);
        
        for (int i = 0; i < populationSize; i++) {
            improvedPopulation.getBats().set(i, combinedBats.get(i));
        }
        
        return improvedPopulation;
    }

    // Step 3: Define frequency using equation 2: fi = fmin + (fmax − fmin)β
    public void defineFrequency() {
        Random random = new Random();
        for (int i = 0; i < populationSize; i++) {
            double beta = random.nextDouble(); // β in [0,1]
            frequency[i] = fmin + (fmax - fmin) * beta;
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
            // Store previous position
            int[] previousPosition = bat.getChromosome().clone();
            
            // Update frequency, velocity, and position
            for (int i = 0; i < bat.getChromosomeLength(); i++) {
                double newFrequency = frequency[bat.getId()];
                // Update velocity using previous position
                double newVelocity = bat.getVelocity()[i] + (previousPosition[i] - globalBestPositions[dcIndex][i]) * newFrequency;
                bat.setVelocity(i, newVelocity);
                
                // Update position using equation 4: xit = xi(t-1) + vit
                int newPosition = previousPosition[i] + (int) Math.round(newVelocity);
    
                // Ensure position stays within bounds
                if (newPosition < minPosition) {
                    newPosition = minPosition;
                } else if (newPosition > maxPosition) {
                    newPosition = maxPosition;
                }
    
                bat.setGene(i, newPosition);
            }
    
            // Step 7: Local search around the best solution
            if (random.nextDouble() > pulseRate[bat.getId()]) {
                int[] localSolution = globalBestPositions[dcIndex].clone();
                for (int i = 0; i < localSolution.length; i++) {
                    localSolution[i] += random.nextGaussian() * 0.1; // Small random perturbation
                }
                bat.setChromosome(localSolution);
            } else {
                // Generate a new solution randomly
                for (int i = 0; i < bat.getChromosomeLength(); i++) {
                    bat.setGene(i, random.nextInt(maxPosition - minPosition + 1) + minPosition);
                }
            }
        }

        // Apply OBL after generating new solutions with probability oblProbability
        if (useOBL && random.nextDouble() < oblProbability) {
            population = applyOppositionBasedLearning(population, dataCenterIterator);
        }
    }
    

    // Step 12: Accept new solutions
    public void acceptNewSolutions(PopulationBA population, int dataCenterIterator) {
        Random random = new Random();
        int dcIndex = dataCenterIterator - 1;
    
        // Apply OBL before accepting new solutions with probability oblProbability
        if (useOBL && random.nextDouble() < oblProbability) {
            population = applyOppositionBasedLearning(population, dataCenterIterator);
        }

        for (Bat bat : population.getBats()) {
            double fitness = calcFitness(bat, dataCenterIterator, 0); // Calculate fitness for the bat
    
            if (random.nextDouble() < loudness[bat.getId()] && fitness > globalBestFitnesses[dcIndex]) {
                globalBestFitnesses[dcIndex] = fitness;
                globalBestPositions[dcIndex] = bat.getChromosome().clone();
    
                // Update loudness and pulse rate
                loudness[bat.getId()] *= alpha; // Reduce loudness
                pulseRate[bat.getId()] += gamma; // Increase pulse rate (ensure it doesn't exceed 1)
                if (pulseRate[bat.getId()] > 1) {
                    pulseRate[bat.getId()] = 1;
                }
            } 
        }
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