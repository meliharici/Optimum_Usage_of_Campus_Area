package Model.DijkstraModel;

import Model.MapModel.Node;
import java.util.ArrayList;

public class GraphInitializer {

    Graph graph;
    ArrayList<Vertex> vertices;
    ArrayList<Edge> edges;
    Node[][] nodes;

    public GraphInitializer(Node[][] nodes){
        this.nodes = nodes;
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public Graph initializeGraph(){
        for(int i = 0; i < nodes.length; i++){
            for(int j = 0; j < nodes[0].length; j++){
                if(nodes[i][j].nodeState == Node.ROAD || nodes[i][j].nodeState == Node.POI){  // if road or building
                    Vertex v = new Vertex(i,j);
                    vertices.add(v);
                }
            }
        }
        for(int i = 0; i < vertices.size(); i++) {
            Vertex source = vertices.get(i);
            ArrayList<double[]> connections = nodes[source.getX()][source.getY()].connections;
            for(int k = 0; k < connections.size(); k++){
                int targetX = (int) (connections.get(k)[0]);
                int targetY = (int) (connections.get(k)[1]);
                if(nodes[targetX][targetY].nodeState == Node.ROAD || nodes[targetX][targetY].nodeState == Node.POI){ //road or building
                    double weight = connections.get(k)[2];
                    Vertex target = null;
                    for(int m = 0; m < vertices.size(); m++){
                        if(vertices.get(m).getX() == targetX && vertices.get(m).getY() == targetY){
                            target = vertices.get(m);
                        }
                    }
                    String id = "(" + source.getX() + " , " + source.getY() + ")   ->   (" +  targetX + " , " + targetY + ")";
                    Edge e = new Edge(id,source, target, (int) weight);

                    edges.add(e);
                }
            }
        }
        graph = new Graph(vertices,edges);
        return graph;
    }
}








