import java.util.*;

public class bayesianNode{
    private String name;
    private List<String> outcomes;
    private List <String> parents;
    private bayesianNetwork network;
    List <String> children;
    private Map<Map<String, String>, Double> cptTable;
    private FactorComponent factor;



    public bayesianNode(String name, ArrayList<String> parents, ArrayList<String> outcomes,bayesianNetwork network) {
        this.name = name;
        this.parents = new ArrayList<>(parents);
        this.children = new ArrayList<>();
        this.network = network;
        this.outcomes = new ArrayList<>(outcomes);
        this.cptTable = new HashMap<>();
        //for(String o:outcomes){
        // this.outcomes.add(name+"="+o);
        //}
    }
    public bayesianNode(String name) {
        this.name = name;
        this.parents = new ArrayList<>();
        this.cptTable = new HashMap<>();
    }



    public bayesianNode(String name, List<String> outcomes) {
        this.name = name;
        this.parents = new ArrayList<>();
        this.outcomes = new ArrayList<>(outcomes);
        this.children = new ArrayList<>();
        this.cptTable = new HashMap<>();
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
        return "Node{name" + name + ",outcomes=" + outcomes + ",parents=" +parents + ",CPT=" +cptTable + "}" ;
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



    public void setFactor(FactorComponent factor) {
        this.factor = factor;
    }

    public FactorComponent getFactor() {
        return factor;
    }



//    public void buildCPT(String[] table) {
//        for (String row : table) {
//            HashMap<String, String> cptRow = new HashMap<>();
//            String[] entries = row.split(" ");
//            int index = 0;
//            for (String parentName : parents) {
//                cptRow.put(parentName, entries[index++]);
//            }
//            cptRow.put(name, entries[index++]);
//            cptRow.put("P", entries[index]);
//            cptTable.add(cptRow);
//        }
//    }


    public Map<Map<String, String>, Double> getCptTable() {
        return cptTable;
    }

    public void createCptTable(List<bayesianNode> variables, List<Double> probs) {
        List<String> possibleOutcomes = new ArrayList<>();
        createCptTableHelper(variables, probs, possibleOutcomes, 0);
    }

    private int createCptTableHelper(List<bayesianNode> variables, List<Double> probs, List<String> possibleOutcomes, int index) {
        if (possibleOutcomes.size() == variables.size()) {
            Map<String, String> key = new HashMap<>();
            for (int i = 0; i < variables.size(); i++) {
                key.put(variables.get(i).getName(), possibleOutcomes.get(i));
            }
            this.cptTable.put(key, probs.get(index));
            return index + 1;
        }

        bayesianNode var = variables.get(possibleOutcomes.size());
        for (String outcome : var.getOutcomes()) {
            possibleOutcomes.add(outcome);
            index = createCptTableHelper(variables, probs, possibleOutcomes, index);
            possibleOutcomes.remove(possibleOutcomes.size() - 1);
        }
        return index;
    }

    public void printCptTable() {
        List<Map<String, String>> sortedKeys = sortedKeys(cptTable.keySet());
        for (Map<String, String> condition : sortedKeys) {
            StringBuilder conditionStr = new StringBuilder();
            for (Map.Entry<String, String> entry : condition.entrySet()) {
                conditionStr.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
            }
            conditionStr.delete(conditionStr.length() - 2, conditionStr.length()); // Remove trailing comma and space
            System.out.println("Condition: {" + conditionStr + "} -> Probability: " + cptTable.get(condition));
        }
    }


    private List<Map<String, String>> sortedKeys(Set<Map<String, String>> keySet) {
        List<Map<String, String>> sortedList = new ArrayList<>(keySet);
        sortedList.sort((map1, map2) -> {
            StringBuilder key1 = new StringBuilder();
            StringBuilder key2 = new StringBuilder();
            for (String key : map1.keySet()) {
                key1.append(key).append("=").append(map1.get(key)).append(", ");
            }
            for (String key : map2.keySet()) {
                key2.append(key).append("=").append(map2.get(key)).append(", ");
            }
            return key1.toString().compareTo(key2.toString());
        });
        return sortedList;
    }

}