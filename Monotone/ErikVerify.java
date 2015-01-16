import geometry.Point2d;

import java.util.*;
import java.io.*;

public class ErikVerify{

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
                    System.exit(0);
                }
                //upper(0) has been verified to be above lower, remove it
                upper.remove(0);
            } else{
                //upper(0) is right of lower(1)
                lower.remove(0);
            }
        }
    }

    public static void main2(String args[]) throws Exception{
        ArrayList<Point2d> upper = new ArrayList<Point2d>();
        ArrayList<Point2d> upper2 = new ArrayList<Point2d>();
        ArrayList<Point2d> lower = new ArrayList<Point2d>();
        ArrayList<Point2d> lower2 = new ArrayList<Point2d>();

        Scanner input = new Scanner(new File("data", "sample00.txt"));
        boolean u = true;
        //read in all values and store into upper, upper2, lower and lower2
        while(input.hasNext()){
            String point = input.next();
            if(point.equals(";")){
                u = false;
                continue;
            }

            String[] coordinates = point.split(",");
            if(u){
                upper.add(new Point2d(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1])));
                upper2.add(new Point2d(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1])));
            } else{
                lower.add(new Point2d(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1])));
                lower2.add(new Point2d(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1])));
            }
        }
        //Collections.reverse(lower);
        //upper.addAll(lower);
        //DrawGeneralPath.draw(upper);
        
        
        //leftmost is in upper, no need for it to be there, move to lower
        lower.add(0, upper.get(0));
        //rightmost is in upper, no need for it to be there, move to lower
        lower.add(upper.get(upper.size()-1));

        //remove leftmost and rightmost from upper, useless
        upper.remove(0);
        upper.remove(upper.size()-1);

        /**
        try {
        	Thread.sleep(10*1000);
        	} catch(InterruptedException e) {
        	} */
        //verify that all upper are above all lower
        above(upper, lower);
        //verify that all lower are below all upper
        below(upper2, lower2);
        
        System.out.println("verified!");
    }

}