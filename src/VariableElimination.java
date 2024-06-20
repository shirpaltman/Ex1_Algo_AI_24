import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;



public class VariableElimination {

    private String query;
     private String[] hidden;
     private String[] evidence;
    private bayesianNetwork Bayesian_Network;
    int multiplyCount = 0;
    int addCount = 0;



    public VariableElimination(String query, String[] hidden, String[] evidence, bayesianNetwork bayesianNetwork) {
        this.query = query;
        this.hidden = hidden;
        this.evidence = evidence;
        this.Bayesian_Network = bayesianNetwork;
    }
    public String run() {
        //Initializing factors from the network
        Map<String, FactorComponent> factors = initializeFactors();

        //incorporating evidence indo factors
        incorporateEvidence(factors);

        //Eliminate variables
        for (String variable : hidden) {
            if (!variable.equals(query) && !evidenceContains(variable)) {
                factors = eliminateVariable(factors, variable);
            }
        }
        // I want to check if the finalFactor is null so I can avoid the NullPointerException
        FactorComponent finalFactor = null;
        for (FactorComponent factor : factors.values()) {
            if (finalFactor == null) {
                finalFactor = factor;
            } else {
                finalFactor = FactorComponent.multiply(finalFactor, factor);

            }
        }
        if (finalFactor == null || finalFactor.probabilityTable.isEmpty() || finalFactor.probabilityTable == null ) {
            return "No vaild results available for the query.";
        }
        finalFactor.normalizeFactor();
        return formatOutput(finalFactor);
    }

    private void incorporateEvidence(Map<String, FactorComponent> factors) {
        for (String e : evidence) {
            String[] parts = e.split("=");
            String var = parts[0];
            String value = parts[1];
            FactorComponent factor =factors.get(var);
            if(factor !=null) {
                factor.filterRowByEvidence();
            }
        }
    }

    private boolean evidenceContains(String variable) {
        for (String e : evidence) {
            if (e.startsWith(variable + "=")) {
                return true;
            }
        }
        return false;
    }


    private Map<String, FactorComponent> initializeFactors() {

        Map<String, FactorComponent> factors = new HashMap<>();
        for (bayesianNode node : Bayesian_Network.getNodes().values()) {
            ArrayList<HashMap<String, String>> cpt = node.getCPT();
            String[] evidences = new String[node.getParents().size()];
            int i = 0;
            for (String parent : node.getParents()) {
                evidences[i++] = parent;
            }
            FactorComponent factor = new FactorComponent(cpt, evidences);
            factors.put(node.getName(), factor);
        }
        return factors;
    }


    private Map<String, FactorComponent> eliminateVariable(Map<String, FactorComponent> factors, String variable) {
        //Combing all factors that contain the variable

        FactorComponent combinedFactor = null;
        List<String> involvedFactors = new ArrayList<>();

        // Combine all factors involving the variable
        for (Map.Entry<String, FactorComponent> entry : factors.entrySet()) {
            if (entry.getValue().probabilityTable.stream().anyMatch(row -> row.containsKey(variable))) {
                if (combinedFactor == null) {
                    combinedFactor = entry.getValue();
                } else {
                    combinedFactor = FactorComponent.multiply(combinedFactor, entry.getValue());
                }
                involvedFactors.add(entry.getKey());
            }
        }

        if (combinedFactor != null) {
            // Sum out the variable
            FactorComponent newFactor = combinedFactor.marginalizeVariable(variable);
            factors.put("summedOut_" + variable, newFactor);

            // Remove the old factors
            for (String factorName : involvedFactors) {
                factors.remove(factorName);
            }
        }

        return factors;
    }

    private String formatOutput(FactorComponent finalFactor) {
        StringBuilder output = new StringBuilder();
        for (Map<String,String> row :  finalFactor.probabilityTable) {
            output.append("P(");
            for(Map.Entry<String,String> entry : row.entrySet()){
                if (!entry.getKey().equals("P")){
                    output.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
                }
            }
            output.deleteCharAt(output.length()-1);
            output.append(") = ").append(row.get("P")).append("\n");
        }
       return output.toString();
    }
}

//private void normalize(FactorComponent factor) {
//    double sum = factor.values.values().stream().mapToDouble(Double::doubleValue).sum();
//    for (Map.Entry<List<String>, Double> entry : factor.values.entrySet()) {
//        factor.values.put(entry.getKey(), entry.getValue() / sum);
//    }
//}



//    public void normalize(FactorComponent factor){
//        double total =factor.values.values().stream().mapToDouble(v->v).sum();
//            factor.values.replaceAll((k,v)->v/total);;
//            addCount +=factor.values.size()-1; //Normalization involves n-1 additions
//
//    }
//}


