package edu.uci.inf122.guildquest.entities.domain_primitives;

public class XYPlace extends Place{
    private int x;
    private int y;
    public XYPlace(Name name, int x, int y) {
        super(name);
        this.x=x;
        this.y=y;
    }
    public String getHint(int curX, int curY){
        int dx = x - curX;
        int dy = y - curY;

        int manhattan = Math.abs(dx) + Math.abs(dy);
        String vertical = "";
        String horizontal = "";

        if (dy > 0) vertical = "north";
        else if (dy < 0) vertical = "south";
        if (dx > 0) horizontal = "east";
        else if (dx < 0) horizontal = "west";

        // Far away → vague hint
        if (manhattan > 2) {
            if (!vertical.isEmpty() && !horizontal.isEmpty()) {
                return "somewhere " + vertical + "-" + horizontal;
            }
            return "somewhere " + (vertical.isEmpty() ? horizontal : vertical);
        }

        // Close → precise instructions
        if (!vertical.isEmpty() && !horizontal.isEmpty()) {
            return vertical + " then " + horizontal;
        }
        return vertical.isEmpty() ? horizontal : vertical;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
