import java.util.*;

public class bayesianNode{
    private String name;
    private List<String> outcomes;
    private List <String> parents;
    //bayesianNetwork network;
    List <String> children;
    private ArrayList<HashMap<String,String>> cpt;
    String evidenceValue;


    public bayesianNode(String name,List<String> outcomes){
        this.name = name;
        this.outcomes = new ArrayList<>(outcomes);      // Clone the list to avoid external modifications
        this.parents = new ArrayList<>();       // Initialize parents list
        this.cpt = new ArrayList<>();     // Initialize the CPT map
        this.children = new ArrayList<>();
    }



   // Function to build the CPT
//    public void buildCPT(String[] parentValues, String[] nodeValues, double[] probabilities) {
//        for (int i = 0; i < probabilities.length; i++) {
//            HashMap<String, String> row = new HashMap<>();
//            int index = i;
//            for (int j = parentValues.length - 1; j >= 0; j--) {
//                row.put(parents.get(j).getName(), parentValues[index % parentValues.length]);
//                index /= parentValues.length;
//            }
//            row.put(name, nodeValues[i % nodeValues.length]);
//            row.put("P", String.valueOf(probabilities[i]));
//            cpt.add(row);
//        }
//    }
//
//// Function to build the CPT from a 2D array
//public void buildCPT(String[][] table) {
//    for (String[] row : table) {
//        HashMap<String, String> cptRow = new HashMap<>();
//        for (int i = 0; i < row.length - 1; i++) {
//            cptRow.put(parents.get(i).getName(), row[i]);
//        }
//        cptRow.put(name, row[row.length - 2]);
//        cptRow.put("P", row[row.length - 1]);
//        cpt.add(cptRow);
//    }
//}





    @Override
    public String toString(){
        return "Node{name" + name + ",outcomes=" + outcomes + ",parents=" +parents + ",CPT=" +cpt + "}" ;
    }

    public List<String> getParents() {
        return parents;
    }
    public void addParent(String parentName){
        this.parents.add(parentName);
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
    public ArrayList<HashMap<String,String >>getCPT(){
        return cpt;
    }


    public String getEvidenceValue(){
        return evidenceValue;
    }
    public void  setEvidenceValue (String evidenceValue){
        this.evidenceValue = evidenceValue;
    }


}