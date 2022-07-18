package me.bonjur.managers;

import me.bonjur.config.GameConfig;
import me.bonjur.teams.TeamColors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class Monument {
    Random rand = new Random();
    Location LocationMonument = GameConfig.IMP.MONUMENT;

    public void regen(Player player) {
        int chance = rand.nextInt(40);
        if (chance == 20) {
            int color_wool = Bukkit.getWorld("world").getBlockAt(LocationMonument).getData();
            double rand_health = rand.nextInt(6);
            while (rand_health == 0) {
                rand_health = rand.nextInt(6);
            }
            if (color_wool == 14 && TeamColors.getColor(player) == TeamColors.RED) {
                if (player.getHealth() + rand_health >= 20) player.setHealth(20);
                else {
                    player.setHealth(player.getHealth() + rand_health);
                    String HP = String.format("%.1f", rand_health / 2);
                    player.sendMessage("§f§l Ваш танец понравился монументу, вы получили " + HP + " хп");
                }
            }
            if (color_wool == 11 && TeamColors.getColor(player) == TeamColors.BLUE) {
                if (player.getHealth() + rand_health >= 20) player.setHealth(20);
                else {
                    player.setHealth(player.getHealth() + rand_health);
                    String HP = String.format("%.1f", rand_health / 2);
                    player.sendMessage("§f§l Ваш танец понравился монументу, вы получили " + HP + " хп");
                }
            }
        }
    }
}
