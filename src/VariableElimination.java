import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;



public class VariableElimination {

    private String query;
     private String[] hidden;
     private Map<String,String> evidence;
    private bayesianNetwork Bayesian_Network;
    int multiplyCount = 0;
    int addCount = 0;



    public VariableElimination(String query, String[] hidden, Map<String,String> evidence, bayesianNetwork bayesianNetwork) {
        this.query = query;
        this.hidden = hidden;
        this.evidence = evidence;
        this.Bayesian_Network = bayesianNetwork;
    }

    public FactorComponent eliminateVariable(Map<String, FactorComponent> factors, String variable) {
        List<FactorComponent> factorsContainingVariable = new ArrayList<>();
        for (FactorComponent factor : factors.values()) {
            if ( !factor.getProbabilityTable().isEmpty()&& factor.getProbabilityTable().get(0).containsKey(variable)) {
                factorsContainingVariable.add(factor);
            }
        }
        if(factorsContainingVariable.isEmpty()){
            System.out.println("No factors containing variable: " +variable);
            return null;
        }
        FactorComponent multipliedFactor = factorsContainingVariable.remove(0);
        for (FactorComponent factor : factorsContainingVariable) {
            multipliedFactor = FactorComponent.multiply(multipliedFactor, factor);
        }

        FactorComponent marginalizedFactor = multipliedFactor.marginalizeVariable(variable);
        factors.remove(variable);

        return marginalizedFactor;
    }
    public String run() {
        //Initializing factors from the network
        Map<String, FactorComponent> factors = initializeFactors();

        //incorporating evidence indo factors
        incorporateEvidence(factors);

        //Eliminate variables
        for (String variable : hidden) {
            if (!variable.equals(query) && !evidenceContains(variable)) {
                FactorComponent result =eliminateVariable(factors, variable);
                if (result != null) {
                    factors.put(variable,result);
                }
            }
        }
        // I want to check if the finalFactor is null so I can avoid the NullPointerException
        FactorComponent finalFactor = null;
        for (FactorComponent factor : factors.values()) {
            if (finalFactor == null) {
                finalFactor = factor;
            }
            else {
                finalFactor = FactorComponent.multiply(finalFactor, factor);

            }
        }
        if (finalFactor == null || finalFactor.probabilityTable.isEmpty()) {
            return "No vaild results available for the query.";
        }
        finalFactor.normalizeFactor();
        return formatOutput(finalFactor);
    }

    private void incorporateEvidence(Map<String, FactorComponent> factors) {
        for (Map.Entry<String,String> entry : evidence.entrySet()) {
            String var = entry.getKey();
            String value = entry.getValue();
            FactorComponent factor =factors.get(var);
            if(factor !=null) {
                factor.filterRowByEvidence(var,value);
            }
        }
    }

    private boolean evidenceContains(String variable) {
        return evidence.containsKey(variable);
    }


    private Map<String, FactorComponent> initializeFactors() {

        Map<String, FactorComponent> factors = new HashMap<>();
        for (bayesianNode node : Bayesian_Network.getNodes().values()) {

            factors.put(node.getName(), new FactorComponent(node.getCPT(), (ArrayList<String>) node.getParents()));
        }
        return factors;
    }


//    private Map<String, FactorComponent> eliminateVariable(Map<String, FactorComponent> factors, String variable) {
//        //Combing all factors that contain the variable
//
//        FactorComponent combinedFactor = null;
//        List<String> involvedFactors = new ArrayList<>();
//
//        // Combine all factors involving the variable
//        for (Map.Entry<String, FactorComponent> entry : factors.entrySet()) {
//            if (entry.getValue().probabilityTable.stream().anyMatch(row -> row.containsKey(variable))) {
//                if (combinedFactor == null) {
//                    combinedFactor = entry.getValue();
//                } else {
//                    combinedFactor = FactorComponent.multiply(combinedFactor, entry.getValue());
//                }
//                involvedFactors.add(entry.getKey());
//            }
//        }
//
//        if (combinedFactor != null) {
//            // Sum out the variable
//            FactorComponent newFactor = combinedFactor.marginalizeVariable(variable);
//            factors.put("summedOut_" + variable, newFactor);
//
//            // Remove the old factors
//            for (String factorName : involvedFactors) {
//                factors.remove(factorName);
//            }
//        }
//
//        return factors;
//    }

    private String formatOutput(FactorComponent finalFactor) {
        StringBuilder output = new StringBuilder();
        for (HashMap<String, String> row : finalFactor.getProbabilityTable()) {
            output.append(row.toString()).append("\n");
        }
       return output.toString();
    }
}

//private void normalize(FactorComponent factor) {
//    double sum = factor.values.values().stream().mapToDouble(Double::doubleValue).sum();
//    for (Map.Entry<List<String>, Double> entry : factor.values.entrySet()) {
//        factor.put(entry.getKey(), entry.getValue() / sum);
//    }
//}



//    public void normalize(FactorComponent factor){
//        double total =factor.values.values().stream().mapToDouble(v->v).sum();
//            factor.values.replaceAll((k,v)->v/total);;
//            addCount +=factor.values.size()-1; //Normalization involves n-1 additions
//
//    }
//}


