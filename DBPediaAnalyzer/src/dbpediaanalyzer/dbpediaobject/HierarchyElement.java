package dbpediaanalyzer.dbpediaobject;

import java.util.*;

/**
 * TODO JAVADOC
 *
 * @author Pierre Monnin
 *
 */
public abstract class HierarchyElement {
    private String uri;
    private ArrayList<HierarchyElement> parents;
    private ArrayList<HierarchyElement> children;

    public HierarchyElement(String uri) {
        this.uri = uri;
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public void addParent(HierarchyElement parent) {
        if(!this.parents.contains(parent)) {
            this.parents.add(parent);
        }
    }

    public void addChild(HierarchyElement child) {
        if(!this.children.contains(child)) {
            this.children.add(child);
        }
    }

    public String getUri() {
        return this.uri;
    }

    public Collection<HierarchyElement> getParents() {
        return new ArrayList<>(this.parents);
    }

    public Collection<HierarchyElement> getChildren() {
        return new ArrayList<>(this.children);
    }

    public Collection<HierarchyElement> getAncestors() {
        return HierarchyElement.getAccessibleUpwardElements(this.parents);
    }

    protected static Collection<HierarchyElement> getAccessibleUpwardElements(Collection<? extends HierarchyElement> fromSubset) {
        HashMap<String, HierarchyElement> accessible = new HashMap<>();
        Queue<HierarchyElement> queue = new LinkedList<>();

        for(HierarchyElement he : fromSubset) {
            queue.add(he);
            accessible.put(he.getUri(), he);
        }

        while(!queue.isEmpty()) {
            HierarchyElement he = queue.poll();

            for(HierarchyElement parent : he.getParents()) {
                if(!accessible.containsKey(parent.getUri())) {
                    accessible.put(parent.getUri(), parent);
                    queue.add(parent);
                }
            }
        }

        return accessible.values();
    }
}
