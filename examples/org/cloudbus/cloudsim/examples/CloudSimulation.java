package org.cloudbus.cloudsim.examples;

import java.util.Locale;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.DoubleStream;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;


public class CloudSimulation {
    private static PowerDatacenter datacenter1, datacenter2, datacenter3, datacenter4, datacenter5, datacenter6;
    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmlist;
    private static int bot = 30;
    private static int iterationCount = 10; // Number of iterations per dataset

    public static void main(String[] args) {
      Locale.setDefault(new Locale("en", "US"));
      Log.printLine("Starting Cloud Simulation with Bat Algorithm...");
  
      try {
          // Run simulation for SDSC datasets
          runSDSCDatasets();
          
          Log.printLine("All simulations completed!");
      } catch (Exception e) {
          e.printStackTrace();
          Log.printLine("Simulation terminated due to an error");
      }
  }

  /**
   * Determines the dataset size based on the dataset name
   */
  private static int getDatasetSize(String datasetName) {
      if (datasetName.equals("SDSC7395")) {
          return 7395;
      } else if (datasetName.startsWith("RandomSimple_") || datasetName.startsWith("RandomStratified_")) {
          // Extract size from dataset name (e.g., "RandomSimple_1000" -> 1000)
          try {
              return Integer.parseInt(datasetName.split("_")[1]);
          } catch (NumberFormatException e) {
              return 1000; // Default size if parsing fails
          }
      }
      return 1000; // Default size for unknown datasets
  }

  /**
   * Runs simulation for datasets with 10 iterations each
   */
  private static void runSDSCDatasets() throws Exception {
      // Create CSV files for results
      BufferedWriter resultsStratifiedWriter = new BufferedWriter(new FileWriter("results_BAOBL_Stratified.csv"));
      BufferedWriter resultsRandomWriter = new BufferedWriter(new FileWriter("results_BAOBL_Random.csv"));
      
      // Write headers to CSV files
      writeCSVHeader(resultsStratifiedWriter);
      writeCSVHeader(resultsRandomWriter);
      
      System.out.println("==================================================");
      System.out.println("Starting simulation with RandomStratified and RandomSimple datasets");
      System.out.println("10 sizes (1000-10000) with 10 iterations each");
      System.out.println("==================================================");
      
      // Run datasets
      System.out.println("\nProcessing datasets...");
      Log.printLine("Running datasets...");
      
      int totalIterations = 0;
      
      // Define dataset paths for both RandomStratified and RandomSimple datasets (1000-10000)
      String[] datasetPaths = new String[20];
      String[] datasetNames = new String[20];
      
      // Add RandomStratified datasets
      for (int i = 0; i < 10; i++) {
          int size = (i + 1) * 1000;
          datasetPaths[i] = "C:/Users/Advan/Documents/Tugas Akhir/Tools/CloudSim/cloudsim-3.0.3/datasets/randomStratified/randomStratified_" + size + ".txt";
          datasetNames[i] = "RandomStratified_" + size;
      }
      
      // Add RandomSimple datasets
      for (int i = 0; i < 10; i++) {
          int size = (i + 1) * 1000;
          datasetPaths[i + 10] = "C:/Users/Advan/Documents/Tugas Akhir/Tools/CloudSim/cloudsim-3.0.3/datasets/randomSimple/randomSimple_" + size + ".txt";
          datasetNames[i + 10] = "RandomSimple_" + size;
      }
      
      for (int datasetIndex = 0; datasetIndex < datasetPaths.length; datasetIndex++) {
          String datasetName = datasetNames[datasetIndex];
          int datasetSize = getDatasetSize(datasetName);
          
          System.out.println("\n  └─ Dataset: " + datasetName + " (Size: " + datasetSize + " tasks)");
          Log.printLine(datasetName + " dataset...");
          
          for (int i = 0; i < iterationCount; i++) {
              totalIterations++;
              System.out.println("    └─ Iteration " + (i+1) + "/" + iterationCount + " (" + totalIterations + "/" + (datasetPaths.length * iterationCount) + " total)");
              Log.printLine(datasetName + " Iteration " + (i+1) + " of " + iterationCount);
              SimulationResult result = runSimulation(datasetPaths[datasetIndex], datasetSize);
              
              // Write to appropriate file based on dataset type
              if (datasetName.startsWith("RandomStratified_")) {
                  writeResultToCSV(resultsStratifiedWriter, result, datasetName, i+1);
              } else {
                  writeResultToCSV(resultsRandomWriter, result, datasetName, i+1);
              }
              
              System.out.println("      └─ Completed with response time: " + String.format("%.2f", result.responseTime));
          }
      }
      
      // Close CSV writers
      resultsStratifiedWriter.close();
      resultsRandomWriter.close();
      
      System.out.println("\n==================================================");
      System.out.println("All " + totalIterations + " iterations completed successfully!");
      System.out.println("Results saved to: results_BAOBL_Stratified.csv and results_BAOBL_Random.csv");
      System.out.println("==================================================");
  }
  
  /**
   * Writes CSV header to the specified writer
   */
  private static void writeCSVHeader(BufferedWriter writer) throws Exception {
      writer.write("Dataset,Iteration,ResponseTime,TotalCPUTime,TotalWaitTime,TotalCloudletsFinished," +
              "AverageCloudletsFinished,AverageStartTime,AverageExecutionTime,AverageFinishTime," +
              "AverageWaitingTime,Throughput,Makespan,ImbalanceDegree,SchedulingLength,ResourceUtilization,EnergyConsumption\n");
  }
  
  /**
   * Writes simulation result to the specified CSV writer
   */
  private static void writeResultToCSV(BufferedWriter writer, SimulationResult result, String datasetName, int iteration) throws Exception {
      writer.write(String.format("%s,%d,%.6f,%.6f,%.6f,%d,%.6f,%.6f,%.6f,%.6f,%.6f,%.9f,%.6f,%.3f,%.6f,%.6f,%.2f\n",
              datasetName, iteration, result.responseTime, result.totalCPUTime, result.totalWaitTime, 
              result.totalCloudletsFinished, result.averageCloudletsFinished, result.averageStartTime,
              result.averageExecutionTime, result.averageFinishTime, result.averageWaitingTime,
              result.throughput, result.makespan, result.imbalanceDegree, result.schedulingLength,
              result.resourceUtilization, result.energyConsumption));
  }
  
  /**
   * Runs a single simulation with the specified dataset
   */
  private static SimulationResult runSimulation(String datasetPath, int datasetSize) throws Exception {
      System.out.println("      └─ Initializing simulation...");
      
      int num_user = 1;
      Calendar calendar = Calendar.getInstance();
      boolean trace_flag = false;

      CloudSim.init(num_user, calendar, trace_flag);

      int hostId = 0;

      datacenter1 = createDatacenter("DataCenter_1", hostId);
      hostId = 3;
      datacenter2 = createDatacenter("DataCenter_2", hostId);
      hostId = 6;
      datacenter3 = createDatacenter("DataCenter_3", hostId);
      hostId = 9;
      datacenter4 = createDatacenter("DataCenter_4", hostId);
      hostId = 12;
      datacenter5 = createDatacenter("DataCenter_5", hostId);
      hostId = 15;
      datacenter6 = createDatacenter("DataCenter_6", hostId);

      DatacenterBroker broker = createBroker();
      int brokerId = broker.getId();
      int vmNumber = 54;
      int cloudletNumber = datasetSize;

      System.out.println("      └─ Creating VMs and Cloudlets...");
      vmlist = createVM(brokerId, vmNumber);
      cloudletList = createCloudlet(brokerId, cloudletNumber, datasetPath);

      broker.submitVmList(vmlist);
      broker.submitCloudletList(cloudletList);

      int cloudletLoopingNumber = cloudletNumber / vmNumber - 1;

      System.out.println("      └─ Running Bat Algorithm for task scheduling...");
      for (int cloudletIterator = 0; cloudletIterator <= cloudletLoopingNumber; cloudletIterator++) {
          System.out.println("        └─ Cloudlet Iteration " + cloudletIterator + "/" + cloudletLoopingNumber);

          for (int dataCenterIterator = 1; dataCenterIterator <= 6; dataCenterIterator++) {
              System.out.println("          └─ Processing Datacenter " + dataCenterIterator + "/6");
              
              // Parameters for Bat Algorithm
              int maxIterations = 5; // Changed from 10 to 5
              int populationSize = 10; // Changed from 5 to 30
              double alpha = 0.92; // Parameter for updating loudness
              double gamma = 0.92; // Parameter for updating pulse rate

              // Initialize Bat Algorithm
              BatAlgorithm batAlgorithm = new BatAlgorithm(maxIterations, populationSize, alpha, gamma,
                      cloudletList, vmlist, cloudletNumber);

              // Initialize population
              System.out.println("            └─ Initializing population");
              PopulationBA population = batAlgorithm.initPopulation(cloudletNumber, dataCenterIterator);

              // Define frequency and initialize loudness and pulse rate
              batAlgorithm.defineFrequency();
              batAlgorithm.initLoudnessAndPulseRate();

              // Iteration loop
              int iteration = 1;
              while (iteration <= maxIterations) {
                  System.out.println("            └─ Bat Algorithm Iteration " + iteration + "/" + maxIterations);
                  
                  // Generate new solutions
                  batAlgorithm.generateNewSolutions(population, iteration, dataCenterIterator);

                  // Accept new solutions
                  batAlgorithm.acceptNewSolutions(population, dataCenterIterator);

                  // Sort bats and find the current best solution
                  batAlgorithm.sortBatsAndFindBest(population, dataCenterIterator);

                  System.out.println("              └─ Best Fitness: " + batAlgorithm.getBestFitnessForDatacenter(dataCenterIterator));

                  iteration++;
              }

              // Get the best solution
              int[] bestSolution = batAlgorithm.getBestVmAllocationForDatacenter(dataCenterIterator);

              // Assign tasks to VMs based on bestSolution
              System.out.println("            └─ Assigning tasks to VMs");
              for (int assigner = 0 + (dataCenterIterator - 1) * 9 + cloudletIterator * 54;
                   assigner < 9 + (dataCenterIterator - 1) * 9 + cloudletIterator * 54; assigner++) {
                  int vmId = bestSolution[assigner - (dataCenterIterator - 1) * 9 - cloudletIterator * 54];
                  broker.bindCloudletToVm(assigner, vmId);
              }
          }
      }

      // Start simulation
      System.out.println("      └─ Starting CloudSim simulation...");
      CloudSim.startSimulation();

      List<Cloudlet> newList = broker.getCloudletReceivedList();

      CloudSim.stopSimulation();
      System.out.println("      └─ Simulation completed, calculating results...");

      // Calculate and return simulation results
      return calculateSimulationResults(newList);
  }

  /**
   * Calculates simulation results from the completed cloudlets
   */
  private static SimulationResult calculateSimulationResults(List<Cloudlet> list) {
      SimulationResult result = new SimulationResult();
      
      int size = list.size();
      Cloudlet cloudlet = null;

      double waitTimeSum = 0.0;
      double CPUTimeSum = 0.0;
      int totalValues = 0;
      DecimalFormat dft = new DecimalFormat("###,##");

      double response_time[] = new double[size];

      // Calculate metrics
      for (int i = 0; i < size; i++) {
          cloudlet = list.get(i);

          if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
              CPUTimeSum = CPUTimeSum + cloudlet.getActualCPUTime();
              waitTimeSum = waitTimeSum + cloudlet.getWaitingTime();
              totalValues++;

              response_time[i] = cloudlet.getActualCPUTime();
          }
      }
      
      DoubleSummaryStatistics stats = DoubleStream.of(response_time).summaryStatistics();

      // Calculate average response time
      result.responseTime = CPUTimeSum / totalValues;
      
      // Calculate total CPU time
      result.totalCPUTime = CPUTimeSum;
      
      // Calculate total wait time
      result.totalWaitTime = waitTimeSum;
      
      // Calculate total cloudlets finished
      result.totalCloudletsFinished = totalValues;
      
      // Calculate average cloudlets finished
      result.averageCloudletsFinished = CPUTimeSum / totalValues;
      
      // Calculate average start time
      double totalStartTime = 0.0;
      for (int i = 0; i < size; i++) {
          totalStartTime += cloudletList.get(i).getExecStartTime();
      }
      result.averageStartTime = totalStartTime / size;
      
      // Calculate average execution time
      double ExecTime = 0.0;
      for (int i = 0; i < size; i++) {
          ExecTime += cloudletList.get(i).getActualCPUTime();
      }
      result.averageExecutionTime = ExecTime / size;
      
      // Calculate average finish time
      double totalTime = 0.0;
      for (int i = 0; i < size; i++) {
          totalTime += cloudletList.get(i).getFinishTime();
      }
      result.averageFinishTime = totalTime / size;
      
      // Calculate average waiting time
      result.averageWaitingTime = cloudlet.getWaitingTime() / size;
      
      // Calculate throughput
      double maxFT = 0.0;
      for (int i = 0; i < size; i++) {
          double currentFT = cloudletList.get(i).getFinishTime();
          if (currentFT > maxFT) {
              maxFT = currentFT;
          }
      }
      result.throughput = size / maxFT;
      
      // Calculate makespan
      result.makespan = cloudlet.getFinishTime();
      
      // Calculate imbalance degree
      result.imbalanceDegree = (stats.getMax() - stats.getMin()) / (CPUTimeSum / totalValues);
      
      // Calculate scheduling length
      result.schedulingLength = waitTimeSum + result.makespan;
      
      // Calculate resource utilization
      result.resourceUtilization = (CPUTimeSum / (result.makespan * 54)) * 100;
      
      // Calculate energy consumption
      result.energyConsumption = (datacenter1.getPower() + datacenter2.getPower() + datacenter3.getPower() + 
              datacenter4.getPower() + datacenter5.getPower() + datacenter6.getPower()) / (3600 * 1000);
      
      return result;
  }

  private static List<Cloudlet> createCloudlet(int userId, int cloudlets, String datasetPath) {
      ArrayList<Double> randomSeed = getSeedValue(cloudlets, datasetPath);

      LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

      long fileSize = 300; 
      long outputSize = 300;
      int pesNumber = 1; 
      UtilizationModel utilizationModel = new UtilizationModelFull();

      // Ensure we have exactly the required number of cloudlets
      for (int i = 0; i < cloudlets; i++) {
          long length = 0;

          if (randomSeed.size() > i) {
              length = Double.valueOf(randomSeed.get(i)).longValue();
          } else {
              // If we run out of values, use the last available value
              length = Double.valueOf(randomSeed.get(randomSeed.size() - 1)).longValue();
          }

          Cloudlet cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
          cloudlet.setUserId(userId);
          list.add(cloudlet);
      }

      return list;
  }

  private static List<Vm> createVM(int userId, int vms) {
      LinkedList<Vm> list = new LinkedList<Vm>();

      long size = 10000;
      int[] ram = { 512, 1024, 2048 }; 
      int[] mips = { 400, 500, 600 }; 
      long bw = 1000; 
      int pesNumber = 1; 
      String vmm = "Xen"; 

      Vm[] vm = new Vm[vms];

      for (int i = 0; i < vms; i++) {
          vm[i] = new Vm(i, userId, mips[i % 3], pesNumber, ram[i % 3], bw, size, vmm, new CloudletSchedulerSpaceShared());
          list.add(vm[i]);
      }

      return list;
  }

  private static ArrayList<Double> getSeedValue(int cloudletcount, String datasetPath) {
      ArrayList<Double> seed = new ArrayList<Double>();
      
      // First try to read from the file
      try {
          File fobj = new File(datasetPath);
          if (!fobj.exists()) {
              System.out.println("      └─ Error: Dataset file not found: " + datasetPath);
              throw new FileNotFoundException("Dataset file not found: " + datasetPath);
          }
          
          // Use BufferedReader instead of Scanner for more reliable reading
          java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(fobj));
          String line;
          
          while ((line = reader.readLine()) != null && cloudletcount > 0) {
              // Split the line by whitespace and process each token
              String[] tokens = line.trim().split("\\s+");
              for (String token : tokens) {
                  if (cloudletcount <= 0) break;
                  
                  try {
                      double value = Double.parseDouble(token);
                      seed.add(value);
                      cloudletcount--;
                  } catch (NumberFormatException e) {
                      // Skip non-numeric tokens
                      continue;
                  }
              }
          }
          
          reader.close();
          
          // If we don't have enough values, fill with random values
          if (cloudletcount > 0) {
              System.out.println("      └─ Warning: Dataset file doesn't contain enough values. Adding random values.");
              java.util.Random random = new java.util.Random();
              for (int i = 0; i < cloudletcount; i++) {
                  // Generate random values between 1000 and 10000
                  seed.add(1000.0 + random.nextDouble() * 9000.0);
              }
          }
          
      } catch (Exception e) {
          System.out.println("      └─ Error reading dataset file: " + e.getMessage());
          e.printStackTrace();
          
          // Generate random values if file not found or error reading
          System.out.println("      └─ Generating random values instead.");
          java.util.Random random = new java.util.Random();
          for (int i = 0; i < cloudletcount; i++) {
              // Generate random values between 1000 and 10000
              seed.add(1000.0 + random.nextDouble() * 9000.0);
          }
      }

      return seed;
  }

  private static PowerDatacenter createDatacenter(String name, int hostId) {
      List<PowerHost> hostList = new ArrayList<PowerHost>();

      List<Pe> peList1 = new ArrayList<Pe>();
      List<Pe> peList2 = new ArrayList<Pe>();
      List<Pe> peList3 = new ArrayList<Pe>();

      int mipsunused = 300; 
      int mips1 = 400; 
      int mips2 = 500;
      int mips3 = 600;

      peList1.add(new Pe(0, new PeProvisionerSimple(mips1))); 
      peList1.add(new Pe(1, new PeProvisionerSimple(mips1)));
      peList1.add(new Pe(2, new PeProvisionerSimple(mips1)));
      peList1.add(new Pe(3, new PeProvisionerSimple(mipsunused)));
      peList2.add(new Pe(4, new PeProvisionerSimple(mips2)));
      peList2.add(new Pe(5, new PeProvisionerSimple(mips2)));
      peList2.add(new Pe(6, new PeProvisionerSimple(mips2)));
      peList2.add(new Pe(7, new PeProvisionerSimple(mipsunused)));
      peList3.add(new Pe(8, new PeProvisionerSimple(mips3)));
      peList3.add(new Pe(9, new PeProvisionerSimple(mips3)));
      peList3.add(new Pe(10, new PeProvisionerSimple(mips3)));
      peList3.add(new Pe(11, new PeProvisionerSimple(mipsunused)));

      int ram = 128000;
      long storage = 1000000;
      int bw = 10000;
      int maxpower = 117; 
      int staticPowerPercentage = 50; 

      hostList.add(
          new PowerHostUtilizationHistory(
              hostId, new RamProvisionerSimple(ram),
              new BwProvisionerSimple(bw),
              storage,
              peList1,
              new VmSchedulerTimeShared(peList1),
              new PowerModelLinear(maxpower, staticPowerPercentage)));
      hostId++;

      hostList.add(
          new PowerHostUtilizationHistory(
              hostId, new RamProvisionerSimple(ram),
              new BwProvisionerSimple(bw),
              storage,
              peList2,
              new VmSchedulerTimeShared(peList2),
              new PowerModelLinear(maxpower, staticPowerPercentage)));
      hostId++;

      hostList.add(
          new PowerHostUtilizationHistory(
              hostId, new RamProvisionerSimple(ram),
              new BwProvisionerSimple(bw),
              storage,
              peList3,
              new VmSchedulerTimeShared(peList3),
              new PowerModelLinear(maxpower, staticPowerPercentage)));

      String arch = "x86"; 
      String os = "Linux"; 
      String vmm = "Xen"; 
      double time_zone = 10.0; 
      double cost = 3.0; 
      double costPerMem = 0.05; 
      double costPerStorage = 0.1; 
      double costPerBw = 0.1; 
      LinkedList<Storage> storageList = new LinkedList<Storage>();

      DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
          arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

      PowerDatacenter datacenter = null;
      try {
          datacenter = new PowerDatacenter(name, characteristics, new PowerVmAllocationPolicySimple(hostList), storageList, 9); 
      } catch (Exception e) {
          e.printStackTrace();
      }

      return datacenter;
  }

  private static DatacenterBroker createBroker() {
      DatacenterBroker broker = null;
      try {
          broker = new DatacenterBroker("Broker");
      } catch (Exception e) {
          e.printStackTrace();
          return null;
      }
      return broker;
  }

  /**
   * Class to store simulation results
   */
  private static class SimulationResult {
      double responseTime;
      double totalCPUTime;
      double totalWaitTime;
      int totalCloudletsFinished;
      double averageCloudletsFinished;
      double averageStartTime;
      double averageExecutionTime;
      double averageFinishTime;
      double averageWaitingTime;
      double throughput;
      double makespan;
      double imbalanceDegree;
      double schedulingLength;
      double resourceUtilization;
      double energyConsumption;
  }
}
