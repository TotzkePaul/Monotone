import geometry.Point2d;

import javax.swing.*;


import java.awt.*;
import java.util.ArrayList;

public class DrawGeneralPath extends JFrame{
	
	private static final long serialVersionUID = -1449126343936675198L;
	public DrawGeneralPath(){
          setSize(1000,800);
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          setLocationRelativeTo(null);
          setVisible(true);
     }
     
     public static ArrayList<Point2d> polygon;
     public static ArrayList<Point2d> vertices;
     public static ArrayList<Point2d> outerHull;
     public static ArrayList<Point2d> innerHull;
     public static boolean drawVerices = false;
     public static void draw(ArrayList<Point2d> tmp){ //ArrayList<Point2d> innerHull
    	 DrawGeneralPath.polygon = tmp;
    	 //DrawGeneralPath.innerHull = innerHull;
         new DrawGeneralPath();
     }
     
     
     
     public static void draw(ArrayList<Point2d> polygon, ArrayList<Point2d> vertices){ //ArrayList<Point2d> innerHull
    	 DrawGeneralPath.polygon = polygon;
    	 DrawGeneralPath.vertices = vertices;
    	 DrawGeneralPath.drawVerices = true;
         new DrawGeneralPath();
     }
     private static int width = 940;
     private static int height = 740;
     
     private static int scaleX(Point2d point){
    	 return (int) (point.x/Generator.maxLength*width) +20;
     }
     
     private static int scaleY(Point2d point){
    	 return (int) (point.y/Generator.maxLength*height)+40;
     }

     public void paint(Graphics g){
    	 
    	 Point2d previous;
    	 if(drawVerices){
    		 previous = DrawGeneralPath.polygon.get(0);
    		 for(int i =1; i<DrawGeneralPath.polygon.size(); i++){
    			 if(true){
    				 g.drawOval(scaleX(DrawGeneralPath.polygon.get(i)) -1, scaleY(DrawGeneralPath.polygon.get(i))-1, 2, 2);
    			 }
    			g.drawLine(scaleX(previous), scaleY(previous), scaleX(DrawGeneralPath.polygon.get(i)), scaleY(DrawGeneralPath.polygon.get(i)));
 				previous = DrawGeneralPath.polygon.get(i);
    		 }
    		 previous = DrawGeneralPath.vertices.get(0);
    		 for(int i =1; i<DrawGeneralPath.vertices.size(); i++){
    			 if(true){
    				 g.drawOval(scaleX(DrawGeneralPath.vertices.get(i)) -3, scaleY(DrawGeneralPath.vertices.get(i))-3, 6, 6);
    			 }
    			g.drawLine(scaleX(previous), scaleY(previous), scaleX(DrawGeneralPath.vertices.get(i)), scaleY(DrawGeneralPath.vertices.get(i)));
  				previous = DrawGeneralPath.vertices.get(i);
    		 }
	    	 return;
	     }
    	// Point2d previous;
		//draw outer hull
    	 if(DrawGeneralPath.polygon.size()!=0){			
			previous = DrawGeneralPath.polygon.get(0);
			for(int i =1; i<DrawGeneralPath.polygon.size(); i++){
				//if(previous.x < DrawGeneralPath.polygon.get(i).x)
					g.drawOval(scaleX(previous) -i/2, scaleY(previous)-i/2, i, i);
				
				g.drawLine(scaleX(previous), scaleY(previous), scaleX(DrawGeneralPath.polygon.get(i)), scaleY(DrawGeneralPath.polygon.get(i)));
				previous = DrawGeneralPath.polygon.get(i);
			}
			g.drawLine(scaleX(previous), scaleY(previous), scaleX(DrawGeneralPath.polygon.get(0)), scaleY(DrawGeneralPath.polygon.get(0)));
			//g.drawOval(scaleX(previous) -1, scaleY(previous)-1, 2, 2);		
    	 }
		
		if(drawVerices){
			g.setColor(Color.RED);
			if(DrawGeneralPath.vertices.size()!=0){
				previous = DrawGeneralPath.vertices.get(0);
				for(int i =1; i<DrawGeneralPath.vertices.size(); i++){
					//if(previous.x < DrawGeneralPath.polygon.get(i).x){
					if(i==1)
						g.drawOval(scaleX(previous) -1, scaleY(previous)-1, 2, 2);
					//}
					g.drawLine(scaleX(previous), scaleY(previous), scaleX(DrawGeneralPath.vertices.get(i)), scaleY(DrawGeneralPath.vertices.get(i)));
					previous = DrawGeneralPath.vertices.get(i);
				}
				g.drawLine(scaleX(previous), scaleY(previous), scaleX(DrawGeneralPath.vertices.get(0)), scaleY(DrawGeneralPath.vertices.get(0)));
				g.drawOval(scaleX(previous) -1, scaleY(previous)-1, 2, 2);
				//draw inner hull
			
	    	 }
		}
	}
}

