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
    private static int bot = 10;
    private static int iterationCount = 10; // Number of iterations for each dataset

    public static void main(String[] args) {
      Locale.setDefault(new Locale("en", "US"));
        Log.printLine("Starting Cloud Simulation with PSO...");

        try {
            // Run simulation for all datasets
            runAllDatasets();
            
            Log.printLine("All dataset simulations completed!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Simulation terminated due to an error");
        }
    }

    /**
     * Runs simulation for all datasets multiple times and saves results to CSV files
     */
    private static void runAllDatasets() throws Exception {
        // Create CSV file for SDSC results
        BufferedWriter sdcsWriter = new BufferedWriter(new FileWriter("results_SDSC.csv"));
        
        // Write header to CSV file
        writeCSVHeader(sdcsWriter);
        
        // Calculate total number of simulations
        int totalDatasets = 1; // Only SDSC dataset
        int totalSimulations = totalDatasets * iterationCount;
        int currentSimulation = 0;
        
        System.out.println("==================================================");
        System.out.println("Starting simulation with " + totalSimulations + " total runs");
        System.out.println("(SDSC dataset × " + iterationCount + " iterations)");
        System.out.println("==================================================");
        
        // Run SDSC dataset
        System.out.println("\n[1/1] Processing SDSC dataset...");
        Log.printLine("Running SDSC dataset...");
        String sdcsFile = "C:/Users/Advan/Documents/Tugas Akhir/Tools/CloudSim/cloudsim-3.0.3/datasets/SDSC/SDSC7395.txt";
        for (int i = 0; i < iterationCount; i++) {
            currentSimulation++;
            System.out.println("  └─ Iteration " + (i+1) + "/" + iterationCount + " (" + currentSimulation + "/" + totalSimulations + " total)");
            Log.printLine("SDSC Iteration " + (i+1) + " of " + iterationCount);
            SimulationResult result = runSimulation(sdcsFile, "SDSC");
            writeResultToCSV(sdcsWriter, result, "SDSC", i+1);
            System.out.println("      └─ Completed with response time: " + String.format("%.2f", result.responseTime));
        }
        
        // Close CSV writer
        sdcsWriter.close();
        
        System.out.println("\n==================================================");
        System.out.println("All " + totalSimulations + " simulations completed successfully!");
        System.out.println("Results saved to:");
        System.out.println("- results_SDSC.csv");
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
    private static SimulationResult runSimulation(String datasetPath, String datasetName) throws Exception {
        System.out.println("      └─ Initializing simulation for " + datasetName + "...");
        
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
          
          // Extract dataset size from the dataset name or file path
          int datasetSize;
          if (datasetName.equals("SDSC")) {
              datasetSize = 7395; // SDSC dataset has 7395 tasks
          } else {
              // Extract size from dataset name (e.g., "RandomStratified_1000" -> 1000)
              String[] parts = datasetName.split("_");
              datasetSize = Integer.parseInt(parts[parts.length - 1]);
          }
          
          int cloudletNumber = datasetSize;
  
        System.out.println("      └─ Creating VMs and Cloudlets...");
          vmlist = createVM(brokerId, vmNumber);
        cloudletList = createCloudlet(brokerId, cloudletNumber, datasetPath);
  
          broker.submitVmList(vmlist);
          broker.submitCloudletList(cloudletList);
  
          int cloudletLoopingNumber = cloudletNumber / vmNumber - 1;
  
        System.out.println("      └─ Running PSO algorithm for task scheduling...");
          for (int cloudletIterator = 0; cloudletIterator <= cloudletLoopingNumber; cloudletIterator++) {
            System.out.println("        └─ Cloudlet Iteration " + cloudletIterator + "/" + cloudletLoopingNumber);
  
              for (int dataCenterIterator = 1; dataCenterIterator <= 6; dataCenterIterator++) {
                System.out.println("          └─ Processing Datacenter " + dataCenterIterator + "/6");
                
                // Parameters for DAPDP
                int Imax = 5;
                int populationSize = 30;
                double wMax = 0.6;
                double wMin = 0.4;
                double l1 = 1.5;
                double l2 = 2.5;

                // EOBL Coefficient
                double d = 0.3;
                
                // Static inertia weight for PSO
                double w = 0.6;

                // PSO
                PSO PSO = new PSO(Imax, populationSize, w, l1, l2, cloudletList, vmlist, cloudletNumber);
  
                  // Initialize population
                System.out.println("            └─ Initializing population");

                PopulationPSO population = PSO.initPopulation(cloudletNumber, dataCenterIterator);
  
                // Evaluate initial fitness
                PSO.evaluateFitness(population, dataCenterIterator, cloudletIterator);
  
                  // Iteration loop
                  int iteration = 1;
                while (iteration <= Imax) {
                    System.out.println("            └─ PSO Iteration " + iteration + "/" + Imax);
                    PSO.updateVelocitiesAndPositions(population, iteration, dataCenterIterator);
                    PSO.evaluateFitness(population, dataCenterIterator, cloudletIterator);

                    System.out.println("              └─ Best Fitness: " + PSO.getBestFitnessForDatacenter(dataCenterIterator));
  
                      iteration++;
                  }
  
                  // Get the best solution
                int[] bestSolution = PSO.getBestVmAllocationForDatacenter(dataCenterIterator);
  
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

    for (int i = 0; i < cloudlets; i++) {
      long length = 0;

      if (randomSeed.size() > i) {
        length = Double.valueOf(randomSeed.get(i)).longValue();
      }

      Cloudlet cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
      cloudlet.setUserId(userId);
      list.add(cloudlet);
    }
    Collections.shuffle(list);

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

  private static void printCloudletList(List<Cloudlet> list) throws FileNotFoundException {

    // Initializing the printed output to zero
    int size = list.size();
    Cloudlet cloudlet = null;

    String indent = "    ";
    Log.printLine();
    Log.printLine("========== OUTPUT ==========");
    Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
        "Data center ID" + indent + "VM ID" + indent + "Time"
        + indent + "Start Time" + indent + "Finish Time" + indent + "Waiting Time");

    double waitTimeSum = 0.0;
        double CPUTimeSum = 0.0;
    int totalValues = 0;
    DecimalFormat dft = new DecimalFormat("###,##");

    double response_time[] = new double[size];

    // Printing all the status of the Cloudlets
    for (int i = 0; i < size; i++) {
      cloudlet = list.get(i);
      Log.print(cloudlet.getCloudletId() + indent + indent);

      if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
        Log.print("SUCCESS");
                CPUTimeSum = CPUTimeSum + cloudlet.getActualCPUTime();
        waitTimeSum = waitTimeSum + cloudlet.getWaitingTime();
        Log.printLine(
            indent + indent + indent + (cloudlet.getResourceId() - 1) + indent + indent + indent + cloudlet.getVmId() +
                indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent
                + dft.format(cloudlet.getExecStartTime()) +
                indent + indent + dft.format(cloudlet.getFinishTime()) + indent + indent + indent
                + dft.format(cloudlet.getWaitingTime()));
        totalValues++;

        response_time[i] = cloudlet.getActualCPUTime();
      }
    }
    DoubleSummaryStatistics stats = DoubleStream.of(response_time).summaryStatistics();

    Log.printLine();
    System.out.println(String.format("min = %,6f",stats.getMin()));
        System.out.println(String.format("Response_Time: %,6f",CPUTimeSum / totalValues));

    Log.printLine();
        Log.printLine(String.format("TotalCPUTime : %,6f",CPUTimeSum));
    Log.printLine("TotalWaitTime : "+waitTimeSum);
    Log.printLine("TotalCloudletsFinished : "+totalValues);

    // Average Cloudlets Finished
        Log.printLine(String.format("AverageCloudletsFinished : %,6f",(CPUTimeSum / totalValues)));

    // Average Start Time
    double totalStartTime = 0.0;
    for (int i = 0; i < size; i++) {
      totalStartTime += cloudletList.get(i).getExecStartTime();
    }
        double avgStartTime = totalStartTime / size;
        System.out.println(String.format("Average StartTime: %,6f",avgStartTime));

    // Average Execution Time
        double ExecTime = 0.0;
    for (int i = 0; i < size; i++) {
            ExecTime += cloudletList.get(i).getActualCPUTime();
    }
        double avgExecTime = ExecTime / size;
        System.out.println(String.format("Average Execution Time: %,6f",avgExecTime));

    // Average Finish Time
    double totalTime = 0.0;
    for (int i = 0; i < size; i++) {
      totalTime += cloudletList.get(i).getFinishTime();
    }
        double avgTAT = totalTime / size;
        System.out.println(String.format("Average FinishTime: %,6f",avgTAT));

    // Average Waiting Time
        double avgWT = cloudlet.getWaitingTime() / size;
        System.out.println(String.format("Average Waiting time: %,6f",avgWT));

    // Throughput
        double maxFT = 0.0;
    for (int i = 0; i < size; i++) {
            double currentFT = cloudletList.get(i).getFinishTime();
            if (currentFT > maxFT) {
                maxFT = currentFT;
            }
        }
        double throughput = size / maxFT;
    System.out.println(String.format("Throughput: %,9f",throughput));

    // Makespan
    double makespan = 0.0;
        double makespan_total = makespan + cloudlet.getFinishTime();
        System.out.println(String.format("Makespan: %,f",makespan_total));

    // Imbalance Degree
        double degree_of_imbalance = (stats.getMax() - stats.getMin()) / (CPUTimeSum / totalValues);
        System.out.println(String.format("Imbalance Degree: %,3f",degree_of_imbalance));

    // Scheduling Length
        double scheduling_length = waitTimeSum + makespan_total;
        Log.printLine(String.format("Total Scheduling Length: %,f", scheduling_length));

    // CPU Resource Utilization
        double resource_utilization = (CPUTimeSum / (makespan_total * 54)) * 100;
        Log.printLine(String.format("Resouce Utilization: %,f",resource_utilization));

    // Energy Consumption
        Log.printLine(String.format("Total Energy Consumption: %,2f  kWh",
            (datacenter1.getPower() + datacenter2.getPower() + datacenter3.getPower() + datacenter4.getPower()
                + datacenter5.getPower() + datacenter6.getPower()) / (3600 * 1000)));
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
