import geometry.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;




public class monotoneIO {

	public static boolean exportPolygonToFile(String filename, ArrayList<Point2d> polygon){
		ObjectOutputStream outputStream = null;
        
        try {
            //Construct the LineNumberReader object
            outputStream = new ObjectOutputStream(new FileOutputStream(new File("data", filename)));   
            
            outputStream.writeObject(polygon);           
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the ObjectOutputStream
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		
		return false;
	}
	
	public static ArrayList<Point2d> importSolutionFromTextFile(String filename, Polygon polygon) throws FileNotFoundException{
		File file = null;
		if(filename ==null){
			JFileChooser chooser=new  JFileChooser(new File("/Monotone/Montone"));
	        int returnVal = chooser.showOpenDialog(null);
	        if(returnVal == JFileChooser.APPROVE_OPTION) {
	        	file = chooser.getSelectedFile();
			} 
		} else {
			file = new File("output",filename);
		}
		
		Scanner input = new Scanner(file);
        String[] pointLabel = new String[0];
        while(input.hasNextLine()){
            String point = input.nextLine();
            
            pointLabel = point.split(" ");
        }
        int[] pointIds = new int[pointLabel.length];
        
        
        for(int i = 0; i < pointLabel.length; i++){
        		pointIds[i] = Integer.parseInt(pointLabel[i]);
        }
        ArrayList<Point2d> solutions = new ArrayList<Point2d>();
        for(int i = 0; i < pointIds.length; i++){
        	for(int j = 0; j < polygon.size(); j++){
        		if(polygon.get(j).getID() == pointIds[i]){
        			solutions.add(polygon.get(j));
        			break;
        		}
        	}
        	
        }
        
        return solutions;
		
	}
	
	public static boolean exportPolygonToTextFile(String filename, ArrayList<Point2d> polygon){
		FileWriter outFile = null;
        try {
        	outFile = new FileWriter(new File("data", filename));
    		PrintWriter out = new PrintWriter(outFile);
    		
    		ArrayList<Point2d> upper= new ArrayList<Point2d>();
    		ArrayList<Point2d> lower= new ArrayList<Point2d>();
    		Point2d previous = polygon.get(0);
    		upper.add(previous);
    		for(int i=1; i<polygon.size();i++){
    			if(previous.x < polygon.get(i).x){
    				upper.add(polygon.get(i));
    			} else {
    				lower.add(polygon.get(i));
    			}
    			previous = polygon.get(i);
    		}
    		
    		Collections.sort(lower);
    		
    		for(int i = 0; i<upper.size(); i++){
    			out.print(upper.get(i).x + ","+upper.get(i).y+ " ");
    		}
    		out.print("; ");
    		for(int i = 0; i<lower.size(); i++){
    			out.print(lower.get(i).x + ","+lower.get(i).y+ " ");
    		}
    		
    		/**
    		Point2d previous = polygon.get(0);
    		out.print(previous.x + ","+previous.y+ " ");
    		int lastCeilingPoint = 0;
    		for(int i = 1; i<polygon.size(); i++){
    			if(previous.x < polygon.get(i).x){
    				out.print(polygon.get(i).x + ","+polygon.get(i).y+ " ");
    				
    			} else {
    				//out.print(polygon.get(i).x + ","+polygon.get(i).y+ " ");
    				lastCeilingPoint= i;
    				
    				break;
    			}
    			previous = polygon.get(i);
    		}
    		out.print("; ");
    		for(int i = polygon.size()-1; i>=lastCeilingPoint; i--){
    			
    			out.print(polygon.get(i).x + ","+polygon.get(i).y+ " ");
    			
    		}*/
    		
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the ObjectOutputStream
            try {
                if(outFile != null)
                	outFile.close();
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Point2d> importPolygonToFile(String filename) throws IOException{
		File file = null;
		if(filename ==null){
			JFileChooser chooser=new  JFileChooser(new File("/Monotone/Montone"));
	        int returnVal = chooser.showOpenDialog(null);
	        if(returnVal == JFileChooser.APPROVE_OPTION) {
	        	file = chooser.getSelectedFile();
			} 
		} else {
			file = new File(filename);
		}
		ArrayList<Point2d> polygon=null;
		if(file == null){
			return polygon;
		}
		ObjectInputStream in = null;
		try {
	        in = new ObjectInputStream(new FileInputStream(file));
	        polygon = (ArrayList<Point2d>) in.readObject();
	    } catch(Exception e) {
	        e.printStackTrace();
	    } finally{
	    	in.close();
	    }
		
		return polygon;
	}

	public static Polygon importPolygonFromTextFile(String filename) throws IOException{
		ArrayList<Point2d> upper = new ArrayList<Point2d>();
        ArrayList<Point2d> lower = new ArrayList<Point2d>();
        File file = null;
		if(filename ==null){
			JFileChooser chooser=new  JFileChooser(new File("/Monotone/Montone"));
	        int returnVal = chooser.showOpenDialog(null);
	        if(returnVal == JFileChooser.APPROVE_OPTION) {
	        	file = chooser.getSelectedFile();
			} 
		} else {
			file = new File("data", filename);
		}
		
        Scanner input = new Scanner(file);
        boolean u = true;
        int idCounter = 1; 
        //read in all values and store into upper, upper2, lower and lower2
        while(input.hasNext()){
            String point = input.next();
            if(point.equals(";")){
                u = false;
                //System.out.println();
                continue;
            }

            String[] coordinates = point.split(",");
            if(u){
                upper.add(new Point2d(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]), idCounter));
                idCounter++;
                
            } else{
                lower.add(new Point2d(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]), idCounter));
                idCounter++;
            }
            //System.out.print(point + " ");
        }
        return new Polygon(upper, lower);
        /*
        Collections.reverse(lower);
        upper.addAll(lower);
        return upper;*/
	}
	
	public static boolean visibilityListToLinearProgram(String filename, Polygon polygon, ArrayList<Point2d>[] visibilityList){
		BufferedWriter bufferedWriter = null;
        
        try {
            
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(new File("data", filename)));
            
            for(int i = 1; i<=polygon.size(); i++){
            	bufferedWriter.write("var x" +i+ " >= 0;");
            	bufferedWriter.newLine();
            }
            bufferedWriter.write("minimize obj: ");
            bufferedWriter.newLine();
            for(int i = 1; i<=polygon.size(); i++){
            	if(i!=polygon.size()){
            		bufferedWriter.write("x" +i+ " + ");
            	} else {
            		bufferedWriter.write("x" +i + ";");
                	bufferedWriter.newLine();
            	}
            	
            }
            
            bufferedWriter.write("# this represents the total number of guards seen by a particular point to be guarded must be more than 1");
            bufferedWriter.newLine();
            
            for(int i = 0; i<visibilityList.length; i++){
            	bufferedWriter.write("s.t. c" +(i+1) + ": ");
				for(int j=0; j < visibilityList[i].size(); j++){
					if(visibilityList[i].size()-1 != j){
						bufferedWriter.write("x"+(polygon.indexOf(visibilityList[i].get(j))+1) + " + ");
					} else {
						bufferedWriter.write("x"+(polygon.indexOf(visibilityList[i].get(j))+1)  +" >= 1;");
					}
				}
				bufferedWriter.newLine();
			}
            //Start writing to the output stream
            bufferedWriter.write("solve;");
            bufferedWriter.newLine();
            bufferedWriter.write("display obj;");
            bufferedWriter.newLine();
            bufferedWriter.write("end;");
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedWriter
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Point2d>[] linearProgramToVisibilityList(String filename, Polygon polygon) throws IOException{
		File fileLP = null;
		if(filename ==null){
			JFileChooser chooser=new  JFileChooser(new File("/Monotone/Montone"));
	        int returnVal = chooser.showOpenDialog(null);
	        if(returnVal == JFileChooser.APPROVE_OPTION) {
	        	fileLP = chooser.getSelectedFile();
			} 
		} else {
			fileLP = new File("data", filename);
		}
		
        Scanner input = new Scanner(fileLP);
        int section = 0;
        
        ArrayList<Point2d>[] visList = new ArrayList[polygon.size()];
        Map<String, Point2d> map = new HashMap<String, Point2d>(5000);
        int visPos = 0;
        
        //read in all values and store into upper, upper2, lower and lower2
        while(input.hasNextLine()){
            String point = input.nextLine();
            String[] coordinates = point.split(" ");
            if(coordinates.length == 0 || coordinates[0].charAt(0) == '#'){
            	continue;
            }
            switch(section){
	            case 0: 
	            	// creates a Map to find which points see each other.
	            	if(coordinates[0].equals("minimize")){
	            		section = 1;
	                    continue;
	                } else {
	                	map.put(coordinates[1], polygon.next());
	                	
	                }
	            	break;
	            case 1:
	            	if(coordinates[0].equals("solve;")){
	            		section = 2;
	            		break;
	            	}
	            	visList[visPos] = new ArrayList<Point2d>();
	            	for(int i =2; i<coordinates.length-2; i++){
	            		if(!coordinates[i].equals("+")){
	            			visList[visPos].add((Point2d) map.get(coordinates[i]));
	            		}
	            	}
	            	visPos++;
	            	break;
	            case 2:
	            	//Reads the rest of the file
	            	break;
            }            
        }
        return visList; //NOT COMPLETED
	}
}
