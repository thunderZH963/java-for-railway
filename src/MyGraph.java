import com.oocourse.specs3.models.Graph;
import com.oocourse.specs3.models.Path;
import com.oocourse.specs3.models.PathIdNotFoundException;
import com.oocourse.specs3.models.PathNotFoundException;
import com.oocourse.specs3.models.NodeIdNotFoundException;
import com.oocourse.specs3.models.NodeNotConnectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MyGraph implements Graph {
    private int initDij = 0;
    private HashMap<Node, HashMap<Node, Integer>> edgeForPrice
            = new HashMap<>();
    private HashMap<Node, HashMap<Node, Integer>> edgeForUnple
            = new HashMap<>();
    private HashSet<Node> nodesAll = new HashSet<>();
    private HashMap<Integer, HashSet> pathEdges
            = new HashMap<>();
    
    private int nowId = 0;
    private HashMap<Integer, Path> idToPath = new HashMap<>();
    private HashMap<Path, Integer> pathToId = new HashMap<>();
    private HashMap<Integer, ArrayList<Integer>> nodesToPathId
            = new HashMap<>();
    private HashSet<Integer> nodes = new HashSet<>();
    private HashMap<Integer, HashMap<Integer, Integer>> fromToCount
            = new HashMap<>();
    private UnionFind uf = new UnionFind();
    private int updateUf = 0;
    private HashMap<Integer, HashMap<Integer, Integer>> fromToDist
            = new HashMap<>();
    
    public MyGraph() { }
    
    @Override
    public int size() {
        return idToPath.size();
    }
    
    @Override
    public boolean containsPath(Path path) {
        return pathToId.containsKey(path);
    }
    
    @Override
    public boolean containsPathId(int pathId) {
        if (idToPath.containsKey(pathId)) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public Path getPathById(int pathId) throws PathIdNotFoundException {
        if (!containsPathId(pathId)) {
            throw new PathIdNotFoundException(pathId);
        }
        return idToPath.get(pathId);
    }
    
    @Override
    public int getPathId(Path path) throws PathNotFoundException {
        if (path == null || !path.isValid() || !containsPath(path)) {
            throw new PathNotFoundException(path);
        }
        return pathToId.get(path);
    }
    
    @Override
    public int addPath(Path path) {
        if (path == null || !path.isValid()) {
            return 0;
        }
        if (!containsPath(path)) {
            fromToDist = new HashMap<>();
            initDij = 1;
            nowId++;
            idToPath.put(nowId, path);
            pathToId.put(path, nowId);
            for (int i = 0; i < path.size(); i++) {
                addNode(path.getNode(i), pathToId.get(path));
                nodes.add(path.getNode(i));
                
                if (i < path.size() - 1) {
                    if (updateUf == 0) {
                        uf.union(path.getNode(i), path.getNode(i + 1));
                    }
                    addPlatEdge(path.getNode(i), path.getNode(i + 1),
                            pathToId.get(path));
                    addPlatEdge(path.getNode(i + 1), path.getNode(i),
                            pathToId.get(path));
                    addEdge(path.getNode(i), path.getNode(i + 1), nowId);
                    addEdge(path.getNode(i + 1), path.getNode(i), nowId);
                }
                if (nodesToPathId.containsKey(path.getNode(i))) {
                    ArrayList temp = nodesToPathId.get(path.getNode(i));
                    temp.add(pathToId.get(path));
                    nodesToPathId.put(path.getNode(i), temp);
                } else {
                    ArrayList temp = new ArrayList();
                    temp.add(pathToId.get(path));
                    nodesToPathId.put(path.getNode(i), temp);
                }
            }
            addPathEdge(path);
        }
        return pathToId.get(path);
    }
    
    @Override
    public int removePath(Path path) throws PathNotFoundException {
        if (path == null || path.isValid() == false || !containsPath(path)) {
            throw new PathNotFoundException(path);
        }
        uf.setUpdate();
        updateUf = 1;
        fromToDist = new HashMap<>();
        initDij = 1;
        for (int i = 0; i < path.size(); i++) {
            removeNode(path.getNode(i), pathToId.get(path));
            if (i < path.size() - 1) {
                removeEdge(path.getNode(i), path.getNode(i + 1),
                        pathToId.get(path));
                removeEdge(path.getNode(i + 1), path.getNode(i),
                        pathToId.get(path));
            }
            ArrayList temp = nodesToPathId.get(path.getNode(i));
            temp.remove(pathToId.get(path));
            nodesToPathId.put(path.getNode(i), temp);
            if (temp.size() == 0) {
                nodesToPathId.remove(path.getNode(i));
                nodes.remove(path.getNode(i));
                nodesAll.remove(new Node(path.getNode(i), 0));
                nodesAll.remove(new Node(path.getNode(i), -1));
                edgeForUnple.remove(new Node(path.getNode(i), -1));
                edgeForPrice.remove(new Node(path.getNode(i), -1));
            }
        }
        removePathEdge(path);
        int temp = pathToId.get(path);
        pathToId.remove(path);
        idToPath.remove(temp);
        return temp;
    }
    
    @Override
    public void removePathById(int pathId) throws PathIdNotFoundException {
        if (!containsPathId(pathId)) {
            throw new PathIdNotFoundException(pathId);
        }
        uf.setUpdate();
        updateUf = 1;
        fromToDist = new HashMap<>();
        initDij = 1;
        Path path = idToPath.get(pathId);
        for (int i = 0; i < path.size(); i++) {
            removeNode(path.getNode(i), pathToId.get(path));
            if (i < path.size() - 1) {
                removeEdge(path.getNode(i), path.getNode(i + 1),
                        pathToId.get(path));
                removeEdge(path.getNode(i + 1), path.getNode(i),
                        pathToId.get(path));
            }
            ArrayList temp = nodesToPathId.get(path.getNode(i));
            temp.remove(pathToId.get(path));
            nodesToPathId.put(path.getNode(i), temp);
            if (temp.size() == 0) {
                nodesToPathId.remove(path.getNode(i));
                nodes.remove(path.getNode(i));
                nodesAll.remove(new Node(path.getNode(i), -1));
                nodesAll.remove(new Node(path.getNode(i), 0));
                edgeForUnple.remove(new Node(path.getNode(i), -1));
                edgeForPrice.remove(new Node(path.getNode(i), -1));
            }
        }
        removePathEdge(path);
        int temp = pathToId.get(path);
        pathToId.remove(path);
        idToPath.remove(temp);
    }
    
    @Override
    public int getDistinctNodeCount() {
        return nodesToPathId.size();
    }
    
    @Override
    public boolean containsNode(int nodeId) {
        return nodesToPathId.containsKey(nodeId);
    }
    
    @Override
    public boolean containsEdge(int fromNodeId, int toNodeId) {
        return fromToCount.containsKey(fromNodeId)
                && fromToCount.get(fromNodeId).containsKey(toNodeId);
    }
    
    @Override
    public boolean isConnected(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException {
        if (!containsNode(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        if (!containsNode(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        }
        if (fromNodeId == toNodeId) {
            return true;
        }
        if (!fromToCount.containsKey(fromNodeId)) {
            return false;
        }
        if (fromToDist.containsKey(fromNodeId)) {
            if (fromToDist.get(fromNodeId).get(toNodeId) < Integer.MAX_VALUE) {
                return true;
            } else {
                return false;
            }
        }
        if (updateUf == 1) {
            updateUf();
            updateUf = 0;
        }
        return uf.connected(fromNodeId, toNodeId);
    }
    
    public void updateUf() {
        uf = new UnionFind();
        for (Integer key : fromToCount.keySet()) {
            for (Integer otherKey : fromToCount.get(key).keySet()) {
                uf.union(key, otherKey);
            }
        }
    }
    
    @Override
    public int getShortestPathLength(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        return 0;
    }
    
    public void dijsktra(int from) {
        return;
    }
    
    private void addEdge(int from, int to, int pathId) {
        if (fromToCount.containsKey(from)) {
            HashMap<Integer, Integer> temp = fromToCount.get(from);
            if (temp.containsKey(to)) {
                int count = temp.get(to);
                count++;
                temp.remove(to);
                temp.put(to, count);
                fromToCount.remove(from);
                fromToCount.put(from, temp);
            } else {
                temp.remove(to);
                temp.put(to, 1);
                fromToCount.remove(from);
                fromToCount.put(from, temp);
            }
        } else {
            HashMap<Integer, Integer> temp = new HashMap<>();
            temp.put(to, 1);
            fromToCount.put(from, temp);
        }
    }
    
    private void addNode(int nodeId, int pathId) {
        Node newPlat = new Node(nodeId, pathId);
        Node newBegin = new Node(nodeId, 0);
        Node newEnd = new Node(nodeId, -1);
        nodesAll.add(newPlat);
        nodesAll.add(newBegin);
        nodesAll.add(newEnd);
        //for price
       
        HashMap temp = new HashMap();
        temp.put(newBegin, 2);
        edgeForPrice.put(newEnd, temp);
        
        temp = new HashMap<>();
        if (edgeForPrice.containsKey(newBegin)) {
            temp = edgeForPrice.get(newBegin);
        }
        temp.put(newPlat, 0);
        edgeForPrice.put(newBegin, temp);
       
        temp = new HashMap();
        if (edgeForPrice.containsKey(newPlat)) {
            temp = edgeForPrice.get(newPlat);
        }
        temp.put(newEnd, 0);
        edgeForPrice.put(newPlat, temp);
        
        temp = new HashMap();
        //for unpleasant
        temp.put(newBegin, 32);
        edgeForUnple.put(newEnd, temp);
        
        temp = new HashMap<>();
        if (edgeForUnple.containsKey(newBegin)) {
            temp = edgeForUnple.get(newBegin);
        }
        temp.put(newPlat, 0);
        edgeForUnple.put(newBegin, temp);
        
        temp = new HashMap();
        if (edgeForUnple.containsKey(newPlat)) {
            temp = edgeForUnple.get(newPlat);
        }
        temp.put(newEnd, 0);
        edgeForUnple.put(newPlat, temp);
    }
    
    private void addPlatEdge(int from, int to, int pathId) {
        Node newPlatFrom = new Node(from, pathId);
        Node newPlatTo = new Node(to, pathId);
        if (newPlatFrom.equals(newPlatTo)) {
            return;
        }
        HashMap temp = new HashMap();
        if (edgeForPrice.containsKey(newPlatFrom)) {
            temp = edgeForPrice.get(newPlatFrom);
        }
        temp.put(newPlatTo, 1);
        edgeForPrice.put(newPlatFrom, temp);
        
        temp = new HashMap();
        if (edgeForUnple.containsKey(newPlatFrom)) {
            temp = edgeForUnple.get(newPlatFrom);
        }
        int fromValue = (int) Math.
                pow(4, (from % 5 + 5) % 5);
        int toValue = (int) Math.
                pow(4, (to % 5 + 5) % 5);
        if (fromValue > toValue) {
            temp.put(newPlatTo, fromValue);
        } else {
            temp.put(newPlatTo, toValue);
        }
        edgeForUnple.put(newPlatFrom, temp);
    }
    
    private void addPathEdge(Path from) {
        HashSet temp1 = new HashSet();
        HashSet temp2;
        for (Path key : pathToId.keySet()) {
            if (!key.equals(from)) {
                for (int i = 0; i < from.size(); i++) {
                    if (key.containsNode(from.getNode(i))) {
                        temp1.add(pathToId.get(key));
                        temp2 = pathEdges.get(pathToId.get(key));
                        temp2.add(pathToId.get(from));
                        pathEdges.put(pathToId.get(key), temp2);
                        break;
                    }
                }
            }
        }
        pathEdges.put(pathToId.get(from), temp1);
    }
    
    private void removeEdge(int from, int to, int pathId) {
        HashMap<Integer, Integer> temp = fromToCount.get(from);
        int count = temp.get(to);
        count--;
        temp.remove(to);
        if (count != 0) {
            temp.put(to, count);
        }
        fromToCount.remove(from);
        if (temp.size() != 0) {
            fromToCount.put(from, temp);
        }
    }
    
    private void removeNode(int nodeId, int pathId) {
        Node newPlat = new Node(nodeId, pathId);
        Node newBegin = new Node(nodeId, 0);
        if (edgeForPrice.containsKey(newBegin)) {
            HashMap temp = edgeForPrice.get(newBegin);
            temp.remove(newPlat);
            edgeForPrice.remove(newBegin);
            if (temp.size() != 0) {
                edgeForPrice.put(newBegin, temp);
            }
        }
        edgeForPrice.remove(newPlat);
        nodesAll.remove(newPlat);
        
        if (edgeForUnple.containsKey(newBegin)) {
            HashMap temp = edgeForUnple.get(newBegin);
            temp.remove(newPlat);
            edgeForUnple.remove(newBegin);
            if (temp.size() != 0) {
                edgeForUnple.put(newBegin, temp);
            }
        }
        edgeForUnple.remove(newPlat);
    }
    
    private void removePathEdge(Path from) {
        pathEdges.remove(pathToId.get(from));
        for (Integer pathId : pathEdges.keySet()) {
            pathEdges.get(pathId).remove(pathToId.get(from));
        }
    }
    
    public int getUpdateUf() {
        return updateUf;
    }
    
    public void setUpdateUf() {
        updateUf = 0;
    }
    
    public int getDistinctId() {
        return uf.DistinctId();
    }
    
    public HashSet<Node> nodesAll() {
        return nodesAll;
    }
    
    public HashMap<Node, HashMap<Node, Integer>> edgesForUnple() {
        return edgeForUnple;
    }
    
    public HashMap<Node, HashMap<Node, Integer>> edgesForPrice() {
        return edgeForPrice;
    }
    
    public HashMap<Integer, HashSet> pathEdges() {
        return pathEdges;
    }
    
    public HashMap<Integer, HashMap<Integer, Integer>> fromToDist() {
        return fromToDist;
    }
    
    public HashMap<Integer, ArrayList<Integer>> nodesToPathId() {
        return nodesToPathId;
    }
    
    public HashMap<Integer, HashMap<Integer, Integer>> fromToCount() {
        return fromToCount;
    }
    
    public int getInitDij() {
        return initDij;
    }
    
    public void setInitDij() {
        initDij = 0;
    }
    
    public boolean ufConnect(int from, int to) {
        return uf.connected(from, to);
    }
    
}
