import java.util.*;

public class bayesianNetwork {
    private Map<String, bayesianNode> nodes;
    List<bayesianNode> _bayesianNetwork;

    public bayesianNetwork() {
        nodes = new HashMap<>();
    }

    public void addNode(bayesianNode node) {
        nodes.put(node.getName(), node);
    }


    public bayesianNode getNode(String name) {
        return nodes.get(name);
    }

    public Map<String, bayesianNode> getNodes() {
        return nodes;
    }

    public bayesianNode returnByName(String name) {
        return nodes.get(name);
    }


    //Method to fix ant issues with the network structure
    public void fixNet() {
        // Ensure all parents have their children correctly set
        for (bayesianNode node : nodes.values()) {
            for (String parentName : node.getParents()) {
                bayesianNode parent = getNode(parentName);
                if (parent != null) {
                    parent.addChild(node.getName());
                }
            }
        }
    }


    @Override
    public String toString() {
        return "Network{nodes=" + nodes + "}";
    }
}

