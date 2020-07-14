import com.oocourse.specs3.models.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class MyPath implements Path {
    private ArrayList<Integer> nodes = new ArrayList<>();
    private HashSet<Integer> nodesset = new HashSet<>();
    
    public MyPath(int... nodeList) {
        for (int i = 0; i < nodeList.length; i++) {
            nodes.add(nodeList[i]);
            nodesset.add(nodeList[i]);
        }
    }
    
    @Override
    public int size() {
        return nodes.size();
    }
    
    @Override
    public int getNode(int index) {
        /*if (index < 0 || index >= size()) {
            return 0;
        }*/
        return nodes.get(index);
    }
    
    @Override
    public boolean containsNode(int node) {
        return nodesset.contains(node);
    }
    
    @Override
    public int getDistinctNodeCount() {
        return nodesset.size();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Path) {
            int i;
            for (i = 0; i < nodes.size() && i < ((Path) obj).size(); i++) {
                if (nodes.get(i) != ((Path) obj).getNode(i)) {
                    return false;
                }
            }
            if (nodes.size() == ((Path) obj).size()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public boolean isValid() {
        return nodes.size() >= 2;
    }
    
    @Override
    public int compareTo(Path o) {
        int i = 0;
        while (i < o.size() && i < size() && o.getNode(i) == nodes.get(i)) {
            i++;
        }
        if (i == o.size() && o.size() == size()) {
            return 0;
        } else if (i != o.size() && i != size()) {
            if (o.getNode(i) > nodes.get(i)) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (o.size() > size()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
    
    @Override
    public int hashCode() {
        return nodes.hashCode();
    }
    
    @Override
    public Iterator<Integer> iterator() {
        return nodes.iterator();
    }
    
    public int getUnpleasantValue(int nodeId) {
        if (!containsNode(nodeId)) {
            return 0;
        } else {
            double a = Math.pow(4, (nodeId % 5 + 5) % 5);
            return (int)a;
        }
    }
    
    public HashSet<Integer> getNodesset() {
        return nodesset;
    }
    
}
