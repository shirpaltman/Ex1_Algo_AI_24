import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * This class implements the Variable Elimination algorithm for Bayesian Networks.
 * It handles querying the network with conditions and eliminating irrelevant variables.
 */
public class VariableElimination {
    bayesianNode query;
    String str_query;
    ArrayList<bayesianNode> hidden = new ArrayList<>();
    ArrayList<bayesianNode> evidence = new ArrayList<>();
    ArrayList<String> evi = new ArrayList<>();
    bayesianNetwork BN;
    ArrayList<FactorComponent> factors = new ArrayList<>();
    int multiply = 0;
    int add = 0;

    // Constructor to build the relevant factors to a given query
    public VariableElimination(String query, String[] hidden, String[] evidence, bayesianNetwork BN) {
        String[] _query = query.split("=");
        this.query = BN.returnByName(_query[0]);
        this.str_query = query;
        for (String _hidden : hidden) {
            this.hidden.add(BN.returnByName(_hidden));
        }
        for (String evi : evidence) {
            this.evi.add(evi);
            String[] parts = evi.split("=");
            this.evidence.add(BN.returnByName(parts[0]));
        }
        this.BN = BN;
        // Initialize factors
        generateFactors();
        removeOneSize(this.factors);
        sort(this.factors);
    }

    /**
     * Generates the initial factors for each node in the Bayesian network based on the CPTs.
     */

    private void generateFactors() {
        ArrayList<bayesianNode> irrelevant = findIrrelevantNodes();
        for (bayesianNode node : BN.getNodes().values()) {
            for (Map<String, String> row : node.getCptTable().keySet()) {
                for (String key : row.keySet()) {
                    if (irrelevant.contains(BN.returnByName(key))) {
                        irrelevant.add(node);
                    }
                }
            }
        }
        for (bayesianNode node : BN.getNodes().values()) {
            if (!irrelevant.contains(node)) {
                FactorComponent f = new FactorComponent(node.getCptTable(), evi);
                f.filterRowByEvidence();
                f.removeEvidence();
                node.setFactor(f);
                this.factors.add(f);
            }
        }
    }

    /**
     * Identifies irrelevant nodes using the Bayes Ball algorithm and checks for ancestry.
     *
     * @return A list of nodes determined to be irrelevant for the query.
     */
    private ArrayList<bayesianNode> findIrrelevantNodes() {
        ArrayList<bayesianNode> irrelevant = new ArrayList<>();
        for (bayesianNode h : this.hidden) {
            if (BayesBall.isIndependent(BN, this.query, h, this.evidence).equals("yes")) {
                irrelevant.add(h);
            } else {
                boolean isAncestor = isAncestor(h, this.query);
                for (bayesianNode e : this.evidence) {
                    if (isAncestor(h, e)) {
                        isAncestor = true;
                        break;
                    }
                }
                if (!isAncestor) {
                    irrelevant.add(h);
                }
            }
        }
        return irrelevant;
    }

    /**
     * Determines if one node is an ancestor of another within the network.
     *
     * @param hidden The node to check if it is an ancestor.
     * @param node   The node to check against.
     * @return true if hidden is an ancestor of node, false otherwise.
     */
    private boolean isAncestor(bayesianNode hidden, bayesianNode node) {
        for (String parentName : node.getParents()) {
            bayesianNode parent = BN.getNode(parentName);
            if (hidden.equals(parent) || isAncestor(hidden, parent)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Removes factors that only contain one row, as they no longer contribute to the elimination process.
     *
     * @param f List of factors to check and modify.
     */
    public void removeOneSize(ArrayList<FactorComponent> f) {
        f.removeIf(factor -> factor.getProbabilityTable().size() == 1);
    }

    /**
     * Sorts factors by size and, if sizes are equal, by the ASCII value sum of their keys.
     *
     * @param factors List of factors to sort.
     */
    public void sort(ArrayList<FactorComponent> factors) {
        factors.sort(Comparator.naturalOrder());
    }

    /**
     * Checks if the query answer can be directly obtained from the factor without further elimination.
     *
     * @param query The query node to check.
     * @return true if the answer can be directly obtained, false otherwise.
     */
    public boolean answerInFactor(bayesianNode query) {
        for (bayesianNode h : this.hidden) {
            if (query.getParents().contains(h)) {
                return false;
            }
        }
        for (bayesianNode e : this.evidence) {
            if (!query.getParents().contains(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Joins two factors into one by combining their rows based on common variables.
     *
     * @param a The first factor.
     * @param b The second factor.
     * @return The resulting factor after joining.
     */
    public FactorComponent join(FactorComponent a, FactorComponent b) {
        Map<String,String> commonVars = getCommonVars(a, b);
        FactorComponent result = new FactorComponent();
        for (Map<String, String> aRow : a.getProbabilityTable().keySet()) {
            HashMap<String, String> aRowCommonVars = new HashMap<>();
            for (String name : commonVars.keySet()) {
                aRowCommonVars.put(name, aRow.get(name));
            }
            ArrayList<Map<String, String>> bToAdd = new ArrayList<>();
            for (String aRowComKey : aRowCommonVars.keySet()) {
                List<Map<String, String>> bCom = b.probabilityTable.keySet().stream().filter(k -> k.containsKey(aRowComKey) && k.get(aRowComKey) == aRowCommonVars.get(aRowComKey)).toList();
                bToAdd.addAll(bCom);
            }
            for (Map<String, String> bToAddKey : bToAdd) {
                double aVal = a.getProbabilityTable().get(aRow);
                double bVal = b.getProbabilityTable().get(bToAddKey);
                HashMap<String, String> newRow = new HashMap<>();
                newRow.putAll(bToAddKey);
                newRow.putAll(aRow);
                result.probabilityTable.put(newRow,aVal*bVal);
                this.multiply++;
            }
        }
        return result;
    }

    /**
     * Finds the common variables between two factors.
     *
     * @param a The first factor.
     * @param b The second factor.
     * @return A list of common variables.
     */
    private Map<String,String> getCommonVars(FactorComponent a, FactorComponent b) {
        Map<String,String> common = new HashMap<>();
        List<Map<String, String>> aKeys = a.getProbabilityTable().keySet().stream().toList();
        List<Map<String, String>> bKeys = b.getProbabilityTable().keySet().stream().toList();
        for (String key : aKeys.getFirst().keySet()) {
            if (bKeys.getFirst().containsKey(key))
                common.put(key,bKeys.getFirst().get(key));
        }
        return common;
    }

    /**
     * Eliminates a hidden node from a factor, summing over its possible values.
     *
     * @param a      The factor to eliminate the hidden node from.
     * @param hidden The hidden node to eliminate.
     * @return The factor after elimination.
     */

    public FactorComponent eliminate(FactorComponent a, bayesianNode hidden) {
        a.marginalizeVariable(hidden.getName());
        return a;
    }

    /**
     * Normalizes the values in a factor so they sum to 1.
     *
     * @param a The factor to normalize.
     * @return The normalized factor.
     */
    public FactorComponent normalize(FactorComponent a) {
        a.normalizeFactor();
        return a;
    }

    /**
     * The main method for executing the variable elimination algorithm.
     *
     * @return A string representation of the result of the variable elimination.
     */
    public String variableElimination() {
        String[] q = this.str_query.split("=");
        if (answerInFactor(this.query)) {
            for (Map<String, String> row : this.query.getFactor().getProbabilityTable().keySet()) {
                if (row.get(this.query.getName()).equals(q[1])) {
                    return row.get("P");
                }
            }
        }
        ArrayList<FactorComponent> hidFactors;
        try {
            for (bayesianNode h : this.hidden) {
                hidFactors = new ArrayList<>();
                for (FactorComponent factor : this.factors) {
                    if (factor.getProbabilityTable().keySet().stream().anyMatch(key -> key.containsKey(h.getName()))) {
                        hidFactors.add(factor);
                    }
                }
                sort(hidFactors);
                while (hidFactors.size() > 1) {
                    FactorComponent joined = join(hidFactors.get(0), hidFactors.get(1));
                    this.factors.remove(hidFactors.get(0));
                    this.factors.remove(hidFactors.get(1));
                    hidFactors.remove(0);
                    hidFactors.remove(0);
                    this.factors.add(joined);
                    hidFactors.add(joined);
                    removeOneSize(this.factors);
                    removeOneSize(hidFactors);
                    sort(this.factors);
                    sort(hidFactors);
                }
                if (!hidFactors.isEmpty()) {
                    FactorComponent elim = eliminate(hidFactors.get(0), h);
                    this.factors.remove(hidFactors.get(0));
                    hidFactors.remove(0);
                    this.factors.add(elim);
                    hidFactors.add(elim);
                    removeOneSize(this.factors);
                    removeOneSize(hidFactors);
                    sort(this.factors);
                    sort(hidFactors);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FactorComponent res = new FactorComponent();
        while (this.factors.size() > 1) {
            res = join(this.factors.get(0), this.factors.get(1));
            this.factors.remove(0);
            this.factors.remove(0);
            this.factors.add(res);
            removeOneSize(this.factors);
            sort(this.factors);
        }
        String answer = "";
        try {
            res = normalize(this.factors.get(0));
            for (Map<String, String> row : res.getProbabilityTable().keySet()) {
                if (row.get(this.query.getName()).equals(q[1])) {
                    answer = row.get("P");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        answer += "," + this.add + "," + this.multiply;
        return answer;
    }
}