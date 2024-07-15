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

    public static String readfile(String filePath, bayesianNetwork network) {
        StringBuilder result = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = br.readLine();
            bayesianNetwork net = xmlFile.read_net(line);
            if (net != null) {
                System.out.println("Network successfully loaded.");
            } else {
                System.out.println("Failed to load the network.");
                return "Failed to load the network.";
            }

            while ((line = br.readLine()) != null) {
                line = line.trim();
                System.out.println("Processing line: " + line);
                if (line.charAt(0) == 'P') {
                    String[] parts = line.split(" ");
                    if (parts.length < 2) {
                        continue;
                    }

                    String queryPart = parts[0].substring(2, parts[0].length() - 1);
                    String hiddenPart = parts[1];

                    String[] querySplit = queryPart.split("\\|");
                    if (querySplit.length < 2) {
                        continue;
                    }

                    String query = querySplit[0];
                    String evidenceString = querySplit[1];
                    Map<String, String> evidence = parseEvidence(evidenceString);
                    List<String> hiddenVariables = parseHiddenVariables(hiddenPart);

                    VariableElimination ve = new VariableElimination(query, hiddenVariables.toArray(new String[0]), evidenceString.split(","), net);
                    result.append(ve.variableElimination()).append("\n");
                } else {
                    String[] parts = line.split("\\|");
                    if (parts.length < 1) {
                        continue;
                    }
                    String[] query = parts[0].split("-");
                    if (query.length < 2) {
                        continue;
                    }

                    ArrayList<bayesianNode> evidence = new ArrayList<>();
                    if (parts.length > 1) {
                        evidence = parseEvidenceNodes(net, parts[1]);
                    }

                    bayesianNode src = net.getNode(query[0]);
                    bayesianNode dest = net.getNode(query[1]);
                    result.append(BayesBall.isIndependent(net, src, dest, evidence)).append("\n");
                }
            }
        } catch (Exception e) {
            System.out.println("Failed");
            e.printStackTrace();
        }
        return result.toString();
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
            bayesianNode node = network.getNode(part.split("=")[0]);
            if (node != null) {
                evidence.add(node);
            }
        }
        return evidence;
    }

    private static List<String> parseHiddenVariables(String line) {
        List<String> hiddenVariables = new ArrayList<>();
        if (line.contains("-")) {
            String[] parts = line.split("-");
            if (parts.length > 1) {
                hiddenVariables = Arrays.asList(parts);
            }
        }
        return hiddenVariables;
    }
}

