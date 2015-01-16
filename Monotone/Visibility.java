import geometry.Point2d;

import java.util.ArrayList;
import java.util.Collections;




public class Visibility {
	

	public static ArrayList<Point2d>[] point2dVisibility(ArrayList<Point2d> polygon){
		@SuppressWarnings("unchecked")
		ArrayList<Point2d>[] visiblePoints = new ArrayList[polygon.size()];
		ArrayList<Point2d> upperHull = new ArrayList<Point2d>();
		ArrayList<Point2d> lowerHull = new ArrayList<Point2d>();
		{
			   		
    		Point2d previous = polygon.get(0);
    		upperHull.add(previous);
    		for(int i=1; i<polygon.size();i++){
    			if(previous.x < polygon.get(i).x){
    				upperHull.add(polygon.get(i));
    			} else {
    				lowerHull.add(polygon.get(i));
    			}
    			previous = polygon.get(i);
    		}
    		
    		Collections.sort(lowerHull);
		}
		for(int i = 0; i <polygon.size(); i++){
			visiblePoints[i] = new ArrayList<Point2d>(polygon);
			ArrayList<Point2d> removedPoints = new ArrayList<Point2d>();
			
			for(int j = 0; j<polygon.size(); j++){
				if(i<j){
					
				
					for(int k = 0; k<upperHull.size(); k++){
						
					}
					
					for(int k = 0; k<lowerHull.size(); k++){
						
					}
				} else if(j<i){
					if(!visiblePoints[j].contains(polygon.get(i))){
						removedPoints.add(polygon.get(i));
					}
				}
			}
		}
		
		
		return visiblePoints;
	}
	
	public static boolean isAbove(Point2d a, Point2d b, Point2d c){
		if(a.x > b.x){
			//
			Point2d swap = a;
			a=b;
			b=swap;
		}
		return ((b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x)) > 0;
	}
	
	public static boolean isBelow(Point2d a, Point2d b, Point2d c){
		if(a.x > b.x){
			//
			Point2d swap = a;
			a=b;
			b=swap;
		}
		return ((b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x)) < 0;
	}
	
}
