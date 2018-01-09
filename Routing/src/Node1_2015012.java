import java.util.ArrayList;
import java.util.Arrays;

// Link-State
public class Node1_2015012 {

    private int[][] graph;
    private int nodeNumber;
    private int[] routingTable;
    private int totalNodes;
    private int[] parent;
    private int[] dist;
    private ArrayList<int []> history;

    public Node1_2015012(int[][] graph, int nodeNumber){
        this.graph = graph;
        this.nodeNumber = nodeNumber;
        totalNodes = graph.length;
        routingTable = new int[totalNodes];
        history = new ArrayList<>();

    }

    public ArrayList<int[]> getHistory() {
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
        int[] N_ = new int[totalNodes];
        dist = new int[totalNodes];
        parent = new int[totalNodes];

        for(int i=0;i<totalNodes;i++){
            N_[i]=0;
            dist[i] = inf;
            parent[i] = -1;
        }

        dist[nodeNumber] = 0;
//        history.add(Arrays.copyOf(dist,dist.length));
        parent[nodeNumber] = nodeNumber;

        for(int i=0;i<totalNodes;i++){

            int minInd = -1;
            int min = inf;
            for(int j=0;j<totalNodes;j++){
                if(N_[j]==0 && dist[j]<=min){
                    min = dist[j];
                    minInd = j;
                }
            }

            int u = minInd;
            N_[u] = 1;

            for(int v=0;v<totalNodes;v++){

                if(N_[v]==0 && graph[u][v]>0 && dist[u] + graph[u][v] < dist[v]){
                    dist[v] = dist[u] + graph[u][v];
                    parent[v] = u;
                }
            }
            history.add(Arrays.copyOf(dist,dist.length));

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
