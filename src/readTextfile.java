import java.io.*;
import java.util.ArrayList;

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

     public String readfile(bayesianNetwork bn){
         String ans= "";
         try {
             //BN = xmlFile.read_net(this.lines.get(0));    //checking that the XML file is read right
             for (int i = 1; i < lines.size(); i++) {
                 String line =lines.get(i).trim();
                 System.out.println("Processing line: " +line);
                 if (lines.get(i).charAt(0) == 'P') {
                     String[] probs = new String[2];
                     String[] hiddens = new String[2];
                     String[] eliminate_split = lines.get(i).split(" ");
                     if(eliminate_split.length< 2){
                         System.out.println("Invaild prob query format" +line);
                         continue;
                     }
                     probs[0] = eliminate_split[0];
                     hiddens[0] = eliminate_split[1];
                     probs[0] = probs[0].replace("P(", "").replace(")", "");
                     String[] hidden = hiddens[0].split("-");
                     String[] given_split = probs[0].split("\\|");
                     if(given_split.length< 2){
                         System.out.println("Invaild format for given part of the query" +line);
                         continue;
                     }

                     String query = given_split[0];
                     String[] evi = given_split[1].split(",");
                     VariableElimination ve = new VariableElimination(query, hidden, evi, bn);
                     ans = ans + ve.VariableElimination() + "\n";
                 } else {
                     String[] given_split = lines.get(i).split("\\|");
                     if(given_split.length < 1){
                         System.out.println("Invaild format for BayesBall query" +line);
                         continue;
                     }
                     String[] query = given_split[0].split("_");

                     if(query.length < 2){
                         System.out.println("Invaild format for query nodes" +line);
                         continue;
                     }
                     ArrayList<bayesianNode> evidence = new ArrayList<>();
                     if (given_split.length > 1) {
                         String[] ev = given_split[1].split(",");
                         for (String s : ev) {
                             String[] one = s.split("=");
                             bayesianNode e = bn.returnByName(one[0]);
                             evidence.add(e);
                         }
                     }

                     bayesianNode src =bn.returnByName(query[0]);
                     bayesianNode dest = bn.returnByName(query[1]);
                     ans = ans + BayesBall.isInd(bn, src, dest, evidence) + "\n";
                 }
             }
         }
         catch (Exception e){
             System.out.println("Failed");
             e.printStackTrace();
         }
         return  ans;
     }
}
