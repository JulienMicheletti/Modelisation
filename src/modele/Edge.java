package modele;

class Edge
{
    int from;
    int to;
    int cost;
    Edge(int x, int y, int cost)
    {
        this.from = x;
        this.to = y;
        this.cost = cost;
    }

    public void setEdge(int cost){
        this.cost = cost;
    }

    public void setFrom(int from){
        this.from = from;
    }

    public void setTo(int to){
        this.to = to;
    }

}
