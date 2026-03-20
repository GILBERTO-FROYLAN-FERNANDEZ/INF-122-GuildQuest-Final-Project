package edu.uci.inf122.guildquest.ui.playablecharacteruis;

import edu.uci.inf122.guildquest.entities.playablecharacters.Assassin;
import edu.uci.inf122.guildquest.entities.playablecharacters.Cleric;
import edu.uci.inf122.guildquest.entities.playablecharacters.Knight;
import edu.uci.inf122.guildquest.entities.playablecharacters.PlayableCharacter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class PartyStatsUI {
    private final List<PlayableCharacter> characters;

    public PartyStatsUI(List<PlayableCharacter> characters) {
        if (characters == null || characters.isEmpty() || characters.size() < 2) {
            throw new IllegalArgumentException("Party must contain 1 more than 1 character");
        }
        this.characters = characters;
    }

    public PartyStatsUI(PlayableCharacter character1, PlayableCharacter character2) {
        if (character1 == null || character2 == null) {
            throw new IllegalArgumentException("Characters cannot be null.");
        }
        characters = List.of(character1, character2);
    }

    public void display() {
        List<Map<String, String>> statsPerCharacter = new ArrayList<>();

        for (PlayableCharacter character : characters) {
            statsPerCharacter.add(extractStats(character));
        }

        // Union of all stat names (preserves insertion order)
        LinkedHashSet<String> statNames = new LinkedHashSet<>();
        for (Map<String, String> stats : statsPerCharacter) {
            statNames.addAll(stats.keySet());
        }

        // Column widths
        int statColWidth = "Stat".length();
        for (String statName : statNames) {
            statColWidth = Math.max(statColWidth, statName.length());
        }

        List<String> headers = new ArrayList<>();
        for (PlayableCharacter character : characters) {
            headers.add(character.getName().toString());
        }

        List<Integer> valueColWidths = new ArrayList<>();
        for (int i = 0; i < characters.size(); i++) {
            int width = headers.get(i).length();
            for (String statName : statNames) {
                String value = statsPerCharacter.get(i).getOrDefault(statName, "-");
                width = Math.max(width, value.length());
            }
            valueColWidths.add(width);
        }

        // Print table
        printBorder(statColWidth, valueColWidths);
        printHeader(statColWidth, headers, valueColWidths);
        printBorder(statColWidth, valueColWidths);

        for (String statName : statNames) {
            StringBuilder row = new StringBuilder();
            row.append("| ").append(padRight(statName, statColWidth)).append(" ");

            for (int i = 0; i < characters.size(); i++) {
                String value = statsPerCharacter.get(i).getOrDefault(statName, "-");
                row.append("| ").append(padRight(value, valueColWidths.get(i))).append(" ");
            }

            row.append("|");
            System.out.println(row);
        }

        printBorder(statColWidth, valueColWidths);
    }

    private Map<String, String> extractStats(PlayableCharacter character) {
        LinkedHashMap<String, String> stats = new LinkedHashMap<>();

        // Common stats
        stats.put("Name", character.getName().toString());
        stats.put("Class", character.getCharacterClass().toString());
        stats.put("Level", String.valueOf(character.getLevel().getLevelInt()));
        stats.put("Health", String.valueOf(character.getHealth().getHealth()));
        stats.put("Escorting Princess", String.valueOf(character.getWithPrincess().isWithPrincess()));

        // Class-specific stats
        if (character instanceof Knight knight) {
            stats.put("Attack Power", String.valueOf(knight.getAttackPower().getCount()));
            stats.put("Damage Reduction", knight.getDamageReductionMultiplierDisplay().getCount() + "%");
            stats.put("Healing Multiplier", knight.getHealingMultiplier().getCount() + "%");
        } else if (character instanceof Assassin assassin) {
            stats.put("Attack Power", String.valueOf(assassin.getAttackPower().getCount()));
            stats.put("Critical Hit Chance", assassin.getCriticalHitChanceDisplay().getCount() + "%");
        } else if (character instanceof Cleric cleric) {
            stats.put("Healing Power", String.valueOf(cleric.getHealingPower().getCount()));
        }

        return stats;
    }

    private void printHeader(int statColWidth, List<String> headers, List<Integer> valueColWidths) {
        StringBuilder header = new StringBuilder();
        header.append("| ").append(padRight("Stat", statColWidth)).append(" ");
        for (int i = 0; i < headers.size(); i++) {
            header.append("| ").append(padCenter(headers.get(i), valueColWidths.get(i))).append(" ");
        }
        header.append("|");
        System.out.println(header);
    }

    private void printBorder(int statColWidth, List<Integer> valueColWidths) {
        StringBuilder border = new StringBuilder();
        border.append("+").append("-".repeat(statColWidth + 2));
        for (int width : valueColWidths) {
            border.append("+").append("-".repeat(width + 2));
        }
        border.append("+");
        System.out.println(border);
    }

    private String padRight(String value, int width) {
        return String.format("%-" + width + "s", value);
    }

    private String padCenter(String value, int width) {
        int totalPadding = width - value.length();
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        return " ".repeat(leftPadding) + value + " ".repeat(rightPadding);
    }
}