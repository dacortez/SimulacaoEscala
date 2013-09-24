package br.com.asasoftware.simuladorEscala;

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
	private int totalJobs;
	private int totalMachines;
	private char kind;
	private double rejectionProbability;
	private List<Job> jobs;
	private PriorityQueue<Machine> machines;
	
	public int getTotalJobs() {
		return totalJobs;
	}

	public void setTotalJobs(int totalJobs) {
		this.totalJobs = totalJobs;
	}

	public int getTotalMachines() {
		return totalMachines;
	}

	public void setTotalMachines(int totalMachines) {
		this.totalMachines = totalMachines;
	}

	public char getKind() {
		return kind;
	}

	public void setKind(char kind) {
		this.kind = kind;
	}
	
	public double getRejectionProbability() {
		return rejectionProbability;
	}

	public void setRejectionProbability(double rejectionProbability) {
		this.rejectionProbability = rejectionProbability;
	}
	
	public Simulator(int totalJobs, int totalMachines, char kind, double rejectionProbability) {
		this.totalJobs = totalJobs;
		this.totalMachines = totalMachines;
		this.kind = kind;
		this.rejectionProbability = rejectionProbability;
	}
	
	public void simulate() throws Exception {
		setJobs();
		setMachines();
		doAssingnments();
	}

	private void setJobs() {
		jobs = new ArrayList<Job>();
		for (int i = 1; i <= totalJobs; i++)
			jobs.add(new Job(i, getJobLength()));
	}
	
	private int getJobLength() {
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
	
	private void setMachines() {
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
	
	private void doAssingnments() throws Exception {
		Random r = new Random();
		List<Machine> rejectedMachines = new ArrayList<Machine>();
		sortJobs();	
		for (Job job: jobs) {
			Machine machine = machines.poll();
			while (r.nextDouble() < rejectionProbability) {
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
		System.out.println("Total jobs = " + jobs.size());
		for (Job job: jobs)
			System.out.println(job);
		System.out.println("Total machines = " + machines.size());
		for (Machine machine: machines)
			System.out.println(machine);
	}
	
	public void printJobsLengths(String file) {
		System.out.println("len");
		for (Job job: jobs)
			System.out.println(job.getLength());
	}

	public void printMachinesOccupations(String file) {
		List<Machine> sortedMachines = getMachinesSortedById();
		System.out.println("ocp");
		for (Machine machine: sortedMachines)
			System.out.println(machine.getOccupation());
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
	
	public void printMachinesQueue(String file) {
		System.out.println("---------------------------------");
		for (Machine m: machines)
			System.out.println(m);
		System.out.println("---------------------------------");
	}
	
	public void printStats(String file) {
		int min = getMinOccupation();
		int max = getMaxOccupation();
		double meanLength = getMeanLength();
		double stdevLength = getStdevLength();
		double meanOccupation = getMeanOccupation();
		double stdevOccupation = getStdevOccupation();
		double var = getVarOccupation();
		DecimalFormat df = new DecimalFormat("0.000");
		System.out.println(totalJobs + " " + totalMachines + " " 
			+ df.format(meanLength) + " " + df.format(stdevLength) + " " 
			+ df.format(meanOccupation) + " " + df.format(stdevOccupation) + " " 
			+ df.format(var) + " " + min + " " + max);
	}
	
	public double getStdevLength() {
		int[] jobsLengths = getJobsLengths();
		double meanLength = Stats.getMean(jobsLengths);
		return Stats.getStdev(meanLength, jobsLengths);
	}
	
	public double getMeanLength() {
		return Stats.getMean(getJobsLengths());
	}
	
	public double getStdevOccupation() {
		int[] machinesOccupations = getMachinesOccupations();
		double meanOccupation = Stats.getMean(machinesOccupations);
		return Stats.getStdev(meanOccupation, machinesOccupations);
	}
	
	public double getMeanOccupation() {
		return Stats.getMean(getMachinesOccupations());
	}
	
	public double getVarOccupation() {
		int[] machinesOccupations = getMachinesOccupations();
		double meanOccupation = Stats.getMean(machinesOccupations);
		double stdvOccupation = Stats.getStdev(meanOccupation, machinesOccupations);
		return 100.0 * stdvOccupation / meanOccupation;
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
	
	private int getMinOccupation() {
		return machines.element().getOccupation();
	}
	
	private int getMaxOccupation() {
		int max = -1;
		for (Machine machine: machines)
			if (machine.getOccupation() > max) 
				max = machine.getOccupation();
		return max;
	}
}
