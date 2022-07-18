package me.bonjur.teams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public enum TeamColors {
    RED("§cкрасная", "§cкрасные"), BLUE("§9синяя", "§9синие"), SPECTATOR("§dзритель", "§dпидорасы");

    private final String name1;

    private final String name2;

    TeamColors(String name1, String name2) {
        this.name1 = name1;
        this.name2 = name2;
    }

    public String getName1() {
        return this.name1;
    }

    public String getName2() {
        return this.name2;
    }

    public static TeamColors getColor(Player player) {

        if (player.getName() != null) {
            if (player.getPlayerListName().contains("§c§l")) {
                return TeamColors.RED;
            } else if (player.getPlayerListName().contains("§9§l")) {
                return TeamColors.BLUE;
            }
        }
        return TeamColors.SPECTATOR;
    }


}
