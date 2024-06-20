// This class I used as a utility class

import java.util.*;




    public class FactorComponent implements  Comparable <FactorComponent>{
        ArrayList<HashMap<String,String>> probabilityTable= new ArrayList<>();
        ArrayList<String> evidenceList = new ArrayList<>();


        public FactorComponent(List<HashMap<String,String>> probabilityTable,ArrayList<String>eveidenceList){
            this.probabilityTable = new ArrayList<>(probabilityTable);
            this.evidenceList = new ArrayList<>(eveidenceList);
        }

        public FactorComponent() {
            this.probabilityTable = new ArrayList<>();
            this.evidenceList = new ArrayList<>();
        }

        public  FactorComponent(FactorComponent other){
            for (int i = 0; i < other.probabilityTable.size(); i++) {
                this.probabilityTable.add(deepCopyHashMap(other.probabilityTable.get(i)));
            }
            for (int i = 0; i < other.evidenceList.size(); i++) {
                this.evidenceList.add(other.evidenceList.get(i));
            }
        }

        //Method the I can perform a deep copy of a given HashMap
        private HashMap<String,String> deepCopyHashMap(HashMap<String,String> line){
            HashMap<String,String> copy = new HashMap<>();
            for(String key : line.keySet()){
                copy.put(key, line.get(key));
            }
            return copy;
        }
        public void filterRowByEvidence(String variable, String value) {
            this.probabilityTable.removeIf(row -> !row.get(variable).equals(value));
        }
        public void filterRowByEvidence(){
            ArrayList<String> evidencePairs = new ArrayList<>();
            for(String evidence : this.evidenceList){
                String []parts = evidence.split("=");
                Collections.addAll(evidencePairs,parts);
            }
            for(int i =0; i< this.probabilityTable.size(); i++){
                int valIndex = 0;
                for(String key : this.probabilityTable.get(i).keySet()){
                   if(!key.equals("P")){
                       for(int j=0; j < evidencePairs.size()-1 ; j++){
                           if (evidencePairs.get(j).equals(key) && !evidencePairs.get(j+1).equals(this.probabilityTable.get(i).values().toArray()[valIndex])){
                               this.probabilityTable.remove(i);
                               if(i > 0){
                                   i--;
                               }
                           }
                       }
                   }
                   valIndex++;
                }
            }
        }
        public FactorComponent marginalizeVariable(String variable){
            FactorComponent result = new FactorComponent();
            result.evidenceList.addAll(this.evidenceList);

            Map<List<String>, Double> aggregatedValues = new HashMap<>();
            for (Map<String, String> row : this.probabilityTable) {
                List<String> key = new ArrayList<>(row.values());
                int varIndex = new ArrayList<>(row.keySet()).indexOf(variable);
                if (varIndex != -1 && varIndex < key.size()) {
                    key.remove(varIndex);
                }
                double probValue = Double.parseDouble(row.getOrDefault("P", "0.0"));
                aggregatedValues.merge(key, probValue, Double::sum);
            }

            for (Map.Entry<List<String>, Double> entry : aggregatedValues.entrySet()) {
                HashMap<String, String> newRow = new HashMap<>();
                List<String> keys = new ArrayList<>(this.probabilityTable.get(0).keySet());
                keys.remove(variable);
                for (int i = 0; i < keys.size(); i++) {
                    newRow.put(keys.get(i), entry.getKey().get(i));
                }
                newRow.put("P", entry.getValue().toString());
                result.probabilityTable.add(newRow);
            }

            return result;
        }
        // A method for calculating the ASCII value sum of the factor's key for comparing later on
        public  int calculateAsciiSum(){
            int asciiSum = 0;
            for(String key : this.probabilityTable.get(0).keySet()){
                for(int i =0; i<key.length(); i++){
                    asciiSum +=key.charAt(i);
                }
            }
            return asciiSum;
        }
        public static FactorComponent multiply(FactorComponent f1,FactorComponent f2) {
            FactorComponent result = new FactorComponent();
            for (Map<String, String> row1 : f1.probabilityTable) {
                for (Map<String, String> row2 : f2.probabilityTable) {
                    boolean compatible = true;
                    HashMap<String, String> combinesRows = new HashMap<>(row1);
                    for (String key : row2.keySet()) {
                        if (!key.equals("P")) {
                            if (combinesRows.containsKey(key)) {
                                if (!combinesRows.get(key).equals(row2.get(key))) {
                                    compatible = false;
                                    break;
                                }
                            } else {
                                combinesRows.put(key, row2.get(key));
                            }
                        }
                    }
                    if (compatible == true) {
                        double combinedProb = Double.parseDouble(combinesRows.get("P")) * Double.parseDouble(row2.get("P"));
                        combinesRows.put("P", String.valueOf(combinedProb));
                        result.probabilityTable.add(combinesRows);
                    }
                }
            }
            return result;
        }

        // Method to normalize the factor
        public void normalizeFactor() {
            double total = 0.0;
            for (HashMap<String, String> row : this.probabilityTable) {
                total += Double.parseDouble(row.get("P"));
            }
            for (HashMap<String, String> row : this.probabilityTable) {
                double normalizedValue = Double.parseDouble(row.get("P")) / total;
                row.put("P", String.valueOf(normalizedValue));
            }
        }


        // Method to remove evidence from the CPTs after it has been used
        public void clearEvidenceFromTable() {
            for (String evidence : this.evidenceList) {
                String variable = evidence.split("=")[0];
                for (int i = 0; i < this.probabilityTable.size(); i++) {
                    this.probabilityTable.get(i).remove(variable);
                }
            }
        }

        // Method to compare two FactorComponents
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

        public ArrayList<HashMap<String, String>> getProbabilityTable() {
            return probabilityTable;

        }

        public void addEvidence(String evidence) {
            evidenceList.add(evidence);
        }

        public void addProbabilityRow(HashMap<String, String> row) {
            probabilityTable.add(row);
        }

        // this function removes the evidence from the CPT's after we used it
        public void removeEvidence() {
            for (String e : evidenceList) {
                String var = e.split("=")[0];
                for (HashMap<String, String> row : probabilityTable) {
                    row.remove(var);
                }
            }
        }
    }



