package me.bonjur.listeners;

import me.bonjur.CastleWars;
import me.bonjur.Main;
import me.bonjur.config.GameConfig;
import me.bonjur.managers.Monument;
import me.bonjur.managers.PlayerManager;
import me.bonjur.managers.ScoreboardManager;
import me.bonjur.teams.TeamColors;
import me.bonjur.teams.TeamManager;
import me.bonjur.utils.RectRegion;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.event.inventory.InventoryType.SlotType.ARMOR;

public class PlayerListener implements Listener {
    private final GameConfig CONFIG = GameConfig.IMP;
    TeamManager team = new TeamManager(TeamColors.SPECTATOR);
    TeamColors teamColors = TeamColors.SPECTATOR;
    PlayerManager playerManager = new PlayerManager();
    ScoreboardManager scoreboard = new ScoreboardManager();
    CastleWars castleWars = Main.castleWars;
    Monument monument = new Monument();
    private final RectRegion redFlagRegion = new RectRegion(CONFIG.FLAGS.RED, 1);
    private final RectRegion blueFlagRegion = new RectRegion(CONFIG.FLAGS.BLUE, 1);
    private final RectRegion monumentRegion = new RectRegion(CONFIG.MONUMENT, 3);


    @EventHandler
    public void onMovePlayer(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String nick = player.getName();
        Location PlayerLocation = player.getLocation();
        if (CONFIG.PORTAL_START.distance(PlayerLocation) < 0.9) {
            team.choiceTeam(player);
            scoreboard.update();
        }


        if (monumentRegion.contains(player.getLocation())) {
            monument.regen(player);
        }

        if (castleWars.blueTeam.isThief(player) && redFlagRegion.contains(player.getLocation())) {
            castleWars.redTeam.increasePoints();
            castleWars.blueTeam.setThief(null);
            castleWars.setFlag(TeamColors.BLUE);
            int redPoints = castleWars.redTeam.getPoints();
            int bluePoints = castleWars.blueTeam.getPoints();
            String text = "§c§l" + nick + " §f§lпринес флаг §9§lсиних§f§l! §c§l" + redPoints + "§f§l:§9§l" + bluePoints;
            Bukkit.broadcastMessage(text);
            if (castleWars.redTeam.getPoints() >= CONFIG.POINTS.FLAGS) {
                castleWars.gameFinish(TeamColors.RED);
                return;
            }
            playerManager.restoreInventory(player);
            player.teleport(GameConfig.IMP.RESPAWN.RED);
            scoreboard.update();

        }

        if (castleWars.redTeam.isThief(player) && blueFlagRegion.contains(player.getLocation())) {
            castleWars.blueTeam.increasePoints();
            castleWars.redTeam.setThief(null);
            castleWars.setFlag(TeamColors.RED);
            int redPoints = castleWars.redTeam.getPoints();
            int bluePoints = castleWars.blueTeam.getPoints();
            String text = "§9§l" + nick + " §f§lпринес флаг §c§lкрасных§f§l! §c§l" + redPoints + "§f§l:§9§l" + bluePoints;
            Bukkit.broadcastMessage(text);
            if (castleWars.blueTeam.getPoints() >= CONFIG.POINTS.FLAGS) {
                castleWars.gameFinish(TeamColors.BLUE);
                return;
            }
            playerManager.restoreInventory(player);
            player.teleport(GameConfig.IMP.RESPAWN.BLUE);
            scoreboard.update();

        }

    }


    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            if (player.getHealth() - event.getDamage() <= 0) {
                event.setCancelled(true);
                player.setHealth(20);
                player.setFoodLevel(20);
                playerManager.clearInventory(player);
                playerManager.getKit(player);
                if (TeamColors.getColor(player) == TeamColors.RED) {
                    player.teleport(GameConfig.IMP.RESPAWN.RED);
                    castleWars.redTeam.decreaseKills();
                    if (castleWars.redTeam.getKills() <= 0) {
                        Bukkit.broadcastMessage("§f§lУ §c§lкрасных §f§lзакончились жизни!");
                        castleWars.gameFinish(TeamColors.BLUE);
                    }
                }
                if (TeamColors.getColor(player) == TeamColors.BLUE) {
                    player.teleport(GameConfig.IMP.RESPAWN.BLUE);
                    castleWars.blueTeam.decreaseKills();
                    if (castleWars.blueTeam.getKills() <= 0) {
                        Bukkit.broadcastMessage("§f§lУ §9§lсиних §f§lзакончились жизни!");
                        castleWars.gameFinish(TeamColors.RED);
                    }
                }
                castleWars.checkDropFlag(player);
                scoreboard.update();

            }
        }
    }


    @EventHandler
    public void onHurt(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Player killer = (Player) event.getDamager();
        if (team.isOneTeam(player, killer)) {
            event.setCancelled(true);
        }

    }


    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (castleWars.blueTeam.isThief(player) || castleWars.redTeam.isThief(player)) {
            event.setCancelled(true);
        }

        if (event.getItemDrop().getItemStack().getType().name().equals("WOOL")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerDisconnect(PlayerQuitEvent event) {
        Bukkit.broadcastMessage("§7§l" + event.getPlayer().getName() + " §f§lпокинул бой");
        castleWars.checkDropFlag(event.getPlayer());
        event.setQuitMessage("");
    }

    @EventHandler
    public void PlayerDisconnect(PlayerJoinEvent event) {
        event.setJoinMessage("");
        playerManager.resetPlayer(event.getPlayer());
        scoreboard.update();
    }

    @EventHandler
    public void onBreakingBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location BlockLocation = event.getBlock().getLocation();

        if (CONFIG.FLAGS.RED.equals(BlockLocation)) {
            if (TeamColors.getColor(player) == TeamColors.RED) {
                player.sendMessage("§f[§cCastle§aWars§f] §f§lВы не можете украсть свой флаг!");
                event.setCancelled(true);
                return;
            }

            if (team.countRed() < 1) {
                player.sendMessage("§f[§cCastle§aWars§f] §f§lВы не можете украсть флаг пока в противоположной команде нету игроков!");
                event.setCancelled(true);
                return;
            }

            castleWars.redTeam.setThief(player);
            Bukkit.broadcastMessage("§9§l" + player.getName() + " §f§lукрал флаг §c§lкрасных§f§l!");
            player.sendMessage("§fПринесите вражеский флаг к своему флагу");
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            playerManager.saveInventory(player);
            playerManager.clearInventory(player);

            for (int i = 0; i < 36; i++) {
                player.getInventory().setItem(i, new ItemStack(Material.WOOL, 64, DyeColor.RED.getData()));
            }
        }

        if (CONFIG.FLAGS.BLUE.equals(BlockLocation)) {
            if (TeamColors.getColor(player) == TeamColors.BLUE) {
                player.sendMessage("§f[§cCastle§aWars§f] §f§lВы не можете украсть свой флаг!");
                event.setCancelled(true);
                return;
            }
            if (team.countBlue() < 1) {
                player.sendMessage("§f[§cCastle§aWars§f] §f§lВы не можете украсть флаг пока в противоположной команде нету игроков!");
                event.setCancelled(true);
                return;
            }

            castleWars.blueTeam.setThief(player);
            Bukkit.broadcastMessage("§c§l" + player.getName() + " §f§lукрал флаг §9§lсиних§f§l!");
            player.sendMessage("§fПринесите вражеский флаг к своему флагу");
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            playerManager.saveInventory(player);
            playerManager.clearInventory(player);

            for (int i = 0; i < 36; i++) {
                player.getInventory().setItem(i, new ItemStack(Material.WOOL, 64, DyeColor.BLUE.getData()));
            }
        }

        if (CONFIG.MONUMENT.equals(BlockLocation)) {
            if (event.getBlock().getType() == Material.WOOL) {
                if (event.getBlock().getData() == 14) {
                    Bukkit.broadcastMessage("§f§lКоманда §c§lкрасных §f§lпотеряла контроль над Центральным монументом!");
                } else if (event.getBlock().getData() == 11) {
                    Bukkit.broadcastMessage("§f§lКоманда §9§lсиних §f§lпотеряла контроль над Центральным монументом!");
                }

            }
        }
    }


    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location BlockLocation = event.getBlock().getLocation();

        if (castleWars.redTeam.isThief(player) || castleWars.blueTeam.isThief(player)) {
            event.setCancelled(true);
        }

        if (CONFIG.MONUMENT.equals(BlockLocation)) {
            if (event.getBlock().getType() == Material.WOOL) {
                if (!castleWars.redTeam.isThief(player) && !castleWars.blueTeam.isThief(player)) {
                    if (event.getBlock().getData() == 14) {
                        Bukkit.broadcastMessage("§f§lКоманда §c§lкрасных §f§lзахватила Центральный монумент!");
                    } else if (event.getBlock().getData() == 11) {
                        Bukkit.broadcastMessage("§f§lКоманда §9§lсиних §f§lзахватила Центральный монумент!");
                    }
                }

            }
        }
        if (CONFIG.FLAGS.BLUE.equals(BlockLocation) || CONFIG.FLAGS.RED.equals(BlockLocation)) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void InventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (castleWars.redTeam.isThief(player) || castleWars.blueTeam.isThief(player)) {
            event.setCancelled(true);
        }
        // slot id 39 - helmet
        if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getInventory())) {
            if (event.getSlot() == 39) {
                if (TeamColors.getColor(player) == TeamColors.RED) {
                    event.setCancelled(true);
                    ItemStack wool = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
                    player.setItemOnCursor(wool);
                }
                if (TeamColors.getColor(player) == TeamColors.BLUE) {
                    event.setCancelled(true);
                    ItemStack wool = new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getData());
                    player.setItemOnCursor(wool);
                }
            } else if (event.getSlotType() == ARMOR) {
                event.setCancelled(true);
            }

        }
    }

}
