package Model.MapModel;

import java.util.ArrayList;

public class Path {
    ArrayList<int[]> pathOfNodes;
    ArrayList<Double> pathLength;
    double pathProgress = 0;
    int currentNodeIndex = 0;

    public Path(){
        pathOfNodes = new ArrayList<int[]>();
    }

    public void appendNode(int x,int y){
        int[] node = new int[2];
        node[0] = x;
        node[1] = y;
        pathOfNodes.add(node);
        calculateLength();
    }

    public boolean isDestinationReached(){
        return currentNodeIndex>=pathOfNodes.size();
    }

    public int[] getCurrentNode(){
        return pathOfNodes.get(currentNodeIndex);
    }

    public int[] getNextNode(){
        if(currentNodeIndex < pathOfNodes.size()){
            return pathOfNodes.get(currentNodeIndex+1);
        }
        return pathOfNodes.get(pathOfNodes.size()-1);
    }

    public void reset(){
        pathProgress = 0;
        currentNodeIndex = 0;
    }

    public double getPathProgressRatio(){
        double lenghtTillNextNode = pathLength.get(currentNodeIndex);

        return pathProgress/lenghtTillNextNode;

    }

    public void calculateLength(){
        pathLength = new ArrayList<Double>();

        int[] previousNode = pathOfNodes.get(0);

        for(int i = 1; i<pathOfNodes.size(); i++){
            int[] currentNode = pathOfNodes.get(i);

            double distance = Math.sqrt(Math.pow(currentNode[0]-previousNode[0],2)+Math.pow(currentNode[1]-previousNode[1],2));
            pathLength.add(distance);

            previousNode = currentNode;
        }
        pathLength.add(0.0001);
    }

    public double getPathLength(){
        double totalLength = 0;
        for(double value : pathLength){
            totalLength += value;
        }
        return totalLength;
    }

    public void move(double speed){
        double totalProgress = pathProgress + speed;

        while(currentNodeIndex <pathOfNodes.size()-1){
            double lengthTillNextNode = pathLength.get(currentNodeIndex);

            if(lengthTillNextNode>totalProgress){
                pathProgress = totalProgress;
                break;
            }
            else{
                currentNodeIndex++;
                totalProgress -= lengthTillNextNode;
            }

        }

        if(currentNodeIndex >= pathOfNodes.size()-1){
            pathProgress = 0;
            System.out.println("Destination Reached !");
        }


    }

    public String toString() {
        String str = "";
        int[] currentNode = pathOfNodes.get(currentNodeIndex);
        double pathProgressRatio = getPathProgressRatio();

        str += "Current Node = "+currentNodeIndex+"("+currentNode[0]+","+currentNode[1]+") - Progress = "+pathProgressRatio;
        return str;
    }
}
