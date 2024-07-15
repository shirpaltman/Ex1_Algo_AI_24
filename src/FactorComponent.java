/**
 * This class represents a factor in the Bayesian Network.
 * It stores the factor table and evidence list, and provides various methods to manipulate and query the factor.
 */

import java.util.*;




    public class FactorComponent implements  Comparable <FactorComponent> {
        Map<Map<String, String>, Double> probabilityTable = new HashMap<>();
        HashMap<String,String> evidenceList = new HashMap<>();


        /**
         * Constructor to initialize the FactorComponent with given probability table and evidence list.
         *
         * @param probabilityTable The table storing probabilities for different variable assignments.
         * @param evidenceList     List of evidence variables and their values.
         */
        public FactorComponent(Map<Map<String, String>, Double> probabilityTable, HashMap<String,String> evidenceList) {
            this.probabilityTable = new HashMap<>();
            for(Map<String,String> key:probabilityTable.keySet()){
                Map<String,String> key_copy = Map.copyOf(key);
                this.probabilityTable.put(key_copy,probabilityTable.get(key));
            }
            this.evidenceList = new HashMap<>(evidenceList);
        }

        public FactorComponent() {
            this.probabilityTable = new HashMap<>();
            this.evidenceList = new HashMap<>();
        }


        /**
         * Default constructor initializing empty probability table and evidence list.
         */
        public FactorComponent(FactorComponent other) {
            this.probabilityTable = new HashMap<>();
            for(Map<String,String> key:other.probabilityTable.keySet()){
                Map<String,String> key_copy = Map.copyOf(key);
                this.probabilityTable.put(key_copy,other.probabilityTable.get(key));
            }
            this.evidenceList = new HashMap<>();
            for(String key:other.evidenceList.keySet()){
                this.evidenceList.put(key,other.evidenceList.get(key));
            }
        }

        //Method the I can perform a deep copy of a given HashMap
        private HashMap<String, String> deepCopyHashMap(HashMap<String, String> line) {
            HashMap<String, String> copy = new HashMap<>();
            for (String key : line.keySet()) {
                copy.put(key, line.get(key));
            }
            return copy;
        }


        /**
         * Filters rows based on the evidence list.
         * Iterates over the evidence list and removes rows that do not match the evidence.
         */
        public void filterRowByEvidence() {
                List<Map<String,String>> toRemove;
                if(this.haveEvidence()){
                    toRemove = this.probabilityTable.keySet().stream().filter(this::needToRemoveRow).toList();
                    if(!toRemove.isEmpty()){
                        for(Map<String,String> r:toRemove){
                            this.probabilityTable.remove(r);
                        }
                    }
                   List<Map<String,String>> oneRemove;
                        oneRemove= this.probabilityTable.keySet().stream().toList();
                        for(Map<String,String> key:oneRemove){
                            double value = this.probabilityTable.get(key);
                            HashMap<String,String> new_key = new HashMap<>(key);
                            this.probabilityTable.remove(key);
                            for(String var:this.evidenceList.keySet()){
                                new_key.remove(var);
                            }
                            this.probabilityTable.put(new_key,value);
                        }
                }

        }

        private boolean needToRemoveOne(Map<String,String> key) {
            boolean needToRemove = false;

            for (String eviKey : new HashSet<>(this.evidenceList.keySet())) {
                if (key.containsKey(eviKey)) {
                    String outcome = key.get(eviKey);
                    String eviOutcome = this.evidenceList.get(eviKey);


                }
            }
            return needToRemove;
        }

        private boolean haveEvidence(){
            for(String eviKey:this.evidenceList.keySet()){
                if(this.probabilityTable.keySet().stream().anyMatch(k-> k.containsKey(eviKey)))
                    return true;
            }
            return false;
        }
        private boolean needToRemoveRow(Map<String,String> key){
            for(String eviKey:this.evidenceList.keySet())
            {
                if(key.containsKey(eviKey)){
                    String outcome = key.get(eviKey);
                    String eviOutcome = this.evidenceList.get(eviKey);
                    if(!outcome.equals(eviOutcome))
                        return  true;
                }
            }
            return false;
        }


        /**
         * Calculates the ASCII value sum of the factor's keys.
         * This is used for comparing factors.
         *
         * @return The sum of ASCII values of the keys.
         */
        public int calculateAsciiSum() {
            int asciiSum = 0;
//            for(String key : this.probabilityTable.get(0).keySet()){
//                for(int i =0; i<key.length(); i++){
//                    asciiSum +=key.charAt(i);
//                }
//            }
            return asciiSum;
        }


        /**
         * Multiplies two factors.
         *
         * @param f1 The first factor.
         * @param f2 The second factor.
         * @return A new FactorComponent that is the product of the two factors.
         */
        public static FactorComponent multiply(FactorComponent f1, FactorComponent f2) {
            FactorComponent result = new FactorComponent();
//            for (Map<String, String> row1 : f1.probabilityTable) {
//                for (Map<String, String> row2 : f2.probabilityTable) {
//                    boolean compatible = true;
//                    HashMap<String, String> combinesRows = new HashMap<>(row1);
//                    for (String key : row2.keySet()) {
//                        if (!key.equals("P")) {
//                            if (combinesRows.containsKey(key)) {
//                                if (!combinesRows.get(key).equals(row2.get(key))) {
//                                    compatible = false;
//                                    break;
//                                }
//                            } else {
//                                combinesRows.put(key, row2.get(key));
//                            }
//                        }
//                    }
//                    if (compatible == true) {
//                        double combinedProb = Double.parseDouble(combinesRows.get("P")) * Double.parseDouble(row2.get("P"));
//                        combinesRows.put("P", String.valueOf(combinedProb));
//                        result.probabilityTable.add(combinesRows);
//                    }
//                }
//            }
            return result;
        }




        /**
         * Compares two FactorComponents first by size, then by ASCII sum of their keys.
         *
         * @param other The other FactorComponent to compare to.
         * @return -1, 0, or 1 as this FactorComponent is less than, equal to, or greater than the other FactorComponent.
         */
        @Override
        public int compareTo(FactorComponent other) {
            int comparison = Integer.compare(this.probabilityTable.size(), other.probabilityTable.size());
            if (comparison == 0) {
                comparison = Integer.compare(this.calculateAsciiSum(), other.calculateAsciiSum());
            }
            return comparison;
        }


        @Override
        public String toString() {
            return "FactorComponent{" +
                    "probabilityTable=" + probabilityTable +
                    ", evidenceList=" + evidenceList + '}';
        }

        public Map<Map<String, String>,Double> getProbabilityTable() {
            return this.probabilityTable;
        }

    }



