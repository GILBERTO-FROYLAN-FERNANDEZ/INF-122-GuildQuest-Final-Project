package edu.uci.inf122.guildquest.api.win_conditions;

import edu.uci.inf122.guildquest.entities.domain_primitives.Text;
import edu.uci.inf122.guildquest.entities.playablecharacters.PlayableCharacter;

public class GetToTargetXYWithPrincessCondition extends GetToTargetXYCondition{

    /**
     * Win condition constructor, accepts the destination coordinates
     *
     * @param x the target x
     * @param y the target y
     */
    public GetToTargetXYWithPrincessCondition(int x, int y) {
        super(x, y);
    }

    public void updateCondition(int curX, int curY, PlayableCharacter p){
        isThere = curX==x && curY==y && p.getWithPrincess().isWithPrincess();
    }
    @Override
    public Text loseMessage() {
        return new Text("Did not arrive to target position with the princess!");
    }

    @Override
    public Text winMessage() {
        return new Text("Got to target position with the princess!");
    }


}
