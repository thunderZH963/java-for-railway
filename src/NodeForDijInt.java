public class NodeForDijInt implements Comparable<NodeForDijInt> {
    private int node;
    private int weight;
    
    public NodeForDijInt(int node, int weight) {
        this.node = node;
        this.weight = weight;
    }
    
    public int compareTo(NodeForDijInt o) {
        if (weight > o.getWeight()) {
            return 1;
        } else if (weight == o.getWeight()) {
            return 0;
        } else {
            return -1;
        }
    }
    
    public int getWeight() {
        return weight;
    }
    
    public int getNode() {
        return node;
    }
}
