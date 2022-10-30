package sn.thu.space_war;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite {
    public Vector pos;
    public Vector vel; //velocity
    public double rot; //deg
    public Rectangle boundary;
    public Image img;


    public Sprite() {
        this.pos = new Vector();
        this.vel = new Vector();
        this.rot = 0;
        this.boundary = new Rectangle();
    }

    public Sprite(String imgFileName) {
        this();
        setImage(imgFileName);
    }

    public void setImage(String imgFileName) {
        this.img = new Image(imgFileName);
        this.boundary.setSize(this.img.getWidth(), this.img.getHeight());
    }


    public Rectangle getBoundary() {
        this.boundary.setPos(this.pos.x, this.pos.y);
        return this.boundary;
    }

    public boolean overlaps(Sprite other) {
        return this.getBoundary().overlaps(other.getBoundary());
    }

    public void update(double deltaTime) {
        this.pos.add(this.vel.x * deltaTime, this.vel.y * deltaTime);
    }

    public void render(GraphicsContext context) {
        context.save();

        context.translate(this.pos.x, this.pos.y);
        context.rotate(this.rot);
        context.translate(-this.img.getWidth() / 2, -this.img.getHeight() / 2);
        context.drawImage(this.img, 0, 0);

        context.restore();
    }
}
