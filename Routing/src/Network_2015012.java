import java.util.ArrayList;
import java.util.Scanner;

public class Network_2015012 {
    int [][] graph;
    Node1_2015012[] list1;
    Node2_2015012[] list2;
    int n;

    public void createGraph(int n, String edges){
        int [][] matrix = new int[n][n];
        String [] edgeArray = edges.split(" ");
        for(int i=0;i<edgeArray.length-1;i+=3){
            matrix[edgeArray[i].charAt(0)-'A'][edgeArray[i+1].charAt(0)-'A'] = Integer.parseInt(edgeArray[i+2]);
            matrix[edgeArray[i+1].charAt(0)-'A'][edgeArray[i].charAt(0)-'A'] = Integer.parseInt(edgeArray[i+2]);
        }
        this.graph = matrix;
    }

    public static void printHistory(int x, ArrayList<int[]> history){
        System.out.println("\nWorking for Router " + (char)(x+'A'));
        System.out.print("Step\t");
        for(int i=0;i<history.get(0).length;i++){
            System.out.print((char)(i+'A')+"\t");
        }
        System.out.println();
        for(int i=0;i<history.size();i++){
            System.out.print("(" + Integer.toString(i) + ")\t\t");
            for(int j=0;j<history.get(i).length;j++){
                if(history.get(i)[j]==10000){
                    System.out.print("∞" + "\t");
                }
                else{

                    System.out.print(history.get(i)[j] + "\t");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printHistory2(int n, Node2_2015012[] list){
        ArrayList< ArrayList<int [] >> history = new ArrayList<>();
        for(int i=0;i<n;i++){
            history.add(list[i].getHistory());
        }
        System.out.println("\nRouting:");
        //  IS ROUTER
        // STEP
        // ELEMENT
        for(int i=0;i<n;i++){

            System.out.println("\nStep " + Integer.toString(i));
            for(int j=0;j<n;j++){

                System.out.println("\nRouter " + (char)(j+'A') );
                for(int k=0;k<n;k++){

                    for(int l=0;l<n;l++){
                        if(k!=j && i==0){
                            int h = 10000;
                            if(h==10000){
                                System.out.print("∞\t");
                            }
                            else{

                                System.out.print(Integer.toString(h)+ "\t");
                            }

                        }
                        else{
                            int h = history.get(k).get(i)[l];
                            if(h==10000){
                                System.out.print("∞\t");
                            }
                            else{

                                System.out.print(Integer.toString(h)+ "\t");
                            }
                        }
                    }
                    System.out.println();
                }

            }

        }
    }

    public static void printRT(int x, int[] RT){
        System.out.println("\nRouting table for Router " + (char)(x+'A'));
        System.out.println("Destination\tLink");
        for(int i=0;i<RT.length;i++){
            System.out.println((char)(i+'A') + "\t\t\t(" + (char)(x+'A') + "," + (char)(RT[i]+'A') + ")\t");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Network_2015012 network = new Network_2015012();

        System.out.print("Enter Routing Type (1/2): ");
        Scanner sc = new Scanner(System.in);
        String type = sc.nextLine();
        System.out.print("Enter number of nodes in the network: ");
        network.n = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Network Edges: ");
        String edges = sc.nextLine();
        network.createGraph(network.n, edges);
        if(type.equals("1")){
            network.list1 = new Node1_2015012[network.n];
            for(int i=0;i<network.n;i++){
                network.list1[i] = new Node1_2015012(network.graph,i);
                network.list1[i].findDistances();
                network.list1[i].makeTable();
                printHistory(i,network.list1[i].getHistory());
                printRT(i,network.list1[i].getRoutingTable());
            }

        }
        else{
            network.list2 = new Node2_2015012[network.n];
            for(int i=0;i<network.n;i++){
                network.list2[i] = new Node2_2015012(network.graph,i);
                network.list2[i].findDistances();
                network.list2[i].makeTable();
            }
            printHistory2(network.n, network.list2);
            for(int i=0;i<network.n;i++){

                printRT(i,network.list2[i].getRoutingTable());
            }

        }
        while(true){

            System.out.print("Enter edge to be replaced: ");
            String edge = sc.nextLine();
            String [] edgeArray = edge.split(" ");
            network.graph[edgeArray[0].charAt(0)-'A'][edgeArray[1].charAt(0)-'A'] = Integer.parseInt(edgeArray[2]);
            network.graph[edgeArray[1].charAt(0)-'A'][edgeArray[0].charAt(0)-'A'] = Integer.parseInt(edgeArray[2]);
            if(type.equals("1")){
                network.list1 = new Node1_2015012[network.n];
                for(int i=0;i<network.n;i++){
                    network.list1[i] = new Node1_2015012(network.graph,i);
                    network.list1[i].findDistances();
                    network.list1[i].makeTable();
                    printHistory(i,network.list1[i].getHistory());
                    printRT(i,network.list1[i].getRoutingTable());
                }

            }
            else{
                network.list2 = new Node2_2015012[network.n];
                for(int i=0;i<network.n;i++){
                    network.list2[i] = new Node2_2015012(network.graph,i);
                    network.list2[i].findDistances();
                    network.list2[i].makeTable();
                }
                printHistory2(network.n, network.list2);
                for(int i=0;i<network.n;i++){

                    printRT(i,network.list2[i].getRoutingTable());
                }

            }
        }


    }
}
