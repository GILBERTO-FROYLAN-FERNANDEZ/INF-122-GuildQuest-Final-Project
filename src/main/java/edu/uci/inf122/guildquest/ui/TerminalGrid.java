package edu.uci.inf122.guildquest.ui;

import edu.uci.inf122.guildquest.api.Status;
import edu.uci.inf122.guildquest.api.state.GridCell;
import edu.uci.inf122.guildquest.api.state.GridState;
import edu.uci.inf122.guildquest.entities.Entity;
import edu.uci.inf122.guildquest.entities.playablecharacters.Move;
import edu.uci.inf122.guildquest.entities.playablecharacters.PlayableCharacter;

import java.util.List;

public class TerminalGrid extends GridState {
//    private GridState grid;
    private Page out;
    public TerminalGrid(int l, int w){
        super(l, w);
        out = Page.getPage();
    }

    @Override
    public void render() {
        out.print(getGridStr());
    }

    public String getGridStr(){
        int longestNameLen=0;
        for (List<GridCell> row : grid){
            if (row==null) continue;
            for (GridCell cell : row){
                if (cell.isEmpty()) continue;
                longestNameLen = Math.max(cell.getContent().get(cell.getContent().size()-1).getName().length(), longestNameLen);
            }
        }

        int leftPad;
        int rightPad;
        int padding;
        StringBuilder res = new StringBuilder();
        res.append("-".repeat((longestNameLen+4)*grid.size())).append('\n');
        for (List<GridCell> row : grid){
            if (row==null) continue;
            for (GridCell cell : row){
                if (cell.isEmpty()) {
                    res.append("|").append(" ".repeat(longestNameLen + 2)).append("|");
                    continue;
                }
                padding = longestNameLen - cell.getContent().get(cell.getContent().size()-1).getName().length();
                leftPad = padding / 2;
                rightPad = padding - leftPad;
                res.append("| ")
                        .append(" ".repeat(leftPad))
                        .append(cell.getContent().get(cell.getContent().size() - 1).getName())
                        .append(" ".repeat(rightPad))
                        .append(" |");
            }
            res.append('\n');
            res.append(("|"+"-".repeat((longestNameLen+2))+"|").repeat(grid.size())).append('\n');
        }
//        res.append("-".repeat((longestNameLen+4)*grid.size())).append('\n');
        return res.toString();
    }

    @Override
    public void changeState() {

    }

    /**
     * Takes valid moves and accepts input until one is chosen.
     *
     * @param pc the pc
     * @return the status
     */
    public static Status acceptPlayerInput(PlayableCharacter pc) {
        List<Move.ValidMoves> moves = pc.getMoves();
        String moveString = Move.toOptionsStr(moves);
        String moveRegex = Move.toOptionsRegexStr(moves);
        String input = Page.getPage().acceptStrUntil(pc.getName() + "'s turn!\n" + moveString, moveRegex);
        return new Status(Status.Option.SUCCESS, input);
    }

    /**
     * Update the target arr to be 1 adjacent in the direction specified.
     *
     * @param target    the target
     * @param direction the direction
     */
    public static void moveCordInDirection(int[] target, char direction){
        switch (direction) {
            case 'e' -> target[1] += 1;
            case 'w' -> target[1] -= 1;
            case 's' -> target[0] += 1;
            case 'n' -> target[0] -= 1;
            default -> throw new IllegalStateException("Unexpected direction: " + direction);
        }
    }
    /**
     * Get the coordinates for the GridCell in the cardinal direction (nswe) adjacent.
     *
     * @param entity    the
     * @param direction the cardinal direction
     * @return the adjacent grid cell
     */
    public GridCell cellAdjacent(Entity entity, char direction){
        int[] target = getLocationCords(getLocation(entity));
        TerminalGrid.moveCordInDirection(target, direction);
        return getCell(target[0], target[1]);
    }


}
