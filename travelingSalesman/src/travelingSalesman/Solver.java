package travelingSalesman;
import java.awt.Container;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Solver implements Runnable{

	static ArrayList<point> points = new ArrayList<point>();
	static Thread solver;

	private static int number = 5;
	private static int[] currentPath = new int[number];
	private static int[] shortestPath = new int[number];
	
	private static int record = Integer.MAX_VALUE;
	private static double totalPossible = 0;
	private static double totalDone = 0;

	
	protected static void setThread(Thread t) {
		solver = t;
	}
	
	protected static int getNumber() {
		return number;
	}
	
	
	protected static void setPoints(ArrayList<point> p) {
		if(p == null || p.size() == 0) {
			System.out.println("points array is null or of size 0");
			return;
		}
		System.out.println("set points method");
		points = p;
		number = p.size();
		record = Integer.MAX_VALUE;
		currentPath = null;
		shortestPath = null;
		totalPossible = 0;
		totalDone = 0;
	}
	public static int[] getCurrentPath() {
		return currentPath;
	}
	public static int[] getShortestPath() {
		return shortestPath;
	}
	
	public static int getRecord() {
		return record;
	}
	public static int getPercentDone() {
		if(totalPossible == 0) totalPossible = factorial(number);

		int percent = (int)((totalDone / totalPossible) * 100);
		return percent;
	}
	
	private static int factorial(int num) {
		if(num <= 1) return 1;
		else return num * factorial(num - 1);
	}
	
	//time complexity of O(n!)
	private static void LexicographicOrder(boolean[] pathsTaken, int[] path, int index) {
		

		//checks
		if(pathsTaken == null || path == null || index < 0) {
			pathsTaken = new boolean[number];
			path = new int[number];
			index = 0;
		}
		if(index > path.length) return;
		
		//if path is completed
		boolean isComplete = true;
		
		// if index is less than the length of path
		if(index < path.length)
			//loops through pathsTaken to see if a point is not yet used
			for(int index2 = 0; index2 < pathsTaken.length; index2++) {
				
				if(pathsTaken[index2]) continue;

				isComplete = false;
				
				path[index] = index2;
				//pathTaken index is turned true for recursion so that there won't be duplicate paths
				pathsTaken[index2] = true;
				LexicographicOrder(pathsTaken.clone(), path.clone(), index + 1);
				//pathTaken index is turned false for recursion so that future recursion can still use that point
				pathsTaken[index2] = false;
			}
		if(isComplete) {
			recordBreaker(path);
			totalDone++;			
		}
		currentPath = path.clone();
	}
	
	
	
	private static void recordBreaker(int[] path) {

		int distance = calcDistance(path);
//		System.out.println("Distance for path -- " + distance);
		if(record > distance) {
			System.out.println("new recorded distance -- " + distance);
			record = distance;
			shortestPath = path.clone();
		}
	}
	
	
	private static int calcDistance(int[] path) {
		if(path == null || path.length == 0) return Integer.MAX_VALUE;
		int sum = 0;
		for(int index = 0; index < path.length - 1; index++) {
			if(sum > record) return Integer.MAX_VALUE;
			int distx = points.get(path[index]).x - points.get(path[index + 1]).x;
			int disty = points.get(path[index]).y - points.get(path[index + 1]).y;
			sum += (int) Math.sqrt( (distx * distx) + (disty * disty) );
		}	
		return sum;
	}
	
	private static boolean linesCross(int point1, int point2, int point3, int point4) {
		
		
		//point 1 and 2 connect to form a line
		//point 3 and 4 connect to form a line
		
		double x1 = points.get(point1).x;
		double x2 = points.get(point2).x;
		
		double y1 = points.get(point1).y;
		double y2 = points.get(point2).y;
		
		
		
		double x3 = points.get(point3).x;
		double x4 = points.get(point4).x;
		
		double y3 = points.get(point3).y;
		double y4 = points.get(point4).y;
		
		
		System.out.println(
				"x1 -- " + x1 + " y1 -- " + y1 +
				"\nx2 -- " + x2 + " y2 -- " + y2 +
				"\nx3 -- " + x3 + " y3 -- " + y3 +
				"\nx4 -- " + x4 + " y4 -- " + y4  + "\n-----before calculations------");
		
		if(x1 > x2) {
			double temp = x1;
			x1 = x2;
			x2 = temp;
			temp = y1;
			y1 = y2;
			y2 = temp;
		}
		if(x3 > x4) {
			double temp = x3;
			x3 = x4;
			x4 = temp;
			temp = y3;
			y3 = y4;
			y4 = temp;
		}
		
		//both lines are vertical
		if(x1 == x2 && x1 == x3 && x1 == x4) {
			//one line has distance of 0
			if(y1 == y2 || y3 == y4) return false;
			
			//same line
			if((y1 == y3 || y1 == y4) && (y2 == y3 || y2 == y4)) return true;
			
			
			//vertical lines cross
			if(y1 < y3 && y1 > y4) return true;
			if(y2 < y3 && y2 > y4) return true;
			
			return false;
		}
		
		//y = mx + b
		//b = -mx + y
		//(y) / x = m
		
		//assumes x1 and x3 are left most points
		
		double m1;
		double m2;
		double b1;
		double b2;
		double xIntersect;

		//lines are vertical -- cannot divide by 0
		if(x1 == x2 || x3 == x4){
			if(x1 == x2) xIntersect = x1;
			else xIntersect = x3;
		}
		else {
			m1 = (y2 - y1) / (x2 - x1);
			m2 = (y4 - y3) / (x4 - x3);
			b1 = -(m1 * x1) + y1;
			b2 = -(m2 * x3) + y3;
			System.out.println(
					"m1 -- " + m1 +
					"\nm2 -- " + m2);
			//lines are parallel
			if(m1 == m2) return false;

			xIntersect = (b2 - b1) / (m1 - m2);
		}

		
		System.out.println(
				"-----After Calculations-----\nx1 -- " + x1 + " y1 -- " + y1 +
				"\nx2 -- " + x2 + " y2 -- " + y2 +
				"\nx3 -- " + x3 + " y3 -- " + y3 +
				"\nx4 -- " + x4 + " y4 -- " + y4 +
				"\nx intersect -- " + xIntersect + "\n\n");
		if(x1 <= xIntersect && x2 >= xIntersect && x3 <= xIntersect && x4 >= xIntersect) return true;
		
		return false;
	}
	
	

	
	

	
	
	public void run() {
		if(points == null || points.size() == 0 || totalDone != 0) return;
		LexicographicOrder(null, null, 0);
		//opt2();
	}

}