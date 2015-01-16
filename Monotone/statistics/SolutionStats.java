package statistics;

import geometry.Point2d;
import geometry.Polygon;

import java.util.ArrayList;

public class SolutionStats {
	Polygon polygon;
	ArrayList<Point2d>[] visibility;
	ArrayList<Point2d> solutions; 
	public SolutionStats(Polygon polygon, ArrayList<Point2d>[] visibility, ArrayList<Point2d> solutions)
	{
		this.polygon = polygon;
		this.visibility = visibility;
		this.solutions = solutions;
		double[] xCoordinate = new double[solutions.size()];
		double[] yCoordinate = new double[solutions.size()];
		int[] visSize = new int[solutions.size()];
		int lc = 0; 
		int midCenterCounter = 0;
		for(int i = 0; i < polygon.size(); i++){
			if(solutions.indexOf(polygon.get(i)) != -1){
				xCoordinate[lc] = polygon.get(i).x;
				yCoordinate[lc] = polygon.get(i).y;
				visSize[lc] = visibility[i].size();
				if( i>0 && i+1<5000 && isMiddleCenter(polygon.get( i-1 ), polygon.get( i), polygon.get( (i+1) ), i < polygon.upper.size() )){
					midCenterCounter++;
				}
				lc++;
			}
		}
		System.out.println("middle center%: " + (midCenterCounter*1.0)/(solutions.size()*1.0));
		System.out.println("x mean " + mean(xCoordinate));
		System.out.println("y mean " + mean(yCoordinate));
		System.out.println("vis mean " + mean(visSize));
		
	}
	
	private int pointPos(int pointPos){
		 if(pointPos < polygon.upper.size()){
			return pointPos;
		} else {
			return polygon.size()  - (pointPos - polygon.upper.size()) -1;
		}
	}
	
	public static double mean(double[] m) {
		double sum = 0;
		for (int i = 0; i < m.length; i++) {
			sum += (double)m[i]/ (double)m.length;
		}
		return sum;
	}
	
	public static double mean(int[] m) {
		double sum = 0;
		for (int i = 0; i < m.length; i++) {
			sum += (double)m[i]/ (double)m.length;
		}
		return sum;
	}
	
	private boolean rightTurn(Point2d a, Point2d b, Point2d c)
    {
            return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x) > 0.0;
    }
	
	private boolean isMiddleCenter(Point2d a, Point2d b, Point2d c, boolean isTop){
		if(isTop){
			return b.x == Math.min(a.x, Math.min(c.x, b.x));
		} else {
			return b.x == Math.max(a.x, Math.max(c.x, b.x));
		}
		
		
	}
	
}
