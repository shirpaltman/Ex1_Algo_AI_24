import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * This class represents the Variable Elimination algorithm.
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

    // Generate the relevant factors for the given question
    private void generateFactors() {
        ArrayList<bayesianNode> irrelevant = findIrrelevantNodes();
        for (bayesianNode node : BN.getNodes().values()) {
            for (HashMap<String, String> row : node.getCptTable()) {
                for (String key : row.keySet()) {
                    if (irrelevant.contains(BN.returnByName(key))) {
                        irrelevant.add(node);
                    }
                }
            }
        }
        for (bayesianNode node : BN.getNodes().values()) {
            if (!irrelevant.contains(node)) {
                FactorComponent f = new FactorComponent(node.getCPT(), new ArrayList<>(evi));
                f.filterRowByEvidence();
                f.removeEvidence();
                node.setFactor(f);
                this.factors.add(f);
            }
        }
    }

    // Find the irrelevant nodes
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

    // Check if a hidden node is an ancestor of a given node
    private boolean isAncestor(bayesianNode hidden, bayesianNode node) {
        for (String parentName : node.getParents()) {
            bayesianNode parent = BN.getNode(parentName);
            if (hidden.equals(parent) || isAncestor(hidden, parent)) {
                return true;
            }
        }
        return false;
    }


    // Remove factors with only one row
    public void removeOneSize(ArrayList<FactorComponent> f) {
        f.removeIf(factor -> factor.getProbabilityTable().size() == 1);
    }

    // Sort factors by their size, and if equal sort by their ascii value
    public void sort(ArrayList<FactorComponent> factors) {
        factors.sort(Comparator.naturalOrder());
    }

    // Check if the answer is in one of the cells in the query factor
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

    // Join two factors into one
    public FactorComponent join(FactorComponent a, FactorComponent b) {
        ArrayList<String> commonVars = getCommonVars(a, b);
        FactorComponent result = new FactorComponent();
        for (HashMap<String, String> aRow : a.getProbabilityTable()) {
            Hashtable<String, String> aRowCommonVars = new Hashtable<>();
            for (String name : commonVars) {
                aRowCommonVars.put(name, aRow.get(name));
            }
            for (HashMap<String, String> bRow : b.getProbabilityTable()) {
                boolean flag = true;
                for (String key : commonVars) {
                    if (!bRow.get(key).equals(aRowCommonVars.get(key))) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    result.addProbabilityRow(getNewRow(aRow, bRow, commonVars));
                }
            }
        }
        return result;
    }

    // Multiply rows for the join function
    private HashMap<String, String> getNewRow(HashMap<String, String> aRow, HashMap<String, String> bRow, ArrayList<String> commonVars) {
        HashMap<String, String> row = new HashMap<>(aRow);
        for (String key : bRow.keySet()) {
            if (!commonVars.contains(key)) {
                row.put(key, bRow.get(key));
            }
        }
        double p = Double.parseDouble(aRow.get("P")) * Double.parseDouble(bRow.get("P"));
        this.multiply++;
        row.put("P", String.valueOf(p));
        return row;
    }

    // Get common variables between two factors
    private ArrayList<String> getCommonVars(FactorComponent a, FactorComponent b) {
        ArrayList<String> common = new ArrayList<>();
        for (String key : a.getProbabilityTable().get(0).keySet()) {
            if (b.getProbabilityTable().get(0).containsKey(key) && !key.equals("P")) {
                common.add(key);
            }
        }
        return common;
    }

    // Eliminate the hidden node from a given factor
    public FactorComponent eliminate(FactorComponent a, bayesianNode hidden) {
        a.marginalizeVariable(hidden.getName());
        return a;
    }

    // Normalize the values in a given factor
    public FactorComponent normalize(FactorComponent a) {
        a.normalizeFactor();
        return a;
    }

    // Variable elimination algorithm
    public String variableElimination() {
        String[] q = this.str_query.split("=");
        if (answerInFactor(this.query)) {
            for (HashMap<String, String> row : this.query.getFactor().getProbabilityTable()) {
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
                    if (factor.getProbabilityTable().get(0).containsKey(h.getName())) {
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
            for (HashMap<String, String> row : res.getProbabilityTable()) {
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


