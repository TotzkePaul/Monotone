package heuristic;

import geometry.Point2d;

import java.util.ArrayList;

public abstract class Search implements Runnable {
	abstract ArrayList<Point2d> getCurrentSolution();
}
