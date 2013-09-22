package br.com.asasoftware.simuladorEscala;

import java.util.ArrayList;
import java.util.List;

public class Machine {
	int id;
	private List<Job> jobs;
	int occupation;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}
	
	public int getOccupation() {
		return occupation;
	}
	
	public Machine(int id) {
		this.id = id;
		jobs = new ArrayList<Job>();
		occupation = 0;
	}
	
	public void assign(Job job) {
		jobs.add(job);
		occupation += job.getLength();
	}
	
	@Override
	public String toString() {
		return "M" + id + ": " + occupation;
	}
}
