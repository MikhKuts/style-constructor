package io.github.mikhkuts;

public class Color {
    private int red;
    private int green;
    private int blue;


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

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }
}
