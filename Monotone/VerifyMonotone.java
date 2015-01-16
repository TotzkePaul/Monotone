import geometry.Point2d;

import java.util.*;


public class VerifyMonotone{
	static boolean exit = false;
	public static boolean isBelow(Point2d lower, Point2d left, Point2d right){
        //first get slope
        double m = (right.y - left.y) / (right.x - left.x);
        //get b
        double b = left.y - (m * left.x);
        //now we have y = mx + b
        //substitute x from upper to get y location on line
        double y = (m * lower.x) + b;
        if(lower.y < y){
            return true;
        } else{
            return false;
        }
    }

    public static void below(ArrayList<Point2d> upper, ArrayList<Point2d> lower){
        //leftmost and rightmost are in upper2
        while(lower.size() > 0){
            if(lower.get(0).x < upper.get(1).x){
                //lower(0) is between upper(0) and upper(1)
                if(!isBelow(lower.get(0), upper.get(0), upper.get(1))){
                    System.out.println("ERROR!!!  2");
                    System.out.println("lower = " + lower.get(0).x + " " +lower.get(0).y);
                    System.out.println("upper left = " + upper.get(0).x + " " +upper.get(0).y);
                    System.out.println("upper right = " + upper.get(1).x + " " +upper.get(1).y);
                }
                //lower2(0) has been verified to be below upper2, remove it
                lower.remove(0);
            } else{
                //lower2(0) is right of upper2(1)
                upper.remove(0);
            }
        }
    }

    public static boolean isAbove(Point2d upper, Point2d left, Point2d right){
        //first get slope
        double m = (right.y - left.y) / (right.x - left.x);
        //get b
        double b = left.y - (m * left.x);
        //now we have y = mx + b
        //substitute x from upper to get y location on line
        double y = (m * upper.x) + b;
        if(upper.y > y){
            return true;
        } else{
            return false;
        }
    }

    public static void above(ArrayList<Point2d> upper, ArrayList<Point2d> lower){
        //leftmost and rightmost were moved from upper to lower
        while(upper.size() > 0){
            if(upper.get(0).x < lower.get(1).x){
                //upper(0) is between lower(0) and lower(1)
                if(!isAbove(upper.get(0), lower.get(0), lower.get(1))){
                    System.out.println("ERROR!!!");
                    System.out.println("upper = " + upper.get(0).x + " " +upper.get(0).y);
                    System.out.println("lower left = " + lower.get(0).x + " " +lower.get(0).y);
                    System.out.println("lower right = " + lower.get(1).x + " " +lower.get(1).y);
                    exit = true;
                    return;
                    //System.exit(0);
                }
                //upper(0) has been verified to be above lower, remove it
                upper.remove(0);
            } else{
                //upper(0) is right of lower(1)
                lower.remove(0);
            }
        }
    }

    public static void verify(ArrayList<Point2d> polygon) throws Exception{
    	exit=false;
    	ArrayList<Point2d> upper = new ArrayList<Point2d>();
    	ArrayList<Point2d> upper2 = new ArrayList<Point2d>();
    	ArrayList<Point2d> lower = new ArrayList<Point2d>();
    	ArrayList<Point2d> lower2 = new ArrayList<Point2d>();
		//draw outer hull
    	 if(polygon.size()!=0){			
			
			upper.add(polygon.get(0));
			upper2.add(polygon.get(0));
			for(int i = 0; i<polygon.size()-1; i++){
				if(polygon.get(i).x < polygon.get(i+1).x){
					upper.add(polygon.get(i+1));
					upper2.add(polygon.get(i+1));
				} else {
					lower.add(polygon.get(i+1));
					lower2.add(polygon.get(i+1));
				}
			}
    	 }

         //leftmost is in upper, no need for it to be there, move to lower
         lower.add(0, upper.get(0));
         //rightmost is in upper, no need for it to be there, move to lower
         lower.add(upper.get(upper.size()-1));

         //remove leftmost and rightmost from upper, useless
         upper.remove(0);
         upper.remove(upper.size()-1);

         //verify that all upper are above all lower
         above(upper, lower);
         
         if(exit){
        	 return;
         }
         //verify that all lower are below all upper
         below(upper2, lower2);
         if(exit){
        	 return;
         }
         System.out.println("verified!");
    }

}