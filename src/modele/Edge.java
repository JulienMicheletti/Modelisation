package modele;

class Edge
{
    int from;
    int to;
    int cost;
    boolean newArrete;
    Edge(int x, int y, int cost, boolean newArrete)
    {
        this.from = x;
        this.to = y;
        this.cost = cost;
        this.newArrete = newArrete;
    }

    public void setEdge(int cost){
        this.cost = cost;
    }


    public boolean getNewArrete(){
        return newArrete;
    }

    public void setFrom(int from){
        this.from = from;
    }

    public void setTo(int to){
        this.to = to;
    }

    public int getFrom(){
        return from;
    }

    public int getTo(){
        return to;
    }
}
