package heuristic;

public class Gene implements Comparable<Object>{
	private int fitness;
	private boolean[] bitGene; 

	
	public Gene(boolean[] bitGene, int fitness){
		this.bitGene = bitGene;
		this.fitness = fitness;
	}
	
	public int getFitness(){
		return fitness;
	}
	
	public boolean[] getBitGene(){
		return bitGene;
	}
	
	public int compareTo(Object obj){
		if(this.getFitness() < ((Gene) obj).getFitness()){
			return -1;
		}
		if(this.getFitness() == ((Gene) obj).getFitness()){
			return 0;
		}
		if(this.getFitness() > ((Gene) obj).getFitness()){
			return 1;
		}
		return 0;
	}

}
