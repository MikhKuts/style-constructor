package io.github.mikhkuts;

import java.util.HashMap;

public class StyleColor {
    public String name;
    public Color color;

    public StyleColor(String name, Color color) {
        this.name = name;
        this.color = color;
    }
    public StyleColor(String name, String  color) {
        this.name = name;
        this.color = new Color(color);
    }
}
