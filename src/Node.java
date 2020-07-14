public class Node {
    private int nodeId;
    //if 起点/终点 pathId = 0/-1
    private int pathId;
    
    public Node(int nodeId, int pathId) {
        this.nodeId = nodeId;
        this.pathId = pathId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Node) {
            return ((Node)obj).getNodeId() == nodeId &&
                    ((Node)obj).getPathId() == pathId;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return nodeId;
    }
    
    public int getNodeId() {
        return nodeId;
    }
    
    public int getPathId() {
        return pathId;
    }
    
}
