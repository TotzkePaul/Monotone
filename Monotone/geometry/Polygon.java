package geometry;

import geometry.Point2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;



public class Polygon {
	public ArrayList<Point2d> upper;
	public ArrayList<Point2d> lower;
	
	public Polygon(){
		
	}
	public Polygon(ArrayList<Point2d> upper2, ArrayList<Point2d> lower2){
		this.upper = upper2;
		this.lower = lower2;
	}
	
	public int size(){
		return upper.size() + lower.size();
	}
	
	int nextCounter = 0;
	public Point2d next(){
		if(nextCounter == size()){
			return null;
		}
		if(nextCounter >= upper.size()){
			return lower.get(nextCounter++ - upper.size());
		}
		return upper.get(nextCounter++);
	}
	
	public Point2d get(int pos){
		if(pos >= size() || pos < 0){
			throw new NullPointerException(""+pos);
			//return null;
		}
		if(pos >= upper.size()){
			return lower.get(pos - upper.size());
		}
		return upper.get(pos);
	}
	
	public boolean reset(){
		nextCounter = 0;
		return true;
	}
	
	public int indexOf(Point2d obj){		
		for(int i = 0; i <upper.size(); i++){
			if(obj == upper.get(i))
				return i;
		}
		for(int i = 0; i <lower.size(); i++){
			if(obj == lower.get(i))
				return upper.size() + i;
		}
		return -1;
	}
	
	public ArrayList<Point2d> toArrayList(){
		Collections.reverse(lower);
		ArrayList<Point2d> retVal = new ArrayList<Point2d>(upper);
		retVal.addAll(upper);
		retVal.addAll(lower);
        Collections.reverse(lower);
        return retVal;
	}
	int[][] visList = null;
	public boolean isSatisfied(ArrayList<Point2d>[] vis, ArrayList<Point2d> solution){
		if(visList == null){
			visList = new int[vis.length][];
			for(int i = 0; i<vis.length; i++){
				visList[i] = new int[vis[i].size()];
				for(int j = 0; j < vis[i].size(); j++){
					visList[i][j] = vis[i].get(j).getID();
				}
				Arrays.sort(visList[i]);
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
				return false;
			}
		}
		return true;
		/** WORKS BUT SLOW
		for(int i = 0; i<vis.length; i++){
			for(int j = 0; j<vis[i].size(); j++){
				boolean found = false;
				for(int k = 0; k<solution.size(); k++){
					if(vis[i].contains(solution.get(k)) ){
						found = true;
						break;
					}
				}
				if(found == false){
					return false;
				}
			}
		}
		return true;
		*/
		
		/**
		int[] solutionList = new int[solution.size()];
		for(int i = 0; i<solution.size(); i++){
			solutionList[i] = solution.get(i).getID();
		}
		if(visList == null){
			visList = new int[vis.length][];
			for(int i = 0; i<vis.length; i++){
				visList[i] = new int[vis[i].size()];
				for(int j = 0; j < vis[i].size(); j++){
					visList[i][j] = vis[i].get(j).getID();
				}
			}
		}
		
		for(int i = 0; i<visList.length; i++){
			if(visList[i].length<3){
				System.out.println("ERROR");
			}
			for(int j = 0; j<visList[i].length; j++){
				boolean found = false;
				for(int k = 0; k<solutionList.length; k++){
					if(visList[i][j] == solutionList[k]){
						found = true;
						break; // found
					}
				}
				if(found == false){
					return false;
				}
			}
		}
		return true;*/
	}
	
}
