import geometry.*;
import heuristic.GeneticSearch;
import heuristic.MonotoneSearch;
import heuristic.Search;
import statistics.SolutionStats;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;


import convexHull.ConvexHullAlgorithm;


public class Generator {
	static int maxLength = 1000000;
	static boolean eightHour = true;
	static boolean twoHour = true;
	public static int smallSize = 1000;
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		for(int i = 1; i<11; i++){
			Polygon verify = monotoneIO.importPolygonFromTextFile("/erik/5000input"+i+".txt");
			ArrayList<Point2d>[] vis = monotoneIO.linearProgramToVisibilityList("/erik/5000lp" + i + ".txt", verify);
			ArrayList<Point2d> solution = monotoneIO.importSolutionFromTextFile("/outputSolution" + i +".txt", verify);
			//System.out.println(verify.isSatisfied(vis, solution));
			
			
			//SolutionStats prog = new SolutionStats(verify, vis, solution);
			if(twoHour = true){
				//continue;
			}
			try {
				myThread(i, verify, vis);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	public static boolean GenerateTestCases(){
		ArrayList<Point2d> polygon;
		for(int i=0; i<10;i++){
			polygon = mergePolygon(generateRandomPoints(smallSize, smallSize+"_"+i));
			
			monotoneIO.exportPolygonToFile("/"+smallSize+"_" + i + ".ply", polygon);
			monotoneIO.exportPolygonToTextFile("/"+smallSize+"_" + i + ".txt", polygon);
			try {
				//ArrayList<Point2d> verify = monotoneIO.importPolygonFromTextFile(smallSize+"_" + i + ".txt");
				//DrawGeneralPath.draw(verify);
				//VerifyMonotone.verify(polygon);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			{
				//ArrayList<Point2d>[] vis = Visibility.point2dVisibility(polygon);
				//monotoneIO.visibilityListToLinearProgram(smallSize+"_linear_prog" + i + ".txt", polygon, vis);
			}
			System.out.println(i);
			/**
			polygon= mergePolygon(generateRandomPoints(50000, "50000_"+i));
			monotoneIO.exportPolygonToFile("/50000_" + i + ".ply", polygon);
			monotoneIO.exportPolygonToTextFile("/50000_" + i + ".txt", polygon);
			{
        		 System.out.println("verified!");
        	}
				ArrayList<Point2d>[] vis = Visibility.point2dVisibility(polygon);
				monotoneIO.visibilityListToLinearProgram("50000_linear_prog" + i + ".txt", polygon, vis);
			}*/
		}
		
		return true;
	}
	
	public static ArrayList<Point2d> answer = new ArrayList<Point2d>();
	
	@SuppressWarnings("static-access")
	public static void myThread(int solution, Polygon polygon, ArrayList<Point2d>[] visList) throws Exception{
		PrintWriter fout = new PrintWriter(new FileWriter(new File("output" ,"outputSolution"+solution+ ".txt")));
		MonotoneSearch mySearch = new MonotoneSearch(polygon, visList);
        Thread go = new Thread(mySearch);
        go.start();
        //wait for 1 minute, then get an answer
        Thread.currentThread().sleep(60000);
        answer = mySearch.getCurrentSolution();
        System.out.println(answer.size());
		fout.println(answer.size());
        for(geometry.Point2d d: answer){
			System.out.print(d.getID()+" ");
			fout.print(d.getID()+" ");
		}
		System.out.println();
		fout.println();
		fout.flush();
		if(maxLength == 1000000){
			//return;
		}
		if(twoHour){
	        //wait for 2 hours, then get an answer
	        Thread.currentThread().sleep(7190000); //7190000
	        answer = mySearch.getCurrentSolution();
	        System.out.println(answer.size());
					fout.println(answer.size());
			        for(Point2d d: answer){
						System.out.print(d.getID()+" ");
						fout.print(d.getID()+" ");
					}
					System.out.println();
			fout.println();
			fout.flush();
		}
		if(eightHour){
	        //wait for 8 hours, then get an answer
	        Thread.currentThread().sleep(21600000); //21600000
	        answer = mySearch.getCurrentSolution();
	        System.out.println(answer.size());
					fout.println(answer.size());
			        for(Point2d d: answer){
						System.out.print(d.getID()+" ");
						fout.print(d.getID()+" ");
					}
					System.out.println();
			fout.println();
			fout.close();
		}
		go.stop();
		System.out.println("done");
		
		//System.exit(0);
	}
	
	public static ArrayList<Point2d> bitonicTour(ArrayList<Point2d> remainingVertices){
		ConvexHullAlgorithm hull = new ConvexHullAlgorithm();
		ArrayList<Point2d> convexHull = hull.execute(remainingVertices);
		remainingVertices.removeAll(convexHull);
		
		ArrayList<Point2d> upperResultingHull= new ArrayList<Point2d>();
		ArrayList<Point2d> lowerResultingHull= new ArrayList<Point2d>();
		
		Point2d previous = convexHull.get(0);
		upperResultingHull.add(previous);
		for(int i=1; i<convexHull.size();i++){
			if(previous.x < convexHull.get(i).x){
				upperResultingHull.add(convexHull.get(i));
			} else {
				lowerResultingHull.add(convexHull.get(i));
			}
			previous = convexHull.get(i);
		}
		
		for(int i=0; i<remainingVertices.size();i++){
			upperResultingHull.add(remainingVertices.get(i));
		}
		
		Collections.sort(upperResultingHull);
		Collections.sort(lowerResultingHull);
		Collections.reverse(lowerResultingHull);
		
		upperResultingHull.addAll(lowerResultingHull);
		
		return upperResultingHull;
	}
	
	//d- how often it is true
	public static boolean randomBoolean(double d){
		Random random = new Random();
		return random.nextDouble()<d;
		
	}
	

	
	
	public static ArrayList<Point2d> mergePolygon(ArrayList<Point2d> remainingVertices){
		ConvexHullAlgorithm hull = new ConvexHullAlgorithm();
		ArrayList<Point2d> verticesOuterHull = hull.execute(remainingVertices);;
		ArrayList<Point2d> verticesInnerHull = null;
		
		while(!remainingVertices.isEmpty()){
			//Make a convex hull
			//remove remaining vertices
			remainingVertices.removeAll(verticesOuterHull);
			//create inner hull
			verticesInnerHull = hull.execute(remainingVertices);
			
			
			
			if(verticesInnerHull.size() == 0){
				break;
			}
			
			ArrayList<Point2d> upperResultingHull= new ArrayList<Point2d>();
			ArrayList<Point2d> lowerResultingHull= new ArrayList<Point2d>();
			//implement: merge inner and outer hull
			Point2d previous = verticesOuterHull.get(0);
			upperResultingHull.add(previous);
			for(int i=1; i<verticesOuterHull.size();i++){
				if(previous.x < verticesOuterHull.get(i).x){
					upperResultingHull.add(verticesOuterHull.get(i));
					
				} else {
					lowerResultingHull.add(verticesOuterHull.get(i));
				}
				previous = verticesOuterHull.get(i);
			}
			previous = verticesInnerHull.get(0);
			lowerResultingHull.add(previous); 
			for(int i=1; i<verticesInnerHull.size();i++){
				if(previous.x < verticesInnerHull.get(i).x){
					upperResultingHull.add(verticesInnerHull.get(i));
				} else {
					lowerResultingHull.add(verticesInnerHull.get(i));
				}
				previous = verticesInnerHull.get(i);
			}
			Collections.sort(upperResultingHull);
			Collections.sort(lowerResultingHull);
			Collections.reverse(lowerResultingHull);
			
			upperResultingHull.addAll(lowerResultingHull);
			verticesOuterHull = upperResultingHull;
		}
		
		//Fix any mistakes
		
		ArrayList<Point2d> upperHull = new ArrayList<Point2d>();
		ArrayList<Point2d> lowerHull = new ArrayList<Point2d>();
		
		
		//Collections.reverse(lowerHull);
		ArrayList<Point2d> movedPoints = new ArrayList<Point2d>();
		//implement: merge inner and outer hull
		Point2d previous = verticesOuterHull.get(0);
		upperHull.add(previous);
		lowerHull.add(upperHull.get(upperHull.size()-1));
		for(int i=1; i<verticesOuterHull.size();i++){
			if(previous.x < verticesOuterHull.get(i).x){
				upperHull.add(verticesOuterHull.get(i));
				
			} else {
				
				lowerHull.add(verticesOuterHull.get(i));
			}
			previous = verticesOuterHull.get(i);
		}
		lowerHull.add(upperHull.get(upperHull.size()-1));
		lowerHull.add(upperHull.get(0));
		int lhPos = 0; //lowerHull Position
		Collections.sort(lowerHull);
		for(int i=0;i<upperHull.size() -1; i++){
			while(lhPos < lowerHull.size() && lowerHull.get(lhPos).x < upperHull.get(i+1).x){
				if(upperHull.get(i).x < lowerHull.get(lhPos).x){
					if(!isRight(upperHull.get(i), upperHull.get(i+1), lowerHull.get(lhPos))){
						movedPoints.add(lowerHull.get(lhPos));
					}
				}
				lhPos++;
			}
		}
		upperHull.addAll(movedPoints);
		lowerHull.removeAll(movedPoints);
		Collections.sort(upperHull);
		
		movedPoints = new ArrayList<Point2d>();
		lhPos =0;
		for(int i=0;i<lowerHull.size() -1; i++){
			while(lhPos <upperHull.size() && upperHull.get(lhPos).x < lowerHull.get(i+1).x){
				if(lowerHull.get(i).x < upperHull.get(lhPos).x){
					if(isRight(lowerHull.get(i), lowerHull.get(i+1), upperHull.get(lhPos))){
						movedPoints.add(upperHull.get(lhPos));
					}
				}
				lhPos++;
			}
		}
		
		lowerHull.addAll(movedPoints);
		upperHull.removeAll(movedPoints);
		lowerHull.removeAll(upperHull);
		Collections.sort(lowerHull);
		
		Collections.reverse(lowerHull);
		
		upperHull.addAll(lowerHull);
		verticesOuterHull = upperHull;
		
		if(isLeft(verticesOuterHull.get(0), verticesOuterHull.get(1), verticesOuterHull.get(verticesOuterHull.size()-1))){
			Collections.reverse(verticesOuterHull);
		}
		return verticesOuterHull;
		
	}
	
	public static ArrayList<Point2d> generateRandomPoints(int inputN, String fileName){
		double[] xCoordinate = new double[inputN];
		double[] yCoordinate = new double[inputN];
		Point2d[] vertices = new Point2d[inputN];
		
		for(int i =0; i<inputN; i++){
			xCoordinate[i] = Math.random()*maxLength + Double.MIN_VALUE;
			yCoordinate[i] = Math.random()*maxLength + Double.MIN_VALUE;
			vertices[i]=new Point2d(xCoordinate[i], yCoordinate[i]);
			for(int j = 0; j<i;j++){
				if(xCoordinate[i] == xCoordinate[j]){
					i--;
					break;
				}
			}
		}	
		ArrayList<Point2d> remainingVertices = new ArrayList<Point2d>(Arrays.asList(vertices));
		
		return remainingVertices;
	}
	
	public static boolean isLeft(Point2d a, Point2d b, Point2d c){
		return ((b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x)) > 0;
	}
	
	public static boolean isRight(Point2d a, Point2d b, Point2d c){
		return ((b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x)) < 0;
	}
}
