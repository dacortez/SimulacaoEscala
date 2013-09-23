package br.com.asasoftware.simuladorEscala;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Simulator {
	private final static int MIN_JOB_SIZE = 120;
	private final static int MAX_JOB_SIZE = 2300;
	private final static int MEAN_JOB_SIZE = 1200;
	private final static int STDV_JOB_SIZE = 350;
	private final static double REJECTION_PROBALITY = 0.25;
	private List<Job> jobs;
	private PriorityQueue<Machine> machines;
	
	public static void main(String[] args) {
		int totalJobs, totalMachines;
		char kind;
		if (args.length == 3) {
			totalJobs = Integer.parseInt(args[0]);
			totalMachines = Integer.parseInt(args[1]);
			kind = args[2].charAt(0);
		} else {
			totalJobs = 10000;
			totalMachines = 1000;
			kind = 'N';
		}
		simulate(totalJobs, totalMachines, kind);
	}

	private static void simulate(int totalJobs, int totalMachines, char kind) {
		for (int i = 1; i <= 1; i++) {
			Simulator sim = new Simulator();
			sim.setJobs(totalJobs, kind);
			sim.setMachines(totalMachines);
			try {
				sim.doAssingnments();
				//sim.printSolution(null);
				//sim.printSolution(null);
				sim.printStats(null);
				//sim.printJobsLengths(null);
				sim.printMachinesOccupations(null);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void setJobs(int totalJobs, char kind) {
		jobs = new ArrayList<Job>();
		for (int i = 1; i <= totalJobs; i++)
			jobs.add(new Job(i, getJobLength(kind)));
	}
	
	private int getJobLength(char kind) {
		Random r = new Random();
		switch (kind) {
		case 'U':
			return MIN_JOB_SIZE + r.nextInt(MAX_JOB_SIZE);
		case 'N':
			int val = (int) Math.round(MEAN_JOB_SIZE + r.nextGaussian() * STDV_JOB_SIZE);
			if (val < MIN_JOB_SIZE) 
				return MIN_JOB_SIZE;
			return val;
		}
		return MIN_JOB_SIZE;
	}
	
	public void setMachines(int totalMachines) {
		setupQueue(totalMachines);
		List<Integer> ids = getIdsList(totalMachines);
		Random r = new Random();
		for (int i = 1; i <= totalMachines; i++) {
			Integer id = ids.get(r.nextInt(ids.size()));
			ids.remove(id);
			machines.add(new Machine(id));
		}
	}

	private void setupQueue(int totalMachines) {
		machines = new PriorityQueue<Machine>(totalMachines, new Comparator<Machine>() {  
            public int compare(Machine m1, Machine m2) {
            	return m1.getOccupation() < m2.getOccupation() ? -1 : 1;
            }  
        });
	}

	private List<Integer> getIdsList(int totalMachines) {
		List<Integer> ids = new ArrayList<Integer>();
		for (Integer i = 1; i <= totalMachines; i++)
			ids.add(i);
		return ids;
	}
	
	public void doAssingnments() throws Exception {
		Random r = new Random();
		List<Machine> rejectedMachines = new ArrayList<Machine>();
		sortJobs();	
		for (Job job: jobs) {
			Machine machine = machines.poll();
			while (r.nextDouble() < REJECTION_PROBALITY) {
				rejectedMachines.add(machine);
				machine = machines.poll();
				if (machine == null)
					throw new Exception("Inst‰ncia invi‡vel.");
			}
			machine.assign(job);
			machines.add(machine);
			machines.addAll(rejectedMachines);
			rejectedMachines.clear();
		}
	}
	
	private void sortJobs() {
		Collections.sort(jobs, new Comparator<Job>() {  
            public int compare(Job j1, Job j2) {
            	return j1.getLength() < j2.getLength() ? 1 : -1;
            }  
        });
	}
	
	public void printSolution(String file) {
		PrintStream ps  = getPrintStream(file);
		ps.println("Total jobs = " + jobs.size());
		for (Job job: jobs)
			ps.println(job);
		ps.println("Total machines = " + machines.size());
		for (Machine machine: machines)
			ps.println(machine);
		ps.close();
	}
	
	public void printMachinesQueue() {
		System.out.println("---------------------------------");
		for (Machine m: machines)
			System.out.println(m);
		System.out.println("---------------------------------");
	}
	
	public void printStats(String file) {
		PrintStream ps  = getPrintStream(file);
		int m = jobs.size();
		int n = machines.size();
		int[] lengths = getJobsLengths();
		int[] occupations = getMachinesOccupations();
		int min = machines.element().getOccupation();
		int max = getMaxOccupation();
		double meanLength = getMean(lengths);
		double stdevLength = getStdev(meanLength, lengths);
		double meanOccupation = getMean(occupations);
		double stdevOccupation = getStdev(meanOccupation, occupations);
		double percent = 100.0 * stdevOccupation / meanOccupation;
		DecimalFormat df = new DecimalFormat("0.000");
		ps.println(m + " " + n + " " 
			+ df.format(meanLength) + " " + df.format(stdevLength) + " " 
			+ df.format(meanOccupation) + " " + df.format(stdevOccupation) + " " 
			+ df.format(percent) + " " + min + " " + max);
	}

	private int[] getJobsLengths() {
		int[] lengths = new int[jobs.size()];
		int i = 0;
		for (Job job: jobs)
			lengths[i++] = job.getLength();
		return lengths;
	}
	
	private int[] getMachinesOccupations() {
		int[] occupations = new int[machines.size()];
		int i = 0;
		for (Machine machine: machines)
			occupations[i++] = machine.getOccupation();
		return occupations;
	}
	
	private int getMaxOccupation() {
		int max = -1;
		for (Machine machine: machines)
			if (machine.getOccupation() > max) 
				max = machine.getOccupation();
		return max;
	}
	
	private double getMean(int[] x) {
		double sum = 0.0;
		for (int xi: x)
			sum += (double) xi;
		return sum / x.length;
	}
	
	private double getStdev(double xMean, int[] x) {
		double sum = 0.0;
		for (int xi: x)
			sum += (double) (xi - xMean) * (xi - xMean);
		return Math.sqrt(sum / x.length);
	}
	
	public void printJobsLengths(String file) {
		PrintStream ps  = getPrintStream(file);
		ps.println("len");
		for (Job job: jobs)
			ps.println(job.getLength());
	}

	public void printMachinesOccupations(String file) {
		PrintStream ps  = getPrintStream(file);
		List<Machine> sortedMachines = getMachinesSortedById();
		ps.println("ocp");
		for (Machine machine: sortedMachines)
			ps.println(machine.getOccupation());
	}
	
	private List<Machine> getMachinesSortedById() {
		List<Machine> list = new ArrayList<Machine>(machines);
		Collections.sort(list, new Comparator<Machine>() {  
            public int compare(Machine m1, Machine m2) {
            	return m1.getId() < m2.getId() ? -1 : 1;
            }  
        });
		return list;
	}
	
	private PrintStream getPrintStream(String file) {
		if (file == null) return System.out;
		try {
			return new PrintStream(new File(file));
		} catch (FileNotFoundException e) {
			return System.out;
		}
	}
}
