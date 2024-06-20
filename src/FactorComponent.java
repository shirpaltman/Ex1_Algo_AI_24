import java.util.*;

public class FactorComponent implements Comparable<FactorComponent> {
    private Map<Map<String, String>, Double> factorTable = new HashMap<>();
    private List<String> variables = new ArrayList<>();

    // Default constructor
    public FactorComponent() {}

    // Constructor with variables and factor table
    public FactorComponent(List<String> variables, Map<Map<String, String>, Double> factorTable) {
        this.variables = new ArrayList<>(variables);
        this.factorTable = new HashMap<>(factorTable);
    }

    // Deep copy constructor
    public FactorComponent(FactorComponent other) {
        this.variables = new ArrayList<>(other.variables);
        this.factorTable = new HashMap<>(other.factorTable);
    }

    // Method to add a row to the factor table
    public void addRow(Map<String, String> key, Double value) {
        this.factorTable.put(new HashMap<>(key), value);
    }

    // Method to remove irrelevant rows based on given evidence
    public void removeIrrelevantRows(Map<String, String> evidence) {
        factorTable.entrySet().removeIf(entry -> {
            for (Map.Entry<String, String> ev : evidence.entrySet()) {
                if (!entry.getKey().get(ev.getKey()).equals(ev.getValue())) {
                    return true;
                }
            }
            return false;
        });
    }

    // Method to normalize the factor table
    public void normalize() {
        double sum = factorTable.values().stream().mapToDouble(Double::doubleValue).sum();
        factorTable.replaceAll((key, value) -> value / sum);
    }

    // Method to multiply two factors
    public static FactorComponent multiply(FactorComponent f1, FactorComponent f2) {
        List<String> newVariables = new ArrayList<>(f1.variables);
        for (String var : f2.variables) {
            if (!newVariables.contains(var)) {
                newVariables.add(var);
            }
        }

        FactorComponent result = new FactorComponent();
        result.variables = newVariables;

        for (Map.Entry<Map<String, String>, Double> entry1 : f1.factorTable.entrySet()) {
            for (Map.Entry<Map<String, String>, Double> entry2 : f2.factorTable.entrySet()) {
                Map<String, String> newKey = new HashMap<>(entry1.getKey());
                newKey.putAll(entry2.getKey());

                if (newKey.size() == newVariables.size()) {
                    result.addRow(newKey, entry1.getValue() * entry2.getValue());
                }
            }
        }

        return result;
    }

    // Method to sum out a variable
    public FactorComponent sumOut(String variable) {
        FactorComponent result = new FactorComponent();
        result.variables = new ArrayList<>(this.variables);
        result.variables.remove(variable);

        Map<Map<String, String>, Double> newFactorTable = new HashMap<>();
        for (Map.Entry<Map<String, String>, Double> entry : factorTable.entrySet()) {
            Map<String, String> newKey = new HashMap<>(entry.getKey());
            newKey.remove(variable);

            newFactorTable.merge(newKey, entry.getValue(), Double::sum);
        }

        result.factorTable = newFactorTable;
        return result;
    }

    // Method to compare two factors by their size
    @Override
    public int compareTo(FactorComponent other) {
        return Integer.compare(this.variables.size(), other.variables.size());
    }

    // Method to print the factor table
    public void printFactorTable() {
        for (Map.Entry<Map<String, String>, Double> entry : factorTable.entrySet()) {
            System.out.println("Condition: " + entry.getKey() + " -> Probability: " + entry.getValue());
        }
    }

    @Override
    public String toString() {
        return "FactorComponent{" +
                "factorTable=" + factorTable +
                ", variables=" + variables +
                '}';
    }
}