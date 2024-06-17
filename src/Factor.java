// This class I used as a utility class

import java.util.*;

class Factor {
    String name;
    Set<String> variables;
    Map<List<String>,Double> values;   //for every key there is a combo of var values ,each value is of-course the probability

    // Constructor for initializing directly from a BayesianNode
    public Factor(bayesianNode node){
        this.name = node.getName();
        this.variables =new HashSet<>();   //Adding its parents
        for(bayesianNode parent : node.getParents()){
            this.variables.add(parent.getName());
        }
        this.variables.add(node.getName()); // adding itself
        this.values = new HashMap<>();
        initializeValuesFromCPT(node);

    }


    // Constructor for other operations like multiplication or sum-out
    private Factor(String name) {
        this.name = name;
        this.variables = new HashSet<>();
        this.values = new HashMap<>();
    }


    private  void initializeValuesFromCPT(bayesianNode node) {
        // Assuming node.cpt holds entries in form of "parentValue1,parentValue2,...:nodeValue -> probability"
        for(Map.Entry<String,Double> entry : node.cpt.entrySet()){
            String key = entry.getKey();
            List <String> keyParts = new ArrayList<>(Arrays.asList(key.split(",")));
            this.values.put(keyParts,entry.getValue());

        }
    }


    public Factor sumOut(String variable) {
        Factor result = new Factor("Summed(" + this.name + ")");
        result.variables.addAll(this.variables);
        result.variables.remove(variable);


        Map<String, Double> newValues = new HashMap<>();
        for (Map.Entry<List<String>, Double> entry : this.values.entrySet()) {
            List<String> reduceKey = new ArrayList<>(entry.getKey());
            reduceKey.removeIf(k->k.startsWith(variable + "="));   // Filter out the variable to sum out
            String reducesKeyString = String.join(",", reduceKey);
            newValues.merge(reducesKeyString, entry.getValue(), Double::sum);

        }
        //converting our string keys back to list format

        for (Map.Entry<String,Double> entry : newValues.entrySet()){
            result.values.put(Arrays.asList(entry.getKey().split(",")), entry.getValue());
        }
        return result;
    }


    public void applyEvidence(String variable,String value){
        this.values.entrySet().removeIf(entry ->{
            int index = new ArrayList<>(this.variables).indexOf(variable);
            return !entry.getKey().get(index).equals(value);
        });
    }
    public static Factor multiply(Factor f1,Factor f2){
        String newName = "Factor(" +f1.name + "*" + f2.name + ")";
        Factor result =new Factor(newName);
        result.variables.addAll(f1.variables);
        result.variables.addAll(f2.variables);


        // Cartesian product of the entries of f1 and f2
        for(Map.Entry<List<String>,Double> e1 : f1.values.entrySet()){
            for(Map.Entry<List<String>,Double> e2 :f1.values.entrySet()){
                List <String> comboKey = new ArrayList<>(e1.getKey());
                double comboValue = e1.getValue()* e2.getValue();
                result.values.put(comboKey,comboValue);
            }
        }
        return  result;
    }

    public void normalize(){
        double totalSum =this.values.values().stream().mapToDouble(Double::doubleValue).sum();
        this.values.replaceAll((key,value)->value/totalSum);
    }
}

