package heuristic;


import geometry.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class GeneticSearch extends Search {

	Polygon polygon;
	public Gene bestGene;
	public Gene[] population;
	public double mutation;
	ArrayList<Point2d>[] visibility;
	int[][] visList;
	boolean debug = true;
	
	public GeneticSearch(Polygon polygon, ArrayList<Point2d>[] visibility){
		this.polygon = polygon;
		this.visibility = visibility;
		this.population = new Gene[150]; // new Gene[polygon.size()/2];
		this.mutation = 0.03/polygon.size();
		boolean[] tempGene = new boolean[polygon.size()];
		for(int i = 0; i < tempGene.length; i++){
			tempGene[i] = true;
		}
		
		visList = new int[visibility.length][];
		for(int i = 0; i<visibility.length; i++){
			visList[i] = new int[visibility[i].size()];
			for(int j = 0; j < visibility[i].size(); j++){
				visList[i][j] = visibility[i].get(j).getID();
			}
			Arrays.sort(visList[i]);
		}
		int cornerCount = 0;
		int maxCount = 0;
		for(int i = 0; i <polygon.size(); i++){
			if(!rightTurn(polygon.get((i+0)%polygon.size()), polygon.get((i+1)%polygon.size()), polygon.get((i+2)%polygon.size()))){
				cornerCount++;
			} else {
				cornerCount =0;
			}
			if(cornerCount> maxCount){
				maxCount = cornerCount;
			}
		}
		System.out.println(maxCount);
		this.bestGene = new Gene(tempGene, fitness(tempGene));
		
		//Gene temp = fixGreedyGene(3,generateRandomSeed());
		//System.out.println("Rand fit:" + temp.getFitness());
		
		
		
	}
	
	@Override
	public ArrayList<Point2d> getCurrentSolution() {
		ArrayList<Point2d> solution = new ArrayList<Point2d>();
		boolean[] bitGene = bestGene.getBitGene();	
		
		for(int i = 0; i < polygon.upper.size(); i++){
			if(bitGene[i]){
				solution.add(polygon.upper.get(i));
			}
		}
		
		for(int i = 0; i < polygon.lower.size(); i++){
			if(bitGene[polygon.upper.size() + i]){
				solution.add(polygon.lower.get(i));
			}
		}
		
		return solution;
	}
	
	private int genCount;
	public void run(){
		population[0] = bestGene;
		
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < population.length; i++){
			if(population[i] == null){
				population[i] = randGreedyGene(5);
				if(population[i].getFitness() <bestGene.getFitness()){
					bestGene = population[i];
				}
			}
		}
		long endTime = System.currentTimeMillis();

		long duration = (endTime - startTime);
		
		if(debug){
			System.out.println("Completed in:"+ (duration/1000));
		}
		
		genCount = 0;
		int noLossCount = 0;
		while(noLossCount < 10){
			if(debug){
				System.out.println("Gen " + genCount++);
			} else {
				genCount++;
			}
			//Sorts the Genes from population based on fitness(asc)
			Arrays.sort(population);
			if(population[0].getFitness() < bestGene.getFitness()){
				bestGene = population[0];
				noLossCount = 0;
			} else{
				noLossCount++;
			}
			if(debug){
				System.out.println("\tBest:"+bestGene.getFitness());
			}
			
			// Removes the weakest genes and are replaced by the offspring of better ones.
			int unselectedSize = population.length/10;
			for(int i = population.length - unselectedSize; i<population.length; i++){
				if(Math.random() <.97){
					population[i] = this.mate(population[parentIndex(true, i)], population[parentIndex(false,i )]);
				} 
			}
		}
		if(debug){
			System.out.println("Switch to neighborhood...");
		}
		Gene neighborhoodSearch = new Gene(population[0].getBitGene().clone(), population[0].getFitness());
		neighborhoodSearch = improveGene(neighborhoodSearch, 22);
		while(true){
			if(debug){
				System.out.println("Gen " + genCount++);
			} else {
				genCount++;
			}
			
			if(neighborhoodSearch.getFitness() <= bestGene.getFitness()){
				bestGene = neighborhoodSearch;
			}
			if(debug){
				System.out.println("\tBest:"+bestGene.getFitness());
			}
			Gene neighborhoodAttempt = worsenGene(neighborhoodSearch, 20);
			neighborhoodAttempt = improveGene(neighborhoodAttempt, 22);
			if(neighborhoodAttempt.getFitness()<neighborhoodSearch.getFitness()){
				neighborhoodSearch = neighborhoodAttempt;
			}
			
		}
	}
	
	public int fitness(boolean[] bitGene){
		int retVal = 0;
		
		ArrayList<Point2d> solution = new ArrayList<Point2d>();
		for(int i = 0; i < polygon.upper.size(); i++){
			if(bitGene[i]){
				solution.add(polygon.upper.get(i));
				retVal++;
			}
		}
		
		for(int i = 0; i < polygon.lower.size(); i++){
			if(bitGene[polygon.upper.size() + i]){
				solution.add(polygon.lower.get(i));
				retVal++;
			}
		}
		
		if(polygon.isSatisfied(visibility, solution)){
			return retVal;
		} else {
			return Integer.MAX_VALUE;
		}
	}
	
	private Gene naiveSeed(int shift){
		boolean[] tempGene = new boolean[polygon.size()];
		for(int i = 0; i < polygon.upper.size(); i++){
			if(i%3 == shift){
				tempGene[i] = true;
			} else {
				tempGene[i] = false;
			}
		}
		
		for(int i = 0; i < polygon.lower.size(); i++){
			if((i%3) == (2-shift)){
				tempGene[polygon.upper.size() + i] = true;
			} else {
				tempGene[polygon.upper.size() + i] = false;
			}
		}
		
		return new Gene(tempGene, fitness(tempGene));
	}
	
	@SuppressWarnings("unchecked")
	private Gene greedyGene(){
		boolean[] myBitGene = new boolean[polygon.size()];
		ArrayList<Point2d>[] myVis = new ArrayList[polygon.size()];
		for(int i = 0; i<myVis.length; i++){
			myVis[i] = (ArrayList<Point2d>) visibility[i].clone();
		}
		//ArrayList<Point2d> mySolution = new ArrayList<Point2d>();
		while(fitness(myBitGene) > polygon.size()){
			int mostVisibleIndex = -1;
			int mostVisibleValue = -1;
			for(int i = 0; i < myVis.length; i++){
				if(myVis[i].size() >= mostVisibleValue){
					mostVisibleIndex = i;
					mostVisibleValue = myVis[i].size();
				}
			}
			myBitGene[mostVisibleIndex] = true;
			ArrayList<Point2d> removedPoints = (ArrayList<Point2d>) myVis[mostVisibleIndex].clone();
			//myVis[mostVisibleIndex]  = new ArrayList<Point2d>();
			for(int i = 0; i < myVis.length; i++){
					myVis[i].removeAll(removedPoints);
			}
		}
		return new Gene(myBitGene, fitness(myBitGene));		
	}
	
	@SuppressWarnings("unchecked")
	private Gene randGreedyGene(int randSize){
		boolean[] myBitGene = new boolean[polygon.size()];
		ArrayList<Point2d>[] myVis = new ArrayList[polygon.size()];
		for(int i = 0; i<myVis.length; i++){
			myVis[i] = (ArrayList<Point2d>) visibility[i].clone();
		}
		//ArrayList<Point2d> mySolution = new ArrayList<Point2d>();
		while(fitness(myBitGene) > polygon.size()){
			int[] indexes = new int[randSize];
			int[] values = new int[randSize];
			for(int i = 0; i < randSize; i++){
				indexes[i] = -1;
				values[i] = -1;
			}
			
			
			for(int i = 0; i < myVis.length; i++){
				for(int j = 0; j < randSize; j++){
					if(myVis[i].size() >= values[j]){
						for(int k =j+1; k<randSize; k++){
							indexes[k] = indexes[j];
							values[k] = values[j];
						}
						
						indexes[j] = i;
						values[j] = myVis[i].size();
					}
				}
				
			}
			int randNumber = (int) (Math.random()*randSize);
			int mostVisibleIndex = indexes[randNumber];
			
			
			myBitGene[mostVisibleIndex] = true;
			ArrayList<Point2d> removedPoints = (ArrayList<Point2d>) myVis[mostVisibleIndex].clone();
			for(int i = 0; i < myVis.length; i++){
					myVis[i].removeAll(removedPoints);
			}
		}
		return new Gene(myBitGene, fitness(myBitGene));		
	}
	
	private Gene recursiveRemove(Gene gene, int depth){
		if(depth <= 0 || gene.getFitness() > polygon.size()){
			return gene;
		}
		boolean[] bitGene =  gene.getBitGene();
		ArrayList<Gene> children = new ArrayList<Gene>();
		int randStart = (int) (Math.random()*(bitGene.length));
		int minChildSize = 2;
		for(int i = randStart; i<bitGene.length; i++){
			if(bitGene[i]){
				if(children.size()>minChildSize){
					break;
				}
				boolean[] myBitGene = bitGene.clone();
				myBitGene[i] = false;
				Gene myGene = new Gene(myBitGene, fitness(myBitGene));
				if(myGene.getFitness() <  gene.getFitness()){
					//return recursiveRemove(myGene, depth-1);
					children.add(recursiveRemove(myGene, depth-1));
				}
			}
		}
		
		for(int i = 0; i<randStart; i++){
			if(bitGene[i]){
				if(children.size()>minChildSize){
					break;
				}
				boolean[] myBitGene = bitGene.clone();
				myBitGene[i] = false;
				Gene myGene = new Gene(myBitGene, fitness(myBitGene));
				if(myGene.getFitness() <  gene.getFitness()){
					//return recursiveRemove(myGene, depth-1);
					children.add(recursiveRemove(myGene, depth-1));
				}
			}
		}
		
		if(children.size()==0){
			return gene;
		}
		Collections.sort(children);
		return children.get(0);
	}
	
	private Gene improveGene(Gene gene, int remSize){
		int initialFitness = gene.getFitness();
		int targetFitness = (initialFitness) - remSize;
		
		int lastFitness = gene.getFitness();
		for(int i = 0; i<(initialFitness - targetFitness); i++){
			gene = recursiveRemove(gene, 1);
						
			if(gene.getFitness() < lastFitness){
				lastFitness = gene.getFitness();
			} else {
				break;
			}
		}
		return gene;
	}
	
	private Gene worsenGene(Gene gene, int addSize){
		boolean[] myBitGene = gene.getBitGene().clone();
		
		for(int i = 0; i<addSize; i++){
			boolean foundCanidate = false;
			int randStart = (int) (Math.random()*(myBitGene.length));
			for(int j = randStart; j<myBitGene.length; j++){
				if(!myBitGene[j]){
					myBitGene[j] = true;
					foundCanidate = true;
					break;
				}
			}
			for(int j = 0; j< randStart && foundCanidate ==false; j++){
				if(!myBitGene[j]){
					myBitGene[j] = true;
					break;
				}
			}
		}
		
		return new Gene(myBitGene, fitness(myBitGene));
	}
	

	
	private int parentIndex(boolean isFather, int pos){
		if(isFather){
			if(Math.random() < .5){
				return (int) (Math.random()*(10));
			}
			return (int) (Math.random()*(pos));
		} else{
			if(Math.random() < .5){
				return Math.min((int) (Math.random()*(pos)), (int) (Math.random()*(pos))) ;
			}
			return (int) (Math.random()*(pos));
		}		
	}
	
	private Gene mate(Gene fatherGene, Gene motherGene){
		boolean[] father = fatherGene.getBitGene();
		boolean[] mother = motherGene.getBitGene();
		boolean fatherFirst = false;
		boolean top = (genCount/5) % 2 == 0;
		if(Math.random() <.5){
			fatherFirst = true;
		}
		int crossover1 = (int) (Math.random()*(polygon.size()));
		int crossover2 = (int) (Math.random()*(polygon.size()));
		double lowX = Math.min(polygon.get(crossover1).x, polygon.get(crossover2).x);
		double highX = Math.max(polygon.get(crossover1).x, polygon.get(crossover2).x);
		
		
		boolean[] child = new boolean[father.length];
		for(int i = 0; i < father.length; i++){
			if(top){
				if(i < polygon.upper.size() && lowX < polygon.get(i).x &&   polygon.get(i).x <highX ){
					if(fatherFirst){
						child[i] = mother[i];
					} else {
						child[i] = father[i];
					}
				} else {
					if(fatherFirst){
						child[i] = father[i];
					} else {
						child[i] = mother[i];
					}
				}
			} else {
				if(i >= polygon.upper.size() && lowX < polygon.get(i).x &&   polygon.get(i).x <highX ){
					if(fatherFirst){
						child[i] = mother[i];
					} else {
						child[i] = father[i];
					}
				} else {
					if(fatherFirst){
						child[i] = father[i];
					} else {
						child[i] = mother[i];
					}
				}
			}
			if(Math.random() < mutation){
				child[i] = !child[i];
			}
		}
		
		return fixGreedyGene(2, new Gene(child, fitness(child)));//fixGene(child); //new Gene(child, fitness(child));
	}
	
	private Gene fixGene(boolean[] bitGene){
		int fitness = fitness(bitGene);
		if(fitness >0){
			return new Gene(bitGene, fitness);
		}
		ArrayList<Point2d> solution = new ArrayList<Point2d>();
		for(int i = 0; i<bitGene.length; i++){
			if(bitGene[i]){
				solution.add(polygon.get(i));
			}
		}
		
		int[] solutionList = new int[solution.size()];
		for(int i = 0; i<solution.size(); i++){
			solutionList[i] = solution.get(i).getID();
		}
		
		//Arrays.sort(solutionList);
		for(int i = 0; i<visList.length; i++){
			boolean found = false;
			for(int j = 0; j<solutionList.length; j++){
				if(Arrays.binarySearch(visList[i], solutionList[j]) >= 0){
					found = true;
					break;
				}
			}
			if(found == false){
				bitGene[i] = true;
			}
		}
		return new Gene(bitGene, fitness(bitGene));
	}
	
	
	@SuppressWarnings("unchecked")
	private Gene fixGreedyGene(int randSize, Gene broken){
		boolean[] myBitGene = new boolean[polygon.size()];
		ArrayList<Point2d>[] myVis = new ArrayList[polygon.size()];
		for(int i = 0; i<myVis.length; i++){
			myVis[i] = (ArrayList<Point2d>) visibility[i].clone();
		}
		ArrayList<Point2d> removedPointsInit = new ArrayList<Point2d>();
		boolean[] brokenBitGene = broken.getBitGene();
		for(int i = 0; i<brokenBitGene.length; i++){
			if(brokenBitGene[i]){
				removedPointsInit.addAll(myVis[i]);
				myVis[i] = new ArrayList<Point2d>();
			}
		}
		for(int i = 0; i<myVis.length; i++){
			myVis[i].removeAll(removedPointsInit);
		}
		
		myBitGene = brokenBitGene;
		//ArrayList<Point2d> mySolution = new ArrayList<Point2d>();
		while(fitness(myBitGene) > polygon.size()){
			int[] indexes = new int[randSize];
			int[] values = new int[randSize];
			for(int i = 0; i < randSize; i++){
				indexes[i] = -1;
				values[i] = -1;
			}
			
			
			for(int i = 0; i < myVis.length; i++){
				for(int j = 0; j < randSize; j++){
					if(myVis[i].size() >= values[j]){
						for(int k =j+1; k<randSize; k++){
							indexes[k] = indexes[j];
							values[k] = values[j];
						}
						
						indexes[j] = i;
						values[j] = myVis[i].size();
					}
				}
				
			}
			int randNumber = (int) (Math.random()*randSize);
			int mostVisibleIndex = indexes[randNumber];
			
			
			myBitGene[mostVisibleIndex] = true;
			ArrayList<Point2d> removedPoints = (ArrayList<Point2d>) myVis[mostVisibleIndex].clone();
			for(int i = 0; i < myVis.length; i++){
					myVis[i].removeAll(removedPoints);
			}
		}
		return new Gene(myBitGene, fitness(myBitGene));
	}
	
    private boolean rightTurn(Point2d a, Point2d b, Point2d c)
    {
            return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x) > 0.0;
    }
	
	public Gene generateRandomSeed(){
		double r = Math.random();
		boolean[] seed = new boolean[polygon.size()];
		
		for(int i = 0; i<seed.length; i++){
			if(Math.random() < r){
				seed[i] = true;
			} else {
				seed[i] = false;
			}
		}
		return new Gene(seed, fitness(seed));		
	}
}
