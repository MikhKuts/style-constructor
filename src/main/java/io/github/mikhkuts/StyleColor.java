package io.github.mikhkuts;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(": ");
        sb.append(color.toString());
        return sb.toString();
    }
}
