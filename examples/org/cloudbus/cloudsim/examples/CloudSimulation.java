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
    private static int bot = 5;

    public static void main(String[] args) {
      Locale.setDefault(new Locale("en", "US"));
      Log.printLine("Starting Cloud Simulation with Bat Algorithm...");
  
      try {
          int num_user = 1;
          Calendar calendar = Calendar.getInstance();
          boolean trace_flag = false;
  
          BufferedWriter outputWriter = null;
          outputWriter = new BufferedWriter(new FileWriter("filename.txt"));
  
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
//          int cloudletNumber = bot*1000;
           int cloudletNumber = 7395;
  
          vmlist = createVM(brokerId, vmNumber);
          cloudletList = createCloudlet(brokerId, cloudletNumber);
  
          broker.submitVmList(vmlist);
          broker.submitCloudletList(cloudletList);
  
          int cloudletLoopingNumber = cloudletNumber / vmNumber - 1;
  
          for (int cloudletIterator = 0; cloudletIterator <= cloudletLoopingNumber; cloudletIterator++) {
              System.out.println("Cloudlet Iteration Number " + cloudletIterator);
  
              for (int dataCenterIterator = 1; dataCenterIterator <= 6; dataCenterIterator++) {
                  // Parameters for Bat Algorithm
                  int maxIterations = 5; // Maximum number of iterations
                  int populationSize = 15; // Population size (number of bats)
                  double alpha = 1; // Parameter for updating loudness
                  double gamma = 0.5; // Parameter for updating pulse rate
  
                  // Initialize Bat Algorithm
                  BatAlgorithm batAlgorithm = new BatAlgorithm(maxIterations, populationSize, alpha, gamma,
                          cloudletList, vmlist, cloudletNumber);
  
                  // Initialize population
                  System.out.println("Datacenter " + dataCenterIterator + " Population Initialization");
                  PopulationBA population = batAlgorithm.initPopulation(cloudletNumber, dataCenterIterator);
  
                  // Define frequency and initialize loudness and pulse rate
                  batAlgorithm.defineFrequency();
                  batAlgorithm.initLoudnessAndPulseRate();
  
                  // Iteration loop
                  int iteration = 1;
                  while (iteration <= maxIterations) {
                      // Generate new solutions
                      batAlgorithm.generateNewSolutions(population, iteration, dataCenterIterator);

                  // Apply optimized OBL
//                  batAlgorithm.applyOptimizedOBL(population, dataCenterIterator, iteration, maxIterations);
  
                      // Accept new solutions
                      batAlgorithm.acceptNewSolutions(population, dataCenterIterator);
  
                      // Sort bats and find the current best solution
                      batAlgorithm.sortBatsAndFindBest(population, dataCenterIterator);
  
                      System.out.println("Iteration " + iteration + " Best Fitness for DC" + dataCenterIterator
                              + ": " + batAlgorithm.getBestFitnessForDatacenter(dataCenterIterator));
  
                      iteration++;
                  }
  
                  // Get the best solution
                  int[] bestSolution = batAlgorithm.getBestVmAllocationForDatacenter(dataCenterIterator);
  
                  // Assign tasks to VMs based on bestSolution
                  for (int assigner = 0 + (dataCenterIterator - 1) * 9 + cloudletIterator * 54;
                       assigner < 9 + (dataCenterIterator - 1) * 9 + cloudletIterator * 54; assigner++) {
                      int vmId = bestSolution[assigner - (dataCenterIterator - 1) * 9 - cloudletIterator * 54];
                      broker.bindCloudletToVm(assigner, vmId);
                  }
              }
          }
  
          // Start simulation and print results
          CloudSim.startSimulation();
  
          outputWriter.flush();
          outputWriter.close();
  
          List<Cloudlet> newList = broker.getCloudletReceivedList();
  
          CloudSim.stopSimulation();
  
          printCloudletList(newList);
  
          Log.printLine("Cloud Simulation with Bat Algorithm finished!");
      } catch (Exception e) {
          e.printStackTrace();
          Log.printLine("Simulation terminated due to an error");
      }
  }

  private static List<Cloudlet> createCloudlet(int userId, int cloudlets) {
    ArrayList<Double> randomSeed = getSeedValue(cloudlets);

    LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

    long fileSize = 300;  // Ukuran file input Cloudlet dalam satuan MB
    long outputSize = 300;  // Ukuran file output Cloudlet dalam MB
    int pesNumber = 1;  // Jumlah CPU yang digunakan
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

// Method untuk membuat daftar VM (Virtual Machine) berdasarkan jumlah yang ditentukan
private static List<Vm> createVM(int userId, int vms) {
    // Membuat LinkedList untuk menyimpan daftar VM yang akan dibuat
    LinkedList<Vm> list = new LinkedList<Vm>();

    long size = 10000;  // Menentukan ukuran penyimpanan setiap VM dalam MB
    int[] ram = { 512, 1024, 2048 };  // Array yang menyimpan variasi kapasitas RAM (dalam MB) yang bisa digunakan oleh VM
    int[] mips = { 400, 500, 600 }; // Array yang menyimpan variasi nilai MIPS (Million Instructions Per Second) untuk VM
    long bw = 1000; // Menentukan bandwidth (BW) setiap VM dalam MBps
    int pesNumber = 1;  // Menentukan jumlah Processing Elements (PEs) per VM
    String vmm = "Xen"; // Menentukan jenis Virtual Machine Monitor (VMM) yang digunakan, dalam hal ini "Xen"
    Vm[] vm = new Vm[vms];  // Array untuk menyimpan VM yang akan dibuat

    // Loop untuk membuat dan menambahkan VM ke dalam daftar
    for (int i = 0; i < vms; i++) {
        // Membuat VM dengan parameter tertentu:
        // - ID VM (i)
        // - ID pengguna (userId)
        // - MIPS berdasarkan indeks `i` (dengan pola berulang 400, 500, 600)
        // - Jumlah PE (1)
        // - RAM berdasarkan indeks `i` (dengan pola berulang 512, 1024, 2048)
        // - Bandwidth (1000 MBps)
        // - Storage (10000 MB)
        // - Jenis VMM ("Xen")
        // - Menggunakan scheduler berbasis Space Shared untuk Cloudlet
        vm[i] = new Vm(i, userId, mips[i % 3], pesNumber, ram[i % 3], bw, size, vmm, new CloudletSchedulerSpaceShared());

        // Menambahkan VM yang telah dibuat ke dalam List
        list.add(vm[i]);
    }

    // Mengembalikan daftar VM yang telah dibuat
    return list;
}


  private static ArrayList<Double> getSeedValue(int cloudletcount) {
    ArrayList<Double> seed = new ArrayList<Double>();
    try {
//       File fobj = new File(System.getProperty("user.dir") + "/cloudsim-3.0.3/datasets/randomSimple/randomSimple_"+bot+"000.txt");
//        File fobj = new File(System.getProperty("user.dir") + "/cloudsim-3.0.3/datasets/randomStratified/randomStratified_"+bot+"000.txt");
        File fobj = new File(System.getProperty("user.dir") + "/cloudsim-3.0.3/datasets/SDSC/SDSC7395.txt");
      java.util.Scanner readFile = new java.util.Scanner(fobj);

      while (readFile.hasNextLine() && cloudletcount > 0) {
        seed.add(readFile.nextDouble());
        cloudletcount--;
      }
      readFile.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
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

    int ram = 128000; // Kapasitas memori (RAM) Host dalam satuan MB
    long storage = 1000000; // Kapasitas penyimpanan Host dalam satuan MB
    int bw = 10000; // Bandwith Host dalam satuan Mbps
    int maxpower = 117; // Daya maksimum Host dalam satuan Watt
    int staticPowerPercentage = 50; // Host Static Power Percentage

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

    String arch = "x86";  // Jenis arsitektur sistem
    String os = "Linux";  // Jenis sistem operasi
    String vmm = "Xen"; // Jenis Virtual Machine Manager (VMM)
    double time_zone = 10.0;  // Zona waktu lokasi Host
    double cost = 3.0;  // Biaya penggunaan processor
    double costPerMem = 0.05; // Biaya penggunaan memori (RAM)
    double costPerStorage = 0.1;  // Biaya penggunaan penyimpanan (Storage)
    double costPerBw = 0.1; // Biaya penggunaan bandwith
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
    double totalCPUTime = 0.0;
    int totalValues = 0;
    DecimalFormat dft = new DecimalFormat("###,##");

    double response_time[] = new double[size];

    // Printing all the status of the Cloudlets
    for (int i = 0; i < size; i++) {
      cloudlet = list.get(i);
      Log.print(cloudlet.getCloudletId() + indent + indent);

      if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
        Log.print("SUCCESS");
        totalCPUTime = totalCPUTime + cloudlet.getActualCPUTime();
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
    System.out.println(String.format("Response_Time: %,6f",totalCPUTime / totalValues));

    Log.printLine();
    Log.printLine(String.format("TotalCPUTime : %,6f",totalCPUTime));
    Log.printLine("TotalWaitTime : "+waitTimeSum);
    Log.printLine("TotalCloudletsFinished : "+totalValues);

    // Average Cloudlets Finished
    Log.printLine(String.format("AverageCloudletsFinished : %,6f",(totalCPUTime / totalValues)));

    // Average Start Time
    double totalStartTime = 0.0;
    for (int i = 0; i < size; i++) {
      totalStartTime += cloudletList.get(i).getExecStartTime();
    }
    double averageStartTime = totalStartTime / size;
    System.out.println(String.format("Average Start Time: %,6f",averageStartTime));

    // Average Execution Time
    double executionTime = 0.0;
    for (int i = 0; i < size; i++) {
      executionTime += cloudletList.get(i).getActualCPUTime();
    }
    double averageExecutionTime = executionTime / size;
    System.out.println(String.format("Average Execution Time: %,6f",averageExecutionTime));

    // Average Finish Time
    double totalTime = 0.0;
    for (int i = 0; i < size; i++) {
      totalTime += cloudletList.get(i).getFinishTime();
    }
    double averageFinishTime = totalTime / size;
    System.out.println(String.format("Average Finish Time: %,6f",averageFinishTime));

    // Average Waiting Time
    double averageWaitingTime = cloudlet.getWaitingTime() / size;
    System.out.println(String.format("Average Waiting time: %,6f",averageWaitingTime));

    // Throughput
    double maxMakespan = 0.0;
    for (int i = 0; i < size; i++) {
      double currentMakespan = cloudletList.get(i).getFinishTime();
      if (currentMakespan > maxMakespan) {
        maxMakespan = currentMakespan;
      }
    }
    double throughput = size / maxMakespan;
    System.out.println(String.format("Throughput: %,9f",throughput));

    // Makespan
    double makespan = 0.0;
    double totalMakespan = makespan + cloudlet.getFinishTime();
    System.out.println(String.format("Makespan: %,f",totalMakespan));

    // Imbalance Degree
    double imbalanceDegree = (stats.getMax() - stats.getMin()) / (totalCPUTime / totalValues);
    System.out.println(String.format("Imbalance Degree: %,3f",imbalanceDegree));

    // Scheduling Length
    double schedulingLength = waitTimeSum + totalMakespan;
    Log.printLine(String.format("Total Scheduling Length: %,f", schedulingLength));

    // CPU Resource Utilization
    double resourceUtilization = (totalCPUTime / (totalMakespan * 54)) * 100;
    Log.printLine(String.format("Resouce Utilization: %,f",resourceUtilization));

    // Energy Consumption
    double totalEnergyConsumption = (datacenter1.getPower() + datacenter2.getPower() + datacenter3.getPower() + datacenter4.getPower() + datacenter5.getPower() + datacenter6.getPower())   / (3600 * 1000);

    Log.printLine(String.format("Total Energy Consumption: %,2f kWh", totalEnergyConsumption));
    // Log.printLine(String.format("Total Energy Consumption: %,2f  kWh",
    //     (datacenter1.getPower() + datacenter2.getPower() + datacenter3.getPower() + datacenter4.getPower()
    //         + datacenter5.getPower() + datacenter6.getPower()) / (3600 * 1000)));
  }

}
