import com.oocourse.specs3.models.NodeIdNotFoundException;
import com.oocourse.specs3.models.NodeNotConnectedException;
import com.oocourse.specs3.models.Path;
import com.oocourse.specs3.models.RailwaySystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class MyRailwaySystem extends MyGraph implements RailwaySystem {
    private HashMap<Node, HashMap<Node, Integer>> recodeLeastPrice
            = new HashMap<>();
    private HashMap<Node, HashMap<Node, Integer>> recodeLeastUnple
            = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, Integer>> recodeLeastTrans
            = new HashMap<>();
    
    public MyRailwaySystem() {
        super();
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
        updateContainer();
        if (fromToDist().containsKey(fromNodeId)) {
            if (fromToDist().get(fromNodeId).get(toNodeId)
                    < Integer.MAX_VALUE) {
                return true;
            } else {
                return false;
            }
        } else if (recodeLeastPrice.containsKey(new Node(fromNodeId, 0))) {
            if (recodeLeastPrice.get(new Node(fromNodeId, 0)).
                    get(new Node(toNodeId, -1))
                    < Integer.MAX_VALUE) {
                return true;
            } else {
                return false;
            }
        } else if (recodeLeastUnple.containsKey(new Node(fromNodeId, 0))) {
            if (recodeLeastUnple.get(new Node(fromNodeId, 0)).
                    get(new Node(toNodeId, -1))
                    < Integer.MAX_VALUE) {
                return true;
            } else {
                return false;
            }
        }
        if (getUpdateUf() == 1) {
            updateUf();
            setUpdateUf();
        }
        return ufConnect(fromNodeId, toNodeId);
    }
    
    @Override
    public int getConnectedBlockCount() {
        if (getUpdateUf() == 1) {
            updateUf();
            setUpdateUf();
        }
        return getDistinctId();
    }
    
    @Override
    public int getShortestPathLength(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        if (!containsNode(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        } else if (!containsNode(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        }
        
        if (fromToDist().containsKey(fromNodeId)) {
            if (fromToDist().get(fromNodeId).get(toNodeId)
                    < Integer.MAX_VALUE) {
                return fromToDist().get(fromNodeId).get(toNodeId);
            } else {
                throw new NodeNotConnectedException(fromNodeId, toNodeId);
            }
        }
        updateContainer();
        if (!isConnected(fromNodeId, toNodeId)) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        
        dijsktra(fromNodeId);
        return fromToDist().get(fromNodeId).get(toNodeId);
        
    }
    
    @Override
    public int getLeastTicketPrice(int i, int i1)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        if (!containsNode(i)) {
            throw new NodeIdNotFoundException(i);
        }
        if (!containsNode(i1)) {
            throw new NodeIdNotFoundException(i1);
        }
        updateContainer();
        if (recodeLeastPrice.containsKey(new Node(i, 0))) {
            if (recodeLeastPrice.get(new Node(i, 0)).
                    get(new Node(i1, -1)) < Integer.MAX_VALUE) {
                return recodeLeastPrice.get(
                        new Node(i, 0)).get(new Node(i1, -1));
            } else {
                throw new NodeNotConnectedException(i, i1);
            }
        }
        if (!isConnected(i, i1)) {
            throw new NodeNotConnectedException(i, i1);
        }
        dijForPrice(new Node(i, 0));
        return recodeLeastPrice.get(new Node(i, 0)).get(new Node(i1, -1));
    }
    
    @Override
    public int getLeastTransferCount(int i, int i1)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        if (!containsNode(i)) {
            throw new NodeIdNotFoundException(i);
        }
        if (!containsNode(i1)) {
            throw new NodeIdNotFoundException(i1);
        }
    
        updateContainer();
        
        if (!isConnected(i, i1)) {
            throw new NodeNotConnectedException(i, i1);
        }
        
        int minValue = Integer.MAX_VALUE;
        for (Integer pathId : nodesToPathId().get(i)) {
            if (!recodeLeastTrans.containsKey(pathId)) {
                dijForTrans(pathId);
            }
            for (Integer toId : nodesToPathId().get(i1)) {
                if (recodeLeastTrans.get(pathId).get(toId) < minValue) {
                    minValue = recodeLeastTrans.get(pathId).get(toId);
                }
            }
        }
        //System.out.println(endtime - starttime);
        return minValue;
    }
    
    @Override
    public int getLeastUnpleasantValue(int i, int i1)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        //long starttime = System.currentTimeMillis();
        if (!containsNode(i)) {
            throw new NodeIdNotFoundException(i);
        }
        if (!containsNode(i1)) {
            throw new NodeIdNotFoundException(i1);
        }
    
        updateContainer();
        
        if (recodeLeastUnple.containsKey(new Node(i, 0))) {
            if (recodeLeastUnple.get(new Node(i, 0)).
                    get(new Node(i1, -1)) < Integer.MAX_VALUE) {
                return recodeLeastUnple.get(new Node(i, 0)).
                        get(new Node(i1, -1));
            } else {
                throw new NodeNotConnectedException(i, i1);
            }
        }
        if (!isConnected(i, i1)) {
            throw new NodeNotConnectedException(i, i1);
        }
        
        dijForUnple(new Node(i,0));
        //System.out.println(endtime - starttime);
        return recodeLeastUnple.get(new Node(i, 0)).get(new Node(i1, -1));
    }
    //public int this love,rian or shine has been destined to be forever.
    
    @Override
    public int getUnpleasantValue(Path path, int fromIndex, int toIndex) {
        if (containsPath(path) && fromIndex >= 0
                && fromIndex < toIndex && toIndex < path.size()) {
            int maxValue = 0;
            for (int j = fromIndex; j < toIndex; j++) {
                if (path.getUnpleasantValue(j) > maxValue) {
                    maxValue = path.getUnpleasantValue(j);
                }
                if (path.getUnpleasantValue(j + 1) > maxValue) {
                    maxValue = path.getUnpleasantValue(j + 1);
                }
            }
            return maxValue;
        }
        return -1;
    }
    
    private int getUnpleasantValue(Node from, Node to) {
        if (edgesForUnple().containsKey(from) &&
                edgesForUnple().get(from).containsKey(to)) {
            return edgesForUnple().get(from).get(to);
        } else {
            return Integer.MAX_VALUE;
        }
    }
    
    public void dijsktra(int fromNodeId) {
        HashMap<Integer, Boolean> add = new HashMap<>();
        HashMap<Integer, Integer> dist = new HashMap<>();
        for (Integer key : nodesToPathId().keySet()) {
            add.put(key, false);
            dist.put(key, getLen(fromNodeId, key));
        }
        PriorityQueue<NodeForDijInt> queue = new PriorityQueue<>();
        dist.put(fromNodeId, 0);
        queue.add(new NodeForDijInt(fromNodeId, 0));
        NodeForDijInt tmp;
        while (!queue.isEmpty()) {
            tmp = queue.poll();
            int u = tmp.getNode();
            if (add.get(u)) {
                continue;
            }
            add.put(u, true);
            HashMap<Integer, Integer> temp = fromToCount().get(u);
            for (Integer key : temp.keySet()) {
                if (getLen(u, key)
                        < Integer.MAX_VALUE
                        && dist.get(u) < Integer.MAX_VALUE &&
                        !add.get(key) && dist.get(u) +
                        getLen(u, key)
                        <= dist.get(key)) {
                    dist.put(key, dist.get(u) +
                            getLen(u, key));
                    queue.add(new NodeForDijInt(key, dist.get(key)));
                }
            }
            
        }
        fromToDist().put(fromNodeId, dist);
    }
    
    private void updateContainer() {
        if (getInitDij() == 1) {
            recodeLeastPrice = new HashMap<>();
            recodeLeastUnple = new HashMap<>();
            recodeLeastTrans = new HashMap<>();
            setInitDij();
        }
    }
    
    private void dijForPrice(Node fromNode) {
        HashMap<Node, Boolean> add = new HashMap<>();
        HashMap<Node, Integer> distNode = new HashMap<>();
        for (Node key : nodesAll()) {
            add.put(key, false);
            distNode.put(key, getTicketPrice(fromNode, key));
        }
        PriorityQueue<NodeForDij> queue = new PriorityQueue<>();
        distNode.put(fromNode, 0);
        queue.add(new NodeForDij(fromNode, 0));
        NodeForDij tmp;
        while (!queue.isEmpty()) {
            tmp = queue.poll();
            Node u = tmp.getNode();
            if (add.get(u)) {
                continue;
            }
            add.put(u, true);
            HashMap<Node, Integer> temp = edgesForPrice().get(u);
            for (Node key : temp.keySet()) {
                if (getTicketPrice(u, key)
                        < Integer.MAX_VALUE
                        && distNode.get(u) < Integer.MAX_VALUE &&
                    !add.get(key) && distNode.get(u) +
                        getTicketPrice(u, key)
                        <= distNode.get(key)) {
                    distNode.put(key, distNode.get(u) +
                            getTicketPrice(u, key));
                    queue.add(new NodeForDij(key, distNode.get(key)));
                }
            }
        }
        recodeLeastPrice.put(fromNode, distNode);
    }
    
    private void dijForUnple(Node fromNode) {
        HashMap<Node, Boolean> add = new HashMap<>();
        HashMap<Node, Integer> distNode = new HashMap<>();
        for (Node key : nodesAll()) {
            add.put(key, false);
            distNode.put(key, getUnpleasantValue(fromNode, key));
        }
        PriorityQueue<NodeForDij> queue = new PriorityQueue<>();
        distNode.put(fromNode, 0);
        queue.add(new NodeForDij(fromNode, 0));
        NodeForDij tmp;
        while (!queue.isEmpty()) {
            tmp = queue.poll();
            Node u = tmp.getNode();
            if (add.get(u)) {
                continue;
            }
            add.put(u, true);
            HashMap<Node, Integer> temp = edgesForUnple().get(u);
            for (Node key : temp.keySet()) {
                if (getUnpleasantValue(u, key)
                        < Integer.MAX_VALUE
                        && distNode.get(u) < Integer.MAX_VALUE &&
                        !add.get(key) && distNode.get(u) +
                        getUnpleasantValue(u, key)
                        <= distNode.get(key)) {
                    distNode.put(key, distNode.get(u) +
                            getUnpleasantValue(u, key));
                    queue.add(new NodeForDij(key, distNode.get(key)));
                }
            }
        }
        recodeLeastUnple.put(fromNode, distNode);
    }
    
    private void dijForTrans(Integer pathId) {
        HashMap<Integer, Boolean> add = new HashMap<>();
        HashMap<Integer, Integer> dist = new HashMap<>();
        for (Integer key : pathEdges().keySet()) {
            add.put(key, false);
            dist.put(key, getPathLen(pathId, key));
        }
        PriorityQueue<NodeForDijInt> queue = new PriorityQueue<>();
        dist.put(pathId, 0);
        queue.add(new NodeForDijInt(pathId, 0));
        NodeForDijInt tmp;
        while (!queue.isEmpty()) {
            tmp = queue.poll();
            int u = tmp.getNode();
            if (add.get(u)) {
                continue;
            }
            add.put(u, true);
            HashSet<Integer> temp = pathEdges().get(u);
            for (Integer key : temp) {
                if (getPathLen(u, key)
                        < Integer.MAX_VALUE
                        && dist.get(u) < Integer.MAX_VALUE &&
                        !add.get(key) && dist.get(u) +
                        getPathLen(u, key)
                        <= dist.get(key)) {
                    dist.put(key, dist.get(u) +
                            getPathLen(u, key));
                    queue.add(new NodeForDijInt(key, dist.get(key)));
                }
            }
        
        }
        recodeLeastTrans.put(pathId, dist);
    }
    
    /* the below is the weight
     * for different Dijsktra
     */
    private int getLen(int from, int to) {
        if (from == to) {
            return 0;
        }
        if (!fromToCount().containsKey(from)) {
            return Integer.MAX_VALUE;
        } else if (fromToCount().get(from).containsKey(to)) {
            return 1;
        } else {
            return Integer.MAX_VALUE;
        }
    }
    
    private int getPathLen(int from, int to) {
        if (from == to) {
            return 0;
        }
        if (pathEdges().containsKey(from)) {
            if (pathEdges().get(from).contains(to)) {
                return 1;
            } else {
                return Integer.MAX_VALUE;
            }
        } else {
            return Integer.MAX_VALUE;
        }
    }
    
    private int getTicketPrice(Node from, Node to) {
        if (edgesForPrice().containsKey(from) &&
                edgesForPrice().get(from).containsKey(to)) {
            return edgesForPrice().get(from).get(to);
        } else {
            return Integer.MAX_VALUE;
        }
    }
    
}
