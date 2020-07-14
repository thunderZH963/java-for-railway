import java.util.HashMap;
import java.util.HashSet;

public class UnionFind {
    
    private HashMap<Integer, Integer> id;
    private HashMap<Integer, Integer> size;
    private HashSet<Integer> idNum;
    private int update = 0;
    
    public UnionFind() {
        id = new HashMap<>();
        size = new HashMap<>();
        idNum = new HashSet<>();
    }
    
    public void union(int p, int q) {
        update = 1;
        if (!id.containsKey(p)) {
            id.put(p, p);
            size.put(p, 1);
        }
        if (!id.containsKey(q)) {
            id.put(q, q);
            size.put(q, 1);
        }
        int r1 = find(p);
        int r2 = find(q);
        if (r1 == r2) {
            return;
        }
        if (size.get(r1) < size.get(r2)) {
            id.put(r1, r2);
            size.put(r2,size.get(r2) + size.get(r1));
        } else {
            id.put(r2, r1);
            size.put(r1,size.get(r1) + size.get(r2));
        }
    }
    
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }
    
    public int DistinctId() {
        if (update == 1) {
            updateDistinctId();
            update = 0;
        }
        return idNum.size();
    }
    
    public void setUpdate() {
        update = 1;
    }
    
    private void updateDistinctId() {
        idNum = new HashSet<>();
        for (Integer key : id.keySet()) {
            idNum.add(find(key));
        }
    }
    
    private int find(int q) {
        int p = q;
        while (p != id.get(p)) {
            p = id.get(p);
        }
        return p;
    }
    
}
