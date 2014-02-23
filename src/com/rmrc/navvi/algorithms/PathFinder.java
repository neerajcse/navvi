package com.rmrc.navvi.algorithms;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rmrf.navvi.data.structure.Pair;

public class PathFinder {

	List<String> findShortestPath(String start, String end, JSONObject adjacencyObj) {

		List< Pair<String, List<String> > > queue = new LinkedList< Pair<String, List<String> > >();
		Pair<String, List<String> > source = new Pair<String, List<String> >(start, new LinkedList<String>());
		queue.add(source);

		while(!queue.isEmpty()) {
			Pair<String, List<String> > current = queue.remove(0);
			String currentNode = current.getFirst();
			List<String> currentPath = current.getSecond();
			if (currentNode.equals(end)) {
				return currentPath;
			} else {
				try {
					if (adjacencyObj.has(currentNode)) {
						JSONArray adjacents = adjacencyObj.getJSONArray(currentNode);
						for(int i = 0 ; i < adjacents.length() ; i++) {
							String adjacent = adjacents.getString(i);
							List<String> path = new LinkedList<String>(currentPath);
							path.add(currentNode);
							Pair<String, List<String> > pair = new Pair<String, List<String> >(adjacent, path);
							queue.add(pair);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println("Starting");
		PathFinder pathFinder = new PathFinder();
		String start = "A";
		String end = "B";
		String adjacency_string = "{\"A\":[\"C\", \"D\"], \"C\": [\"E\"],\"E\":[\"B\"]}";
		JSONObject adjacencyObj = null;
		try {
			adjacencyObj = new JSONObject(adjacency_string);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> path = pathFinder.findShortestPath(start, end, adjacencyObj);
		System.out.println(path);
	}

}
