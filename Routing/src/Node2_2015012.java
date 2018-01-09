import java.util.ArrayList;
import java.util.Arrays;

// Distance-Vector
public class Node2_2015012 {

    private int[][] graph;
    private int nodeNumber;
    private int[] routingTable;
    private int totalNodes;
    private int[] parent;
    private int[] dist;
    private ArrayList<int []> history;

    public Node2_2015012(int[][] graph, int nodeNumber){
        this.graph = graph;
        this.nodeNumber = nodeNumber;
        totalNodes = graph.length;
        routingTable = new int[totalNodes];
        history = new ArrayList<>();

    }

    public ArrayList<int[]> getHistory() {
//        for(int i =0; i<history.size();i++){
//            for(int j=0; j<history.get(0).length;j++)
//                System.out.print(Integer.toString(history.get(i)[j]) + " ");
//            System.out.println();
//        }
//        System.out.println();
        return history;
    }

    public void setGraph(int[][] graph) {
        this.graph = graph;
    }

    public int[] getRoutingTable() {

        return routingTable;
    }

    public void findDistances(){

        int inf = 10000;
        dist = new int[totalNodes];
        parent = new int[totalNodes];

        for(int i=0;i<totalNodes;i++){
            dist[i] = inf;
            parent[i] = -1;
        }

        dist[nodeNumber] = 0;

        int [] temp = new int[totalNodes];
        for(int i=0;i<totalNodes;i++){
            if(graph[nodeNumber][i]>0){

                temp[i]=graph[nodeNumber][i];
            }
            else{
                temp[i] = inf;
            }
        }
        temp[nodeNumber]=0;

        history.add(Arrays.copyOf(temp,temp.length));
        parent[nodeNumber] = nodeNumber;

        for(int i=0;i<totalNodes-1;i++) {
            for (int j = 0; j < totalNodes; j++) {
                for (int k = 0; k < totalNodes; k++) {
                    if (graph[j][k] > 0) {

                        int u = j;
                        int v = k;
                        int w = graph[u][v];
                        if (dist[u] != inf && dist[u] + w < dist[v]) {
                            dist[v] = dist[u] + w;
                            parent[v] = u;
                        }
                    }
                }
            }

            history.add(Arrays.copyOf(dist, dist.length));
        }
        this.makeTable();

    }
    public int via(int i){
        if(parent[i]==nodeNumber){
            return i;
        }
        return via(parent[i]);
    }

    public void makeTable(){
        for(int i=0;i<totalNodes;i++)
        {
            routingTable[i] = via(i);
        }

    }
}
