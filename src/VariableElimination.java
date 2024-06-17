import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;



public class VariableElimination {

    String query;
    List<String> hidden;
    Map<String, String> evidence;
    bayesianNetwork Bayesian_Network;



    public VariableElimination(String query, List<String> hidden, Map<String, String> evidence, bayesianNetwork bayesian_Network) {
        this.query = query;
        this.hidden = hidden;
        this.evidence = evidence;
        this.Bayesian_Network = bayesian_Network;
    }
    public String run() {
        //Initializing factors from the network
        Map<String, Factor> factors = initializeFactors();

        //incorporating evidence indo factors
        incorporateEvidence(factors);

        //Eliminate variables
        for (String variable : hidden) {
            if (!variable.equals(query) && !evidence.containsKey(variable)) {
                factors = eliminateVariable(factors, variable);
            }
        }
        // I want to check if the finalFactor is null so I can avoid the NullPointerException
        Factor finalFactor = null;
        for (Factor factor : factors.values()) {
            if (finalFactor == null) {
                finalFactor = factor;
            } else {
                finalFactor = Factor.multiply(finalFactor, factor);
            }
        }
        if (finalFactor == null || finalFactor.values == null || finalFactor.values.isEmpty()) {
            return "No vaild results available for the query.";
        }
        normalize(finalFactor);
        return formatOutput(finalFactor);
    }

    private void incorporateEvidence(Map<String, Factor> factors) {
        for (Map.Entry<String, String> entry : evidence.entrySet()) {
            String var = entry.getKey();
            Factor factor = factors.get(var);
            if (factor != null) {
                factor.applyEvidence(var,entry.getValue());
            }
        }
    }

    private Map<String, Factor> initializeFactors() {
        Map<String, Factor> factors = new HashMap<>();
        for (bayesianNode node : Bayesian_Network.nodes) {
            Factor factor = new Factor(node);
            factors.put(node.getName(), factor);
        }
        return factors;
    }

    private Map<String, Factor> eliminateVariable(Map<String, Factor> factors, String variable) {
        //Combing all factors that contain the variable
        Factor newFactor = null;
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String,Factor>entry : factors.entrySet()) {
            if (entry.getValue().variables.contains(variable)) {
                if (newFactor == null) {
                    newFactor = entry.getValue();
                } else {
                    newFactor = Factor.multiply(newFactor,entry.getValue());
                }
                toRemove.add(entry.getKey());
            }
        }
        toRemove.forEach(factors::remove);

        //summing out the var
        if(newFactor != null){
            Factor summedFactor =newFactor.sumOut(variable);
            factors.put(variable + "_summed", summedFactor);
        }
        return factors;
    }

    private String formatOutput(Factor finalFactor) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<List<String>, Double> entry : finalFactor.values.entrySet()) {
            sb.append("P(").append(query).append("=").append(entry.getKey().get(0)).append(") = ");
            sb.append(String.format("%.5f", entry.getValue())).append("\n");
        }
        return sb.toString();
    }

    public void normalize(Factor factor){
        double total =factor.values.values().stream().mapToDouble(v->v).sum();
        for(List<String> key : new ArrayList<>(factor.values.keySet())){
            factor.values.put(key,factor.values.get(key)/total);
        }
    }
}


