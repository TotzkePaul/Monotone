package convexHull;

import geometry.Point2d;

import java.util.ArrayList;
import java.util.Collections;
//import java.util.Comparator;


public class ConvexHullAlgorithm
{
		/*
		public ArrayList<Point2d> merge(ArrayList<Point2d> outerHull, ArrayList<Point2d> innerHull)
		{
			//Collections.sort(outerHull);
			//Collections.sort(innerHull);
			ArrayList<Point2d> result = new ArrayList<Point2d>();
			
			int innerCount = 0;
			int outerCount = 0;
			for(int i=0; i <outerHull.size() + innerHull.size(); i++){
				//less than
				if(outerHull.get(outerCount).compareTo(innerHull.get(innerCount)) ==-1){
					
				}
			}
			return innerHull;
		}*/
	
        public ArrayList<Point2d> execute(ArrayList<Point2d> verticesOuterHull) 
        {
                //ArrayList<Point2d> xSorted = (ArrayList<Point2d>) verticesOuterHull.clone();
        		ArrayList<Point2d> xSorted = verticesOuterHull;
                Collections.sort(xSorted);
                
                int n = xSorted.size();
                if(n <=1){
                	return xSorted;
                }
                Point2d[] lUpper = new Point2d[n];
                
                // save first two points
                lUpper[0] = xSorted.get(0);
                lUpper[1] = xSorted.get(1);
                
                int lUpperSize = 2;
                
                for (int i = 2; i < n; i++)
                {
                        lUpper[lUpperSize] = xSorted.get(i);
                        lUpperSize++;
                        
                        while (lUpperSize > 2 && !rightTurn(lUpper[lUpperSize - 3], lUpper[lUpperSize - 2], lUpper[lUpperSize - 1]))
                        {
                            // Remove the middle Point2d of the three last
                        	lUpper[lUpperSize - 2] = lUpper[lUpperSize - 1];
                            lUpperSize--;
                        }
                }
                
                Point2d[] lLower = new Point2d[n];
                
                lLower[0] = xSorted.get(n - 1);
                lLower[1] = xSorted.get(n - 2);
                
                int lLowerSize = 2;
                
                for (int i = n - 3; i >= 0; i--)
                {
                        lLower[lLowerSize] = xSorted.get(i);
                        lLowerSize++;
                        
                        while (lLowerSize > 2 && !rightTurn(lLower[lLowerSize - 3], lLower[lLowerSize - 2], lLower[lLowerSize - 1]))
                        {
                                // Remove the middle Point2d of the three last
                                lLower[lLowerSize - 2] = lLower[lLowerSize - 1];
                                lLowerSize--;
                        }
                }
                
                ArrayList<Point2d> result = new ArrayList<Point2d>();
                
                for (int i = 0; i < lUpperSize; i++)
                {
                        result.add(lUpper[i]);
                }
                
                for (int i = 1; i < lLowerSize - 1; i++)
                {
                        result.add(lLower[i]);
                }
                
                return result;
        }
        
        private boolean rightTurn(Point2d a, Point2d b, Point2d c)
        {
                return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x) > 0.0;
        }
}