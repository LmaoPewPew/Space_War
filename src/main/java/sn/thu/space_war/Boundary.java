package sn.thu.space_war;

public class Boundary {
    double x;
    double y;
    double width;
    double height;

    public Boundary() {
        this.setPos(0, 0);
        this.setSize(1, 1);

    }

    public Boundary(double x, double y, double w, double h) {
        this.setPos(x, y);
        this.setSize(w, h);

    }

    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;

    }

    public void setSize(double w, double h) {
        this.height = h;
        this.width = w;
    }

    public boolean overlaps(Boundary other) {

        boolean noOverlap = this.x + this.width < other.x || other.x + other.width < this.x || this.y + this.height < other.y || other.y + other.height < this.y;

        return !noOverlap;

    }
}
