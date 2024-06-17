import java.io.*;
import java.util.*;

public class readTextfile {
    private File path;
    private BufferedReader br;
    bayesianNetwork BN = new bayesianNetwork();
    ArrayList<String> lines = new ArrayList<>();

    public readTextfile(String path){
        this.path = new File(path);
        String line ="";
        try{
            br = new BufferedReader(new FileReader(this.path));
            while((line = br.readLine())!= null ) {
                lines.add(line);
            }
        }
        catch(FileNotFoundException e){
            System.out.println("Could not read this file.\n");
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    // TO DO have to review it !!!!!
    public String readfile(bayesianNetwork bn){
        String ans= "";
        try {
            for (int i = 1; i < lines.size(); i++) {
                String line =lines.get(i).trim();
                System.out.println("Processing line: " +line);
                if (line.startsWith("P")) {
                    String[] parts = line.split(" ");
                    if (parts.length < 2) {
                        System.out.println("Invalid probability query format: " + line);
                        continue;
                    }
                    String probPart = parts[0].substring(2, parts[0].length() - 1);  // Remove "P(" and ")"
                    String[] queryAndEvidences = probPart.split("\\|");
                    String query = queryAndEvidences[0];
                    String[] evidences = queryAndEvidences.length > 1 ? queryAndEvidences[1].split(",") : new String[0];

                    Map<String, String> evidenceMap = new HashMap<>();
                    for (String ev : evidences) {
                        String[] evParts = ev.split("=");
                        if (evParts.length == 2) {
                            evidenceMap.put(evParts[0], evParts[1]);
                        }
                    }

                    List<String> hiddenVariables = new ArrayList<>(Arrays.asList(parts[1].split("-")));

                    // Create a VariableElimination instance and run the algorithm
                    VariableElimination ve = new VariableElimination(query, hiddenVariables, evidenceMap, bn);
                    String result = ve.run();
                    ans += result + "\n";
                } else {
                    // Handle Bayes Ball queries similarly
                    String[] parts = line.split("\\|");
                    if (parts.length < 2) {
                        System.out.println("Invalid Bayes Ball query format: " + line);
                        continue;
                    }
                    String[] nodes = parts[0].split("-");
                    if (nodes.length < 2) {
                        System.out.println("Invalid nodes format in Bayes Ball query: " + line);
                        continue;
                    }
                    ArrayList<bayesianNode> evidence = new ArrayList<>();
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        String[] ev = parts[1].split(",");
                        for (String e : ev) {
                            String[] one = e.split("=");
                            if (one.length == 2) {
                                bayesianNode eNode = bn.returnByName(one[0]);
                                evidence.add(eNode);
                            }
                        }
                    }

                    bayesianNode src = bn.returnByName(nodes[0]);
                    bayesianNode dest = bn.returnByName(nodes[1]);
                    ans += BayesBall.isIndependent(bn, src, dest, evidence) + "\n";
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to process queries");
            e.printStackTrace();
        }
        return ans;
    }
}