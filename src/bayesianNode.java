import java.util.*;

public class bayesianNode{
    private String name;
    private List<String> outcomes;
    private List <String> parents;
    private bayesianNetwork network;
    List <String> children;
    private List<HashMap<String,String>> cpt;
    private FactorComponent factor;



    public bayesianNode(String name, ArrayList<String> parents, ArrayList<String> outcomes,bayesianNetwork network) {
        this.name = name;
        this.parents = parents;
        this.children = new ArrayList<>();
        this.network = network;
        this.outcomes = new ArrayList<>();
        for(String o:outcomes){
            this.outcomes.add(name+"="+o);
        }
        this.cpt = new ArrayList<>();
    }
    public bayesianNode(String name) {
        this.name = name;
        this.parents = new ArrayList<>();
        this.cpt = new ArrayList<>();
    }

    public void build(String[] table) {
//        for (String row : table) {
//            HashMap<String, String> cptRow = new HashMap<>();
//            String[] values = row.split(" ");
//            for (int i = 0; i < values.length; i++) {
//                cptRow.put(parents.get(i), values[i]);
//            }
//            cpt.add(cptRow);
//        }
        int section = table.length;
        ArrayList<HashSet<String>> variables = new ArrayList<>();
        for (int i = 0; i < section; i++) {
            variables.add(new HashSet<>());
        }
        for (String given : this.parents) {
            bayesianNode parentN = network.getNode(given);
            int num_of_outcomes = parentN.outcomes.size();
            section = section / num_of_outcomes;
            int indexer = -1;
            for (String key : parentN.outcomes) {
                indexer++;
                int outcome_index = indexer;
                for (int j = outcome_index * section; j < table.length; j += num_of_outcomes * section) {
                    for (int k = 0; k < section; k++) {
                        variables.get(k + j).add(key);
                    }
                }
            }
        }
        section = section / this.outcomes.size();
        int index = 0;
        for (String key : this.outcomes) {
            int num_of_outcomes = this.outcomes.size();
            for (int j = index * section; j < table.length; j += num_of_outcomes * section) {
                for (int k = 0; k < section; k++) {
                    variables.get(k + j).add(key);
                }
            }
            index++;
        }
        for (int i = 0; i < variables.size(); i++) {
            HashSet<String> variable = variables.get(i);
            String val = table[i];
        }

    }







    @Override
    public String toString(){
        return "Node{name" + name + ",outcomes=" + outcomes + ",parents=" +parents + ",CPT=" +cpt + "}" ;
    }

    public List<String> getParents() {
        return parents;
    }

    public void addParent(String parent){
        parents.add(parent);
    }

    void  addChild(String child){
        if (!children.contains(child)) {
            children.add(child);
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getChildren() {
        return children;
    }

    public List<String> getOutcomes() {
        return outcomes;
    }
    public List<HashMap<String,String >>getCPT(){
        return cpt;
    }


    public void setFactor(FactorComponent factor) {
        this.factor = factor;
    }

    public FactorComponent getFactor() {
        return factor;
    }



    public void buildCPT(String[] table) {
        for (String row : table) {
            HashMap<String, String> cptRow = new HashMap<>();
            String[] entries = row.split(" ");
            int index = 0;
            for (String parentName : parents) {
                cptRow.put(parentName, entries[index++]);
            }
            cptRow.put(name, entries[index++]);
            cptRow.put("P", entries[index]);
            cpt.add(cptRow);
        }
    }
}