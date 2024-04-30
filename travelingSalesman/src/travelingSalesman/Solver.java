package travelingSalesman;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Solver implements Runnable {

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
		if (p == null || p.size() == 0) {
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
		if (totalPossible == 0)
			totalPossible = factorial(number);

		int percent = (int) ((totalDone / totalPossible) * 100);
		return percent;
	}

	private static int factorial(int num) {
		if (num <= 1)
			return 1;
		else
			return num * factorial(num - 1);
	}

	// time complexity of O(n!)
	private static void LexicographicOrder(boolean[] pathsTaken, int[] path, int index) {

		// checks
		if (pathsTaken == null || path == null || index < 0) {
			pathsTaken = new boolean[number];
			path = new int[number];
			index = 0;
		}
		if (index > path.length)
			return;

		// if path is completed
		boolean isComplete = true;

		// if index is less than the length of path
		if (index < path.length)
			// loops through pathsTaken to see if a point is not yet used
			for (int index2 = 0; index2 < pathsTaken.length; index2++) {

				if (pathsTaken[index2])
					continue;

				isComplete = false;

				path[index] = index2;
				// pathTaken index is turned true for recursion so that there won't be duplicate
				// paths
				pathsTaken[index2] = true;
				LexicographicOrder(pathsTaken.clone(), path.clone(), index + 1);
				// pathTaken index is turned false for recursion so that future recursion can
				// still use that point
				pathsTaken[index2] = false;
			}
		if (isComplete) {
			recordBreaker(path);
			totalDone++;
		}
		currentPath = path.clone();
	}

	private static void recordBreaker(int[] path) {

		int distance = calcDistance(path);
		// System.out.println("Distance for path -- " + distance);
		if (record > distance) {
			System.out.println("new recorded distance -- " + distance);
			record = distance;
			shortestPath = path.clone();
		}
	}

	private static int calcDistance(int[] path) {
		if (path == null || path.length == 0)
			return Integer.MAX_VALUE;
		int sum = 0;
		for (int index = 0; index < path.length - 1; index++) {
			// if(sum > record) return Integer.MAX_VALUE;
			int distx = points.get(path[index]).x - points.get(path[index + 1]).x;
			int disty = points.get(path[index]).y - points.get(path[index + 1]).y;
			sum += (int) Math.sqrt((distx * distx) + (disty * disty));
		}
		int distx = points.get(path[0]).x - points.get(path[path.length - 1]).x;
		int disty = points.get(path[0]).y - points.get(path[path.length - 1]).y;
		sum += (int) Math.sqrt((distx * distx) + (disty * disty));
		return sum;
	}

	private static boolean linesCross(int point1, int point2, int point3, int point4) {

		// point 1 and 2 connect to form a line
		// point 3 and 4 connect to form a line

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
						"\nx4 -- " + x4 + " y4 -- " + y4 + "\n-----before calculations------");

		if (x1 > x2) {
			double temp = x1;
			x1 = x2;
			x2 = temp;
			temp = y1;
			y1 = y2;
			y2 = temp;
		}
		if (x3 > x4) {
			double temp = x3;
			x3 = x4;
			x4 = temp;
			temp = y3;
			y3 = y4;
			y4 = temp;
		}

		// both lines are vertical
		if (x1 == x2 && x1 == x3 && x1 == x4) {
			// one line has distance of 0
			if (y1 == y2 || y3 == y4)
				return false;

			// same line
			if ((y1 == y3 || y1 == y4) && (y2 == y3 || y2 == y4))
				return true;

			// vertical lines cross
			if (y1 < y3 && y1 > y4)
				return true;
			if (y2 < y3 && y2 > y4)
				return true;

			return false;
		}

		// y = mx + b
		// b = -mx + y
		// (y) / x = m

		// assumes x1 and x3 are left most points

		double m1;
		double m2;
		double b1;
		double b2;
		double xIntersect;

		// lines are vertical -- cannot divide by 0
		if (x1 == x2 || x3 == x4) {
			if (x1 == x2)
				xIntersect = x1;
			else
				xIntersect = x3;
		} else {
			m1 = (y2 - y1) / (x2 - x1);
			m2 = (y4 - y3) / (x4 - x3);
			b1 = -(m1 * x1) + y1;
			b2 = -(m2 * x3) + y3;
			System.out.println(
					"m1 -- " + m1 +
							"\nm2 -- " + m2);
			// lines are parallel
			if (m1 == m2)
				return false;

			xIntersect = (b2 - b1) / (m1 - m2);
		}

		System.out.println(
				"-----After Calculations-----\nx1 -- " + x1 + " y1 -- " + y1 +
						"\nx2 -- " + x2 + " y2 -- " + y2 +
						"\nx3 -- " + x3 + " y3 -- " + y3 +
						"\nx4 -- " + x4 + " y4 -- " + y4 +
						"\nx intersect -- " + xIntersect + "\n\n");
		if (x1 <= xIntersect && x2 >= xIntersect && x3 <= xIntersect && x4 >= xIntersect)
			return true;

		return false;
	}

	int num_of_solutions = 1000;
	int num_of_generations = 1500;
	ArrayList<solution> solutions = new ArrayList<solution>();
	ArrayList<solution> ranked_solutions = new ArrayList<>();

	public void run() {
		if (points == null || points.size() == 0 || totalDone != 0)
			return;
		// LexicographicOrder(null, null, 0);
		// opt2();

		generate();
		for (int index = 0; index < num_of_generations; index++) {
			fitness(index);
			new_generation();
		}

	}

	/*
	 * -- CREATE --
	 * create a random set of paths
	 */
	private void generate() {
		solutions = new ArrayList<solution>();

		// loop through num of solutions times
		for (int index = 0; index < num_of_solutions; index++) {
			// create new path
			int[] path = new int[points.size()];
			// create a list of possible points for path to go
			ArrayList<Integer> possible_points = new ArrayList<Integer>();
			for (int i = 0; i < points.size(); i++) {
				possible_points.add(i);
			}

			// loop through until all possible points are removed -- path is complete
			for (int path_index = 0; possible_points.size() > 0; path_index++) {
				int point_index = (int) (Math.random() * possible_points.size());

				path[path_index] = possible_points.get(point_index);
				possible_points.remove(point_index);

			}
			solution sol = new solution(0, path);
			solutions.add(sol);
		}
	}

	/*
	 * -- TEST --
	 * measure distance of current path
	 * rank paths based on distance
	 */
	private void fitness(int i) {

		ranked_solutions = new ArrayList<solution>();

		// calculate fitness for all paths created
		for (int index = 0; index < solutions.size(); index++) {
			int distance = calcDistance(solutions.get(index).path);
			solutions.get(index).fitness = distance;
			ranked_solutions.add(solutions.get(index));

		}

		// sort solutions based off of fitness
		Collections.sort(ranked_solutions);

		// update shortest path and current path
		// System.out.println("--- SHORTEST PATH ---");
		int[] best_path = ranked_solutions.get(0).path;
		// for(int index = 0; index < best_path.length; index++) {
		// System.out.print(best_path[index] + " ");
		// }
		System.out.println();

		shortestPath = best_path;
		currentPath = best_path;
		totalPossible = num_of_generations;
		totalDone = i;
		if (record < ranked_solutions.get(0).fitness)
			record = -1;
		else if (record == -1)
			return;
		else
			record = ranked_solutions.get(0).fitness;

	}

	/*
	 * -- NEW --
	 * save best path
	 * create new paths to test based off the best few paths with slight variations
	 */
	private void new_generation() {
		solutions = new ArrayList<solution>();

		solution best_path = ranked_solutions.get(0);
		solutions.add(best_path);

		// only use top half of solutions
		ArrayList<solution> surviving_paths = new ArrayList<>();
		for (int index = 0; index < ranked_solutions.size() / 2; index++)
			surviving_paths.add(ranked_solutions.get(index));

		// create new paths
		for (int index = 0; index < surviving_paths.size(); index++) {
			int[] old_path = surviving_paths.get(index).path.clone();

			while ((int) (Math.random() * 100) % 2 != 0) {
				// mutate path
				int p1 = (int) (Math.random() * old_path.length);
				int p2 = (int) (Math.random() * old_path.length);

				int temp = old_path[p1];
				old_path[p1] = old_path[p2];
				old_path[p2] = temp;
			}
			solution new_path = new solution(0, old_path);

			solutions.add(new_path);

		}
		while (solutions.size() < num_of_solutions) {
			int[] path = new int[points.size()];
			// create a list of possible points for path to go
			ArrayList<Integer> possible_points = new ArrayList<Integer>();
			for (int i = 0; i < points.size(); i++) {
				possible_points.add(i);
			}

			// loop through until all possible points are removed -- path is complete
			for (int path_index = 0; possible_points.size() > 0; path_index++) {
				int point_index = (int) (Math.random() * possible_points.size());

				path[path_index] = possible_points.get(point_index);
				possible_points.remove(point_index);

			}
			solution sol = new solution(0, path);
			solutions.add(sol);
		}

	}

	class solution implements Comparable<solution> {
		int fitness = 0;
		int[] path;

		public solution(int fitness, int[] path) {
			this.fitness = fitness;
			this.path = path;
		}

		@Override
		public int compareTo(solution other) {
			return Double.compare(this.fitness, other.fitness);
		}
	}

}