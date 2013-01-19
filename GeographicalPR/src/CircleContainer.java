class CircleContainer implements Comparable<CircleContainer> {
    private Circle circle;
    private int count;
   
    @Override
    public int compareTo(CircleContainer cc) {
        return Double.compare(cc.count, this.count);
    }
    
    public CircleContainer(Circle circle, int count) {
        super();
        this.circle = circle;
        this.count = count;
    }

    public Circle getCircle() {
        return circle;
    }

    public int getCount() {
        return count;
    }
}