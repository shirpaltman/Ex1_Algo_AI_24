import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
public class BayesBall {
    private bayesianNetwork network;
    private static Set<String> haveSeen;   //For keeping track og nodes so I can prevent cycles

    /**
     * this function determines if 2 given nodes in a  Bayesian Network
     * will determine if the nodes are independent when were given a set of evidence nodes.
     *
     * @param BayesianNetwork The Bayesian Network
     * @param source          The source node
     * @param dest            The destination node
     * @param evidence        The list of evidence nodes
     * @return "yes" if source and dest are independent given the evidence,else return "no"
     */
    public static String isIndependent(bayesianNetwork BayesianNetwork, bayesianNode source, bayesianNode dest, ArrayList<bayesianNode> evidence){
        haveSeen = new HashSet<>();    // initializing the haveSeen set for every independence check
        boolean independent = bayesBall(BayesianNetwork, source, dest, evidence, null);
        if (independent == true) {
            return "yes ";     //independent
        } else {
            return "no ";
        }
    }



    private  static  boolean bayesBall(bayesianNetwork BayesianNetwork ,bayesianNode source, bayesianNode dest,ArrayList<bayesianNode>evidence,bayesianNode lastSeen){

        //if the source node is the same as the destination node,then of course their dependent
        if( source == null ||source.equals(dest)){
            return false;
        }
        //Marking the current node as haveSeen
        haveSeen.add(source.getName());

        //If the current node is in the evidence set
        if(evidence.contains(source)){

            //If after visiting a child,the ball is blocked
            if(lastSeen!=null && source.getChildren().contains(lastSeen.getName())){
                return true;
                // Its independent because the path is blocked
            }

            for(String parentName :source.getParents()){
                bayesianNode parent = BayesianNetwork.getNode(parentName);
//                if (!haveSeen.contains(parent.getName())){
                    if(!bayesBall(BayesianNetwork,parent,dest,evidence,source)){
                        return false;   // Going to be dependent if a path through a child isn't blocked
                    }
//                }
            }
        }
        else{
            //if the current node  isn't in the evidence set
            //if were coming from a child or it is the starting node ,we'll have to check both parents
            if(lastSeen == null || source.getChildren().contains(lastSeen.getName())){
                for(String parentName : source.getParents()) {
                    bayesianNode parent = BayesianNetwork.getNode(parentName);
                    if(!haveSeen.contains(parent.getName())){
                        if(!bayesBall(BayesianNetwork,parent,dest,evidence,source)){
                            return false;  //Is dependent if any path through a parent isnt clogged
                        }
                    }
                }
                for(String childName :source.getChildren()){
                    bayesianNode child = BayesianNetwork.getNode(childName);
                    if (!haveSeen.contains(child.getName())){
                        if (!bayesBall(BayesianNetwork,child,dest,evidence,source)){
                            return false;   // Is dependent if any path through a child isn't clogged
                        }
                    }
                }
            }
            else {
                //if I'm coming from a parent ,we'll check just the children
                for (String childName : source.getChildren()){
                    bayesianNode child = BayesianNetwork.getNode(childName);
                    if(!haveSeen.contains(child.getName())){
                        if(!bayesBall(BayesianNetwork,child,dest,evidence,source)){
                            return false;         // Is dependent ia there is a path through a child isn't clogged
                        }
                    }
                }
            }
        }
        //If there is no paths that lead to dependency ,return true
        return true;
    }
}