public class NodeForDij implements Comparable<NodeForDij> {
    private Node node;
    private int weight;
    
    public NodeForDij(Node node, int weight) {
        this.node = node;
        this.weight = weight;
    }
    
    public int compareTo(NodeForDij o) {
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
    
    public Node getNode() {
        return node;
    }
}
