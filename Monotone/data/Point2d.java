package data;

public class Point2d implements Comparable<Object>{
	
	public double x;
	public double y;
	public int id; 
	public Point2d(double x, double y){
		this.x = x;
		this.y = y;
		
	}
	
	public Point2d(double x, double y, int id){
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public int getID(){
		return id;
	}
	
	
	public int compareTo(Object obj){
		if(this.x < ((Point2d) obj).x){
			return -1;
		}
		if(this.x == ((Point2d) obj).x){
			return 0;
		}
		if(this.x > ((Point2d) obj).x){
			return 1;
		}
		return 0;
	}
}

