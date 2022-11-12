package sn.thu.space_war;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class SpaceWarsTitle extends Pane {
    private final Text text;

    public SpaceWarsTitle(String name) {
        StringBuilder spread = new StringBuilder();
        for (char c : name.toCharArray()) {
            spread.append(c).append(" ");
        }

        text = new Text(spread.toString());
        text.setFont(new Font("Arial Black", 36));
        text.setFill(Color.WHITE);
        text.setEffect(new DropShadow(20, Color.BLACK));

        getChildren().addAll(text);
    }

    /*
    public SpaceWarsTitle(String name) {
        String spread = "";
        for (char c : name.toCharArray()) {
            spread += c + " ";
        }

        text = new Text(spread);
        text.setFont(new Font("Arial Black", 36));
        text.setFill(Color.WHITE);
        text.setEffect(new DropShadow(20, Color.BLACK));

        getChildren().addAll(text);
    }
     */


    public double getTitleWidth() {
        return text.getLayoutBounds().getWidth();
    }

    public double getTitleHeight() {
        return text.getLayoutBounds().getHeight();
    }
}
