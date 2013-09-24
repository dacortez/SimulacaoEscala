package br.com.asasoftware.simuladorEscala;

public class Job {
	private int id;
	private int length;
	
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
	
	public Job(int id, int length) {
		this.id = id;
		this.length = length;
	}
	
	@Override
	public String toString() {
		return "J" + id + ": " + length;
	}
}
