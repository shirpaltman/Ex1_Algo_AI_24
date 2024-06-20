import java.io.*;
import java.util.*;

public class readTextfile {
    private File path;
    private BufferedReader br;
    public bayesianNetwork network;
    //bayesianNetwork network = new bayesianNetwork();
    ArrayList<String> lines = new ArrayList<>();




    public readTextfile(String path) {
        this.path = new File(path);
        String line = "";
        try {
            br = new BufferedReader(new FileReader(this.path));
           
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not read this file.\n");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String  readfile(String filePath,bayesianNetwork n) {
        String ans = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = br.readLine();
            bayesianNetwork network = xmlFile.read_net(line);
            while ((line = br.readLine()) != null) {
                line = line.trim();
                System.out.println("Processing line: " + line);
                if (line.charAt(0) == 'P') {
                    String[] probs = new String[2];
                    String[] hiddens = new String[2];
                    String[] eliminate_split = line.split(" ");
                    if (eliminate_split.length < 2) {
                        continue;
                    }
                    probs[0] = eliminate_split[0];
                    hiddens[0] = eliminate_split[1];
                    probs[0] = probs[0].replace("P(", "").replace(")", "");
                    String[] hidden = hiddens[0].split("-");
                    String[] given_split = probs[0].split("\\|");
                    if (given_split.length < 2) {
                        continue;
                    }

                    String query = given_split[0];
                    String[] evi = given_split[1].split(",");
                    Map<String, String> evidence = new HashMap<>();
                    for (String e : evi) {
                        String[] parts = e.split("=");
                        evidence.put(parts[0], parts[1]);
                    }

                    VariableElimination ve = new VariableElimination(query, hidden, evi, network);
                    ans = ans + ve.variableElimination() + "\n";
                } else {
                    String[] given_split = line.split("\\|");
                    if (given_split.length < 1) {
                        continue;
                    }
                    String[] query = given_split[0].split("-");

                    if (query.length < 2) {
                        continue;
                    }
                    ArrayList<bayesianNode> evidence = new ArrayList<>();
                    if (given_split.length > 1) {
                        String[] ev = given_split[1].split(",");
                        for (String s : ev) {
                            String[] one = s.split("=");
                            bayesianNode e = network.getNode(one[0]);
                            evidence.add(e);
                        }
                    }

                    bayesianNode src = network.getNode(query[0]);
                    bayesianNode dest = network.getNode(query[1]);
                    ans = ans + BayesBall.isIndependent(network, src, dest, evidence) + "\n";
                }
            }
        } catch (Exception e) {
            System.out.println("Failed");
            e.printStackTrace();
        }
        return ans;
    }

        private static Map<String, String> parseEvidence(String evidenceString) {
            Map<String, String> evidence = new HashMap<>();
            String[] parts = evidenceString.split(",");
            for (String part : parts) {
                String[] pair = part.split("=");
                if (pair.length == 2) {
                    evidence.put(pair[0], pair[1]);
                }
            }
            return evidence;
        }




    private static ArrayList<bayesianNode> parseEvidenceNodes(bayesianNetwork network, String evidenceString) {
        ArrayList<bayesianNode> evidence = new ArrayList<>();
        String[] parts = evidenceString.split(",");
        for (String part : parts) {
            bayesianNode node = network.getNode(part);
            if (node != null) {
                evidence.add(node);
            }
        }
        return evidence;
    }

    private static List<String> parseHiddenVariables(String line) {
        List<String> hiddenVariables = new ArrayList<>();
        if (line.contains(" ")) {
            String[] parts = line.split(" ");
            if (parts.length > 1) {
                hiddenVariables = Arrays.asList(parts[1].split("-"));
            }
        }
        return hiddenVariables;
    }
}
