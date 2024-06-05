import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class bayesianNode{
    String name;
    List<String> outcomes;
    List <String> parents;
    bayesianNetwork network;
    List <bayesianNode> children;
    Map<String,Double> cpt;

    bayesianNode(String name, List<String>parentsNames, bayesianNetwork network, List<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
        this.parents = new ArrayList<>();
        this.network = network;
        this.cpt = new HashMap<>(); //Initialize the CPT
        this.children = new ArrayList<>(); //Initialize the children list


        for (String parentName : parentsNames) {
            bayesianNode parent = network.returnByName(parentName);
            if (parents != null) {
                this.parents.add(parentName);
                parent.children.add(this);
            }
        }
    }
        public bayesianNode(String name){
            this.name =name;
        }


    void build (String[] table){
        int outcomeCount =outcomes.size();
        int parentCombinations =table.length /outcomeCount;
        for (int i =0 ;i<parentCombinations; i++){
            for (int j=0;j<outcomeCount; j++){
                String key= generateKey(i,j,outcomeCount);
                cpt.put(key,Double.parseDouble(table[i *outcomeCount +j]));
            }
        }
    }


    private  String generateKey(int parentCombination,int outcomeIndex, int outcomeCount){
        StringBuilder key = new StringBuilder();

        //convert parent comination index to binary  representation
        for ( int i = parents.size()-1; i>=0 ; i--){
            int val = (parentCombination/ (1<<i))%2;
            key.append(val).append(",");
        }
        //Append the outcome index
        key.append(outcomeIndex);
        return  key.toString();
    }



    void  addChild(bayesianNode child){
        children.add(child);
    }
    @Override
    public String toString(){
        return "Node{name" + name + ",outcomes=" + outcomes + ",parents=" +parents + ",CPT=" +cpt + "}" ;
    }


}



