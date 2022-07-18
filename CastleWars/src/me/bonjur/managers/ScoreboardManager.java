package me.bonjur.managers;

import me.bonjur.CastleWars;
import me.bonjur.Main;
import me.bonjur.config.GameConfig;
import me.bonjur.teams.TeamColors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
    CastleWars castleWars = Main.castleWars;

    public void update() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            org.bukkit.scoreboard.ScoreboardManager m = Bukkit.getScoreboardManager();
            Scoreboard b = m.getNewScoreboard();

            Objective o = b.registerNewObjective("CastleWars", "");
            o.setDisplaySlot(DisplaySlot.SIDEBAR);
            if (TeamColors.getColor(player) == TeamColors.RED) o.setDisplayName("§f§lВы §c§lкрасная §f§lкоманда!");
            if (TeamColors.getColor(player) == TeamColors.BLUE) o.setDisplayName("§f§lВы §9§lсиния §f§lкоманда!");
            if (TeamColors.getColor(player) == TeamColors.SPECTATOR) o.setDisplayName("§f§lCastleWars");

            Score red_score = o.getScore("§c§lКрасные " + castleWars.redTeam.getPoints() + "§7§l/§c§l" + GameConfig.IMP.POINTS.FLAGS + "§7§l: ");
            red_score.setScore(castleWars.redTeam.getKills());
            Score blue_score = o.getScore("§9§lСиние " + castleWars.blueTeam.getPoints() + "§7§l/§9§l" + GameConfig.IMP.POINTS.FLAGS + "§7§l: ");
            blue_score.setScore(castleWars.blueTeam.getKills());

            player.setScoreboard(b);

        }
    }
}
