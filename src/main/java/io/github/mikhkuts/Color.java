package io.github.mikhkuts;

public class Color {
    public int red;
    public int green;
    public int blue;


    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(String color) {
        String[] cols = color.split("\s*,\s*");
        red = Integer.parseInt(cols[0]);
        green = Integer.parseInt(cols[1]);
        blue = Integer.parseInt(cols[2]);
    }


    @Override
    public String toString() {
        return red + ", " + green + ", " + blue;
    }
}
