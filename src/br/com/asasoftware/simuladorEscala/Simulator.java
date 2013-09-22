package br.com.asasoftware.simuladorEscala;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Simulator {
	private final static int MIN_JOB_SIZE = 100;
	private final static int MAX_JOB_SIZE = 3000;
	private final static int MEAN_JOB_SIZE = 1500;
	private final static int STDV_JOB_SIZE = 400;
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
		Simulator sim = new Simulator();
		sim.setJobs(totalJobs, kind);
		sim.setMachines(totalMachines);
		sim.doAssingnments();
		sim.printStats();
	}
	
	public void setJobs(int totalJobs, char kind) {
		jobs = new ArrayList<Job>();
		for (int i = 1; i <= totalJobs; i++)
			jobs.add(new Job(i, getLength(kind)));
	}
	
	private int getLength(char kind) {
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
		machines = new PriorityQueue<Machine>(totalMachines, new Comparator<Machine>() {  
            public int compare(Machine m1, Machine m2) {
            	return m1.getOccupation() < m2.getOccupation() ? -1 : 1;
            }  
        });	
		for (int i = 1; i <= totalMachines; i++)
			machines.add(new Machine(i));
	}
	
	public void doAssingnments() {
		sortJobs();	
		for (Job job: jobs) {
			Machine machine = machines.poll();
			machine.assign(job);
			machines.add(machine);
		}
	}
	
	private void sortJobs() {
		Collections.sort(jobs, new Comparator<Job>() {  
            public int compare(Job j1, Job j2) {
            	return j1.getLength() < j2.getLength() ? 1 : -1;
            }  
        });
	}
	
	public void printSolution() {
		System.out.println("Total jobs = " + jobs.size());
		for (Job job: jobs)
			System.out.println(job);
		System.out.println("Total machines = " + machines.size());
		for (Machine machine: machines)
			System.out.println(machine);
	}
	
	public void printStats() {
		int m = jobs.size();
		int n = machines.size();
		int[] lengths = new int[m];
		int[] occupations = new int[n];
		int i = 0;
		for (Job job: jobs)
			lengths[i++] = job.getLength();
		i = 0;
		int min = machines.element().getOccupation();
		int max = -1;
		for (Machine machine: machines) {
			occupations[i++] = machine.getOccupation();
			if (machine.getOccupation() > max) 
				max = machine.getOccupation();
		}
		int totalLength = 0;
		for (Job job: jobs)
			totalLength += job.getLength();
		double meanLength = getMean(lengths);
		double stdevLength = getStdev(meanLength, lengths);
		double meanOccupation = getMean(occupations);
		double stdevOccupation = getStdev(meanOccupation, occupations);
		double percent = 100.0 * stdevOccupation / meanOccupation;
		DecimalFormat df = new DecimalFormat("0.000");
		System.out.println(m + " " + n + " " + totalLength + " " 
			+ df.format(meanLength) + " " + df.format(stdevLength) + " " 
			+ df.format(meanOccupation) + " " + df.format(stdevOccupation) + " " 
			+ df.format(percent) + " " + min + " " + max);
		
		for (Machine machine: machines) {
			System.out.println(machine.getOccupation());
		}
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
}
