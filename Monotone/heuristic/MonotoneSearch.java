package heuristic;


import geometry.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class MonotoneSearch extends Search {

	Polygon polygon;
	ArrayList<Point2d>[] visibility;
	public Gene bestGene;
	public Gene[] population;
	public double mutation;
	int[][] visList;
	boolean debug = true;
	
	public MonotoneSearch(Polygon polygon, ArrayList<Point2d>[] visibility){
		this.polygon = polygon;
		this.visibility = visibility;
		this.population = new Gene[2]; // new Gene[polygon.size()/2];
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
		/**
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
		System.out.println(maxCount); */
		this.bestGene = new Gene(tempGene, fitness(tempGene));
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
		//population[0] = bestGene;
		
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < population.length; i++){
			if(population[i] == null){
				population[i] = myRandGreedyGene(20);
				if(population[i].getFitness() <bestGene.getFitness()){
					bestGene = population[i];
				}
			}
		}
		Arrays.sort(population);
		
		genCount = 0;
		Gene neighborhoodSearch = new Gene(population[0].getBitGene().clone(), population[0].getFitness());
		neighborhoodSearch = improveGene(neighborhoodSearch, neighborhoodSearch.getFitness() -1);
		
		long endTime = System.currentTimeMillis();

		long duration = (endTime - startTime);
		
		if(debug){
			System.out.println("Completed in:"+ (duration/1000));
		}
		while(true){
			if(debug){
				System.out.println("Gen " + genCount++);
			} else {
				genCount++;
			}
			
			if(neighborhoodSearch.getFitness() < bestGene.getFitness()){
				bestGene = neighborhoodSearch;
			}
			if(debug){
				System.out.println("\tBest:"+bestGene.getFitness());
			}
			Gene neighborhoodAttempt = worsenGene(neighborhoodSearch, 22);
			neighborhoodAttempt = improveGene(neighborhoodAttempt, 24);
			
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
			int maxRand =0;
			for(int i = 0; i< values.length; i++){
				if(values[i]<=0){
					maxRand = i;
					break;
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
	
	@SuppressWarnings("unchecked")
	private Gene myRandGreedyGene(int randSize){
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
			int maxRand =randSize;
			for(int i = 0; i< values.length; i++){
				if(values[i]<=0){
					maxRand = i;
					break;
				}
			}
			int randNumber = (int) (Math.random()*maxRand);
			int myChoice = indexes[randNumber];
			/*
			double myValue = Double.MAX_VALUE;
			
			for(int i = 0; i< maxRand; i++){
				//if(Math.abs(polygon.get(indexes[i]).x -1000000/2)> myValue ){
				//if(polygon.get(indexes[i]).x< myValue ){
				if(){
					myChoice = indexes[i];
					//myValue = Math.abs(polygon.get(indexes[i]).x -1000000/2);
					//myValue = polygon.get(indexes[i]).x;
				}
			}*/
			//randNumber = myChoice;
			int mostVisibleIndex = myChoice;//indexes[randNumber];
			
			
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
		if(0.5 < Math.random()) {
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
						if(depth>1) {
							children.add(recursiveRemove(myGene, depth-1));
						} else {
							children.add(myGene);
						}
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
						if(depth>1) {
							children.add(recursiveRemove(myGene, depth-1));
						} else {
							children.add(myGene);
						}
					}
				}
			}
		} else {
			for(int i = bitGene.length-1; i> randStart; i--){
				if(bitGene[i]){
					if(children.size()>minChildSize){
						break;
					}
					boolean[] myBitGene = bitGene.clone();
					myBitGene[i] = false;
					Gene myGene = new Gene(myBitGene, fitness(myBitGene));
					if(myGene.getFitness() <  gene.getFitness()){
						//return recursiveRemove(myGene, depth-1);
						if(depth>1) {
							children.add(recursiveRemove(myGene, depth-1));
						} else {
							children.add(myGene);
						}
					}
				}
			}
			for(int i = randStart; i>=0; i--){
				if(bitGene[i]){
					if(children.size()>minChildSize){
						break;
					}
					boolean[] myBitGene = bitGene.clone();
					myBitGene[i] = false;
					Gene myGene = new Gene(myBitGene, fitness(myBitGene));
					if(myGene.getFitness() <  gene.getFitness()){
						//return recursiveRemove(myGene, depth-1);
						if(depth>1) {
							children.add(recursiveRemove(myGene, depth-1));
						} else {
							children.add(myGene);
						}
					}
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
	
	private Gene myWorsenGene(Gene gene, int addSize){
		boolean[] myBitGene = gene.getBitGene().clone();
		
		int randPos = (int) (Math.random()*(polygon.size()));
		int leftMostIndex = polygon.indexOf(visibility[randPos].get(0));
		int rightMostIndex = leftMostIndex;
		for(int i = 1; i <visibility[i].size(); i++){
			if(visibility[randPos].get(i).x < polygon.get(leftMostIndex).x){
				leftMostIndex = polygon.indexOf(visibility[randPos].get(i));
			}
			if(visibility[randPos].get(i).x < polygon.get(leftMostIndex).x){
				rightMostIndex = polygon.indexOf(visibility[randPos].get(i));
			}
		}
		int topLeftMostIndex = 0;
		int topRightMostIndex = 0;
		for(int i = 0; i < polygon.upper.size(); i++){
			if(polygon.get(leftMostIndex).x <= polygon.get(i).x){
				topLeftMostIndex = i;
				break;
			}
		}
		for(int i = topLeftMostIndex; i < polygon.upper.size(); i++){
			if(polygon.get(rightMostIndex).x >= polygon.get(i).x){
				topRightMostIndex = i;
				break;
			}
		}
		
		int bottomLeftMostIndex = 0;
		int bottomRightMostIndex = 0;
		for(int i = 0; i < polygon.upper.size(); i++){
			if(polygon.get(leftMostIndex).x <= polygon.get(i).x){
				bottomLeftMostIndex = i;
				break;
			}
		}
		for(int i = bottomLeftMostIndex; i < polygon.upper.size(); i++){
			if(polygon.get(rightMostIndex).x >= polygon.get(i).x){
				bottomRightMostIndex = i;
				break;
			}
		}
		
		
		
		for(int i = 0; i<addSize; i++){
			boolean foundCanidate = false;
			int randStart;
			if(Math.random() <.5){
				randStart =topLeftMostIndex + (int) (Math.random()*(topRightMostIndex - topLeftMostIndex));
			} else {
				randStart =bottomLeftMostIndex + (int) (Math.random()*(bottomRightMostIndex - bottomLeftMostIndex));
			}
			 
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
