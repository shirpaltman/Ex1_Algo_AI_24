import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class bayesianNetwork{
    List<bayesianNode> nodes;

     public bayesianNetwork(){
        this.nodes = new ArrayList<>();
    }
    public void add_set(bayesianNode node){
        nodes.add(node);
    }


    //Method to fix ant issues with the network structure
    void fixNet(){
        Map<String,bayesianNode> nodeMap = new HashMap<>();
        for (bayesianNode node : nodes) {
            String nodeName =node.name;
            nodeMap.put(node.name, node);
        }
        for(bayesianNode node: nodes){
            for(bayesianNode parentName : node.parents){
                bayesianNode parentNode = nodeMap.get(parentName.getName());
                if(parentNode != null){
                    parentNode.addChild(node);          //adding the current node as a child to the parent's node
                }
            }
        }
    }

    @Override
    public String toString(){
        return "Network{nodes=" + nodes + "}";
    }

    public bayesianNode returnByName(String name) {
        for(bayesianNode node :nodes){
            if(node.name.equals(name)){
                return node;

            }
        }
        return null;
    }


}