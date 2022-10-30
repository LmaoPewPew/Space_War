package sn.thu.space_war;

public class Vector {
    public double x;
    public double y;

    public Vector() {
        this.set(0, 0);
    }


    public Vector(double x, double y) {
        this.set(x, y);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void add(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public void mul(double m) {
        this.x *= m;
        this.y *= m;
    }

    public double getLength() {
        return Math.sqrt(this.x * this.x + this.y * this.y);

    }

    public void setLength(double L) {
        double currentLength = this.getLength();

        if (currentLength == 0) return;

        this.mul(1 / currentLength);

        this.mul(L);

    }

    public double getAngle() {
        return Math.toDegrees(Math.atan2(this.y, this.x));
    }

    public void setAngle(double angDeg) {
        double L = this.getLength();
        double angRad = Math.toRadians(angDeg);
        this.x = L * Math.cos(angRad);
        this.y = L * Math.sin(angRad);
    }
}
