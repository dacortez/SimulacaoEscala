package br.com.asasoftware.simuladorEscala;

public class Main {
	private static final int TRIALS = 10;
	
	public static void main(String[] args) {
		try {
			//varXMachines(1000, 10000, 200, 'U', 0.95);
			//varXMachines(1000, 10000, 200, 'N', 0.95);
			//varXRejection(10000, 1000, 0.05, 'U');
			//varXRejection(10000, 1000, 0.05, 'N');
			//jobsLengths(10000, 1000, 'U', 0.00);
			//jobsLengths(10000, 1000, 'N', 0.00);
			//machinesOccupation(10000, 1000, 'U', 0.00);
			//machinesOccupation(10000, 1000, 'U', 0.95);
			//machinesOccupation(10000, 1000, 'N', 0.00);
			//machinesOccupation(10000, 1000, 'N', 0.95);
			//machinesOccupationOneYear(10000, 1000, 'U', 0.00);
			//machinesOccupationOneYear(10000, 1000, 'U', 0.95);
			//machinesOccupationOneYear(10000, 1000, 'N', 0.00);
			machinesOccupationOneYear(10000, 1000, 'N', 0.95);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void varXMachines(
			int totalMachines, 
			int maxJobs, 
			int step, 
			char kind, 
			double rejectionProbability
		) throws Exception {
		for (int totalJobs = 3 * totalMachines; totalJobs <= maxJobs; totalJobs += step) {
			Simulator sim = new Simulator(totalJobs, totalMachines, kind, rejectionProbability);
			double[] vars =  new double[TRIALS];
			for (int i = 0; i < TRIALS; i++) {
				sim.simulate();
				vars[i] = sim.getVarOccupation();
			}
			double meanVar = Stats.getMean(vars);
			double stdvVar = Stats.getStdev(meanVar, vars);
			System.out.println(totalJobs + "\t" + meanVar + "\t" + stdvVar);
		}
	}
	
	public static void varXRejection(
			int totalJobs, 
			int totalMachines,
			double step,
			char kind
		) throws Exception {
		for (double alpha = 0.0; alpha <= 0.97; alpha += step) {
			Simulator sim = new Simulator(totalJobs, totalMachines, kind, alpha);
			double[] vars =  new double[TRIALS];
			for (int i = 0; i < TRIALS; i++) {
				sim.simulate();
				vars[i] = sim.getVarOccupation();
			}
			double meanVar = Stats.getMean(vars);
			double stdvVar = Stats.getStdev(meanVar, vars);
			System.out.println(alpha + "\t" + meanVar + "\t" + stdvVar);
		}
	}
	
	public static void jobsLength(
			int totalJobs, 
			int totalMachines,
			char kind,
			double rejectionProbability
		) throws Exception {
		Simulator sim = new Simulator(totalJobs, totalMachines, kind, rejectionProbability);
		sim.simulate();
		sim.printJobsLength();
	}
	
	public static void machinesOccupation(
			int totalJobs, 
			int totalMachines, 
			char kind,
			double rejectionProbability
		) throws Exception {
		Simulator sim = new Simulator(totalJobs, totalMachines, kind, rejectionProbability);
		sim.simulate();
		sim.printMachinesOccupation();;
	}
	
	public static void machinesOccupationOneYear(
			int totalJobs, 
			int totalMachines, 
			char kind,
			double rejectionProbability
		) throws Exception {
		for (int mounth = 1; mounth <= 12; mounth++) {
			Simulator sim = new Simulator(totalJobs, totalMachines, kind, rejectionProbability);
			sim.simulate();
			sim.printMachinesOccupation("data/machines_" + mounth + ".dat");
		}
	}
}
