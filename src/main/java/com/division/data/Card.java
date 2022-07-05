package com.division.data;

public class Card {

    private int shape; //2~14, A = 14
    private Color color;

    public Card(Color value, int shape) {
        this.shape = shape;
        this.color = value;
    }

    public enum Color {
        DIAMOND,
        CLOVER,
        SPADE,
        HEART
    }

    public Color getColor() {
        return color;
    }

    public int getShape() {
        return shape;
    }

    @Override
    public String toString() {
        String first, second;
        switch (color) {
            case HEART:
                first = "§c♥";
                break;
            case SPADE:
                first = "§7♠";
                break;
            case DIAMOND:
                first = "§b♦";
                break;
            default:
            case CLOVER:
                first = "§a♣";
                break;
        }
        if (shape <= 10)
            second = "§f" + shape;
        else if (shape == 11)
            second = "§fJ";
        else if (shape == 12)
            second = "§fQ";
        else if (shape == 13)
            second = "§fK";
        else
            second = "§fA";
        return first + second;

    }
}
