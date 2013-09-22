package br.com.asasoftware.simuladorEscala;

public class Job {
	private int id;
	private int length;
	private Machine assignedMachine;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public Machine getAssignedMachine() {
		return assignedMachine;
	}

	public void setAssignedMachine(Machine assignedMachine) {
		this.assignedMachine = assignedMachine;
	}

	public Job(int id, int length) {
		this.id = id;
		this.length = length;
		assignedMachine = null;
	}
	
	public boolean isAssigned() {
		return (assignedMachine != null);
	}
	
	@Override
	public String toString() {
		return "J" + id + ": " + length;
	}
}
