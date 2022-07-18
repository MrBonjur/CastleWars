package me.bonjur;

import me.bonjur.config.GameConfig;
import me.bonjur.managers.PlayerManager;
import me.bonjur.managers.ScoreboardManager;
import me.bonjur.teams.TeamColors;
import me.bonjur.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

public class CastleWars {
    public TeamManager redTeam = new TeamManager(TeamColors.RED);
    public TeamManager blueTeam = new TeamManager(TeamColors.BLUE);
    PlayerManager playerManager = new PlayerManager();
    ScoreboardManager scoreboard = new ScoreboardManager();
    GameConfig CONFIG = GameConfig.IMP;


    public void gameFinish(TeamColors ColorTeam) {

        String text = "§f§lПобедила " + ColorTeam.getName1() + " §f§lкоманда!";
        Bukkit.broadcastMessage("§f§lИгра окончена! Победила " + ColorTeam.getName1() + " §f§lкоманда §f§lСо счетом §c§l" + redTeam.getPoints() + "§f§l:§9§l" + blueTeam.getPoints());

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(text, "§f§lСо счетом §c§l" + redTeam.getPoints() + "§f§l:§9§l" + blueTeam.getPoints());
            playerManager.clearInventory(player);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setPlayerListName(player.getName());
            playerManager.teleport(player, "lobby-leave");
        }
        redTeam.setPoints(0);
        blueTeam.setPoints(0);
        redTeam.setKills(CONFIG.POINTS.KILLS);
        blueTeam.setKills(CONFIG.POINTS.KILLS);

        scoreboard.update();
    }



    public void setFlag(TeamColors color) {
        Block block;
        DyeColor colorFlag;
        if (color == TeamColors.RED){
            block = Bukkit.getWorld("world").getBlockAt(GameConfig.IMP.FLAGS.RED);
            colorFlag = DyeColor.RED;
        }
        else {
            block = Bukkit.getWorld("world").getBlockAt(GameConfig.IMP.FLAGS.BLUE);
            colorFlag = DyeColor.BLUE;
        }
        block.setType(Material.WOOL);
        BlockState state = block.getState();
        Wool wool = (Wool) state.getData();
        wool.setColor(colorFlag);
        state.update();
    }

    public void checkDropFlag(Player player) {
        if (redTeam.isThief(player)) {
            Bukkit.broadcastMessage("§9§l" + player.getName() + " §f§lпотерял флаг §c§lкрасных§f§l!");
            redTeam.setThief(null);
            setFlag(TeamColors.RED);
        }

        if (blueTeam.isThief(player)) {
            Bukkit.broadcastMessage("§c§l" + player.getName() + " §f§lпотерял флаг §9§lсиних§f§l!");
            blueTeam.setThief(null);
            setFlag(TeamColors.BLUE);
        }
    }

}
