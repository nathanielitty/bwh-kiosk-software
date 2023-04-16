package edu.wpi.teamR.pathfinding;

import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.mapdb.MapDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class BFSSearch extends SearchAlgorithm{
    BFSSearch(MapDatabase mapDatabase) {
        super(mapDatabase);
    }

    public Path getPath(int startID, int endID, boolean accessible) throws SQLException, ItemNotFoundException {
        Path path = new Path();
        HashMap<Integer, Integer> cameFrom = new HashMap<>();
        LinkedList<Integer> queue = new LinkedList<>(mapDatabase.getAdjacentNodeIDsByNodeID(startID));
        Integer currentNode;
        while (!queue.isEmpty()) {
            currentNode = queue.remove();
            if (currentNode == endID) {
                break;
            }
            ArrayList<Integer> neighbors = mapDatabase.getAdjacentNodeIDsByNodeID(currentNode);
            for (Integer neighbor : neighbors) {
                if (!cameFrom.containsKey(neighbor)) {
                    queue.addLast(neighbor);
                    cameFrom.put(neighbor, currentNode);
                }
            }
        }

        currentNode = endID;
        while (currentNode != startID) {
            path.add(currentNode);
            currentNode = cameFrom.get(currentNode);
        }

        return path;
    }
}