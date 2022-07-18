package me.bonjur.managers;

import me.bonjur.CastleWars;
import me.bonjur.config.GameConfig;
import me.bonjur.teams.TeamColors;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;

import static me.bonjur.Main.castleWars;

public class PlayerManager {
    public void clearInventory(Player player) {
        player.getInventory().clear();
        if (castleWars.redTeam.isThief(player) || castleWars.blueTeam.isThief(player)) {
            return;
        }

        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }


    public void teleport(Player player, String key) {
        String[] arr = GameConfig.IMP.getString(key).split(";");

        String world = arr[0];

        double x = Double.parseDouble(arr[1]);
        double y = Double.parseDouble(arr[2]);
        double z = Double.parseDouble(arr[3]);

        player.teleport(new Location(Bukkit.getWorld(world), x, y, z));
    }

    public void resetPlayer(Player player) {
        player.setPlayerListName(player.getName());
        clearInventory(player);

        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        player.setHealth(20);
        player.setFoodLevel(20);
        castleWars.checkDropFlag(player);
        teleport(player, "lobby-leave");
    }



    private final HashMap<Player, ItemStack[][]> inv_store = new HashMap<>();
    public void saveInventory(Player player) {
        ItemStack[][] store = new ItemStack[2][1];
        store[0] = player.getInventory().getContents();
        store[1] = player.getInventory().getArmorContents();
        this.inv_store.put(player, store);
    }

    public void restoreInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setContents(this.inv_store.get(player)[0]);
        player.getInventory().setArmorContents(this.inv_store.get(player)[1]);
        this.inv_store.remove(player);
        player.updateInventory();
    }


    public void getKit(Player player) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        if (TeamColors.getColor(player) == TeamColors.RED) {
            meta.setDisplayName("§cШлем война");
            meta.setColor(Color.fromBGR(65, 65, 255));
            meta.spigot().setUnbreakable(true);
            helmet.setItemMeta(meta);
            player.getInventory().setHelmet(helmet);


            ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
            ItemMeta chestplate_meta = chestplate.getItemMeta();
            chestplate_meta.setDisplayName("§cНагрудник война");
            chestplate_meta.spigot().setUnbreakable(true);
            chestplate.setItemMeta(chestplate_meta);
            player.getInventory().setChestplate(chestplate);


            ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
            ItemMeta leggings_meta = leggings.getItemMeta();
            leggings_meta.setDisplayName("§cШтаны война");
            leggings_meta.spigot().setUnbreakable(true);
            leggings.setItemMeta(leggings_meta);
            player.getInventory().setLeggings(leggings);

            ItemStack boots = new ItemStack(Material.IRON_BOOTS);
            ItemMeta boots_meta = boots.getItemMeta();
            boots_meta.setDisplayName("§cБотинки война");
            boots_meta.spigot().setUnbreakable(true);
            boots.setItemMeta(boots_meta);
            player.getInventory().setBoots(boots);

            ItemStack COOKED_BEEF = new ItemStack(Material.COOKED_BEEF, 16);
            player.getInventory().setItem(8, COOKED_BEEF);


            ItemStack DiamondAxe = new ItemStack(Material.DIAMOND_AXE);
            ItemMeta DiamondAxe_meta = DiamondAxe.getItemMeta();
            DiamondAxe_meta.spigot().setUnbreakable(true);
            player.getInventory().setItem(0, DiamondAxe);


            ItemStack bow = new ItemStack(Material.BOW, 1);
            player.getInventory().setItem(1, bow);

            ItemStack arrow = new ItemStack(Material.ARROW, 32);
            player.getInventory().setItem(9, arrow);


        }
        if (TeamColors.getColor(player) == TeamColors.BLUE) {
            meta.setDisplayName("§9Шлем воина");
            meta.setColor(Color.fromBGR(255, 0, 0));
            meta.spigot().setUnbreakable(true);
            helmet.setItemMeta(meta);
            player.getInventory().setHelmet(helmet);

            ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
            ItemMeta chestplate_meta = chestplate.getItemMeta();
            chestplate_meta.setDisplayName("§9Нагрудник война");
            chestplate_meta.spigot().setUnbreakable(true);
            chestplate.setItemMeta(chestplate_meta);
            player.getInventory().setChestplate(chestplate);

            ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
            ItemMeta leggings_meta = leggings.getItemMeta();
            leggings_meta.setDisplayName("§9Штаны война");
            leggings_meta.spigot().setUnbreakable(true);
            leggings.setItemMeta(leggings_meta);
            player.getInventory().setLeggings(leggings);

            ItemStack boots = new ItemStack(Material.IRON_BOOTS);
            ItemMeta boots_meta = boots.getItemMeta();
            boots_meta.setDisplayName("§9Ботинки война");
            boots_meta.spigot().setUnbreakable(true);
            boots.setItemMeta(boots_meta);
            player.getInventory().setBoots(boots);

            ItemStack COOKED_BEEF = new ItemStack(Material.COOKED_BEEF, 16);
            player.getInventory().setItem(8, COOKED_BEEF);


            ItemStack DiamondAxe = new ItemStack(Material.DIAMOND_AXE);
            ItemMeta DiamondAxe_meta = DiamondAxe.getItemMeta();
            DiamondAxe_meta.spigot().setUnbreakable(true);
            player.getInventory().setItem(0, DiamondAxe);

            ItemStack bow = new ItemStack(Material.BOW, 1);
            player.getInventory().setItem(1, bow);

            ItemStack arrow = new ItemStack(Material.ARROW, 32);
            player.getInventory().setItem(9, arrow);

        }
    }

}
