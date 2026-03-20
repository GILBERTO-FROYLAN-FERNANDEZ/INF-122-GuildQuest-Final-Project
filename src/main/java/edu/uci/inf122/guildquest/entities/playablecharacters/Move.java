package edu.uci.inf122.guildquest.entities.playablecharacters;

import edu.uci.inf122.guildquest.ui.TerminalGrid;

import java.util.List;

public interface Move {
    public static String toOptionsStr(List<Move.ValidMoves> moves) {
        StringBuilder res = new StringBuilder();
        for (Move.ValidMoves m : moves){
            switch (m){
                case ATTACK -> res.append("Attack: North (n), South (s), East (e), West (w)\n");
                case TRAVEL -> res.append("Move: North (n), South (s), East (e), West (w)\n");
                case HEAL_OTHER -> res.append("Heal other: North (n), South (s), East (e), West (w)\n");
                case HEAL_SELF -> res.append("Heal self: (heal self)\n");
                case TAKE_ITEM -> res.append("Take Item: North (n), South (s), East (e), West (w)\n");
                case USE_ITEM -> res.append("Use Item: (use item)\n");
                case REQUEST_HINT -> res.append("Request Hint: (r)\n");
                case SKIP -> res.append("skip move (0)\n");
            }
        }
        return res.toString();
    }
    public static String toOptionsRegexStr(List<Move.ValidMoves> moves) {
        StringBuilder res = new StringBuilder();
        for (Move.ValidMoves m : moves){
            switch (m){
                case ATTACK -> res.append("(attack [nsew])");
                case TRAVEL -> res.append("(move [nsew])");
                case HEAL_OTHER -> res.append("(heal [nsew])");
                case HEAL_SELF -> res.append("(heal self)");
                case TAKE_ITEM -> res.append("(take item [nsew])");
                case USE_ITEM -> res.append("(use item)");
                case REQUEST_HINT -> res.append("(r)");
                case SKIP -> res.append("(0)");
            }
            res.append('|');
        }
        res.deleteCharAt(res.length()-1);
        return res.toString();
    }

    enum ValidMoves{
        TRAVEL,
        ATTACK,
        HEAL_OTHER,
        HEAL_SELF,
        TAKE_ITEM,
        USE_ITEM,
        REQUEST_HINT,
        SKIP
    }


    void execute(TerminalGrid state, ValidMoves move);
}
