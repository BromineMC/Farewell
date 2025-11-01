/*
 * Farewell is an addon PaperMC plugin for BromineMC designed to sunset the server.
 *
 * Copyright (C) 2025 BromineMC
 * Copyright (C) 2025 VidTu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package ru.brominemc.farewell;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CompileTimeConstant;
import com.google.errorprone.annotations.DoNotCall;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * Class that provies extra spawn goodies.
 *
 * @author VidTu
 */
@NullMarked
final class SpawnFeaturesPlus implements Listener {
    /**
     * Sound for equipping elytra.
     */
    @CompileTimeConstant
    private static final Sound ELYTRA_SOUND = Sound.sound(org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, Sound.Source.MASTER, Float.MAX_VALUE, 1.0f);

    /**
     * Sound for launching.
     */
    @CompileTimeConstant
    private static final Sound LAUNCH_SOUND = Sound.sound(org.bukkit.Sound.ENTITY_EVOKER_PREPARE_WOLOLO, Sound.Source.MASTER, Float.MAX_VALUE, 1.0f);

    /**
     * Elytra that will never break and can't be taken off.
     */
    private static final ItemStack ELYTRA = new ItemStack(Material.ELYTRA);
    static {
        ELYTRA.editMeta(meta -> {
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, /*ignoreLevelRestriction=*/true);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
        });
    }

    /**
     * One firework.
     */
    private static final ItemStack FIREWORK = new ItemStack(Material.FIREWORK_ROCKET);

    /**
     * The random number generator.
     */
    private static final RandomGenerator RNG = new Random();

    /**
     * Plugin instance.
     */
    private final Farewell plugin;

    /**
     * Creates a new notifier.
     *
     * @param plugin Plugin instance
     */
    @Contract(pure = true)
    SpawnFeaturesPlus(Farewell plugin) {
        // Assign.
        this.plugin = Preconditions.checkNotNull(plugin, "plugin");
    }

    /**
     * Initializes the handler.
     */
    @Contract(mutates = "this")
    void init() {
        // Initialize the handler.
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    /**
     * Closes the handler.
     */
    @Contract(mutates = "this")
    void close() {
        // Close the handler.
        HandlerList.unregisterAll(this);
    }

    /**
     * Gives the elytra when clicking on a comparator.
     *
     * @param event Event to handle
     * @apiNote Do not call, called by Paper, internal use only
     */
    @ApiStatus.Internal
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Check if pressing the comparator on a brick at spawn.
        Block block = event.getClickedBlock();
        if ((block == null) || !SpawnWorldHolder.SPAWN_WORLD.equals(block.getWorld()) ||
                (block.getType() != Material.COMPARATOR) || (block.getRelative(BlockFace.DOWN).getType() != Material.BRICKS)) return;

        // Gives the elytra and fireworks at spawn.
        event.setCancelled(true);
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        inv.setChestplate(ELYTRA);
        inv.addItem(FIREWORK);
        player.playSound(ELYTRA_SOUND);
    }

    /**
     * Allows to fire the firework even if disallowed.
     *
     * @param event Event to handle
     * @apiNote Do not call, called by Paper, internal use only
     */
    @DoNotCall("Called by Paper")
    @ApiStatus.Internal
    @EventHandler
    public void onUseFirework(PlayerInteractEvent event) {
        // Check if at spawn and gliding.
        Player player = event.getPlayer(); // Implicit NPE for 'event'
        if (!player.getWorld().equals(SpawnWorldHolder.SPAWN_WORLD) || !player.isGliding()) return;

        // Check the item. (also except slot 4, which is the gadget one)
        ItemStack item = event.getItem();
        if ((item == null) || (item.getType() != Material.FIREWORK_ROCKET) ||
                (player.getInventory().getHeldItemSlot() == 4)) return;

        // Allow boosting.
        event.setCancelled(true);
        player.fireworkBoost(item);
    }

    /**
     * Launches the player on a flying campfire if they try to ignite it via the bug.
     *
     * @param event Event to handle
     * @apiNote Do not call, called by Paper, internal use only
     */
    @DoNotCall("Called by Paper")
    @ApiStatus.Internal
    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        // Check if at spawn.
        Projectile entity = event.getEntity(); // Implicit NPE for 'event'
        if (!entity.getWorld().equals(SpawnWorldHolder.SPAWN_WORLD)) return;

        // Cancel any stuff.
        event.setCancelled(true);

        // Check if on fire and hit the campfire.
        if (entity.getFireTicks() <= 0) return;
        Block block = event.getHitBlock();
        if ((block == null) || (block.getType() != Material.CAMPFIRE)) return;

        // Extract the shooter.
        if (!(entity.getShooter() instanceof Player player) || !player.isConnected() ||
                !player.getWorld().equals(SpawnWorldHolder.SPAWN_WORLD)) return;

        // Launch.
        FallingBlock fall = SpawnWorldHolder.SPAWN_WORLD.spawn(block.getLocation().toCenterLocation().add(0.0d, 1.0d, 0.0d), FallingBlock.class, b -> {
            b.setBlockData(block.getBlockData());
            b.setDropItem(false);
            b.setCancelDrop(true);
            b.setVelocity(new Vector(
                    RNG.nextDouble(-1.0d, 1.0d),
                    RNG.nextDouble(0.5d, 2.0d),
                    RNG.nextDouble(-1.0d, 1.0d)
            ));
        });
        if (!fall.isValid() || fall.isDead() || !player.isConnected() || player.isDead() ||
                !player.isValid() || !player.getWorld().equals(SpawnWorldHolder.SPAWN_WORLD)) {
            fall.remove();
            return;
        }
        fall.addPassenger(player);
        player.playSound(LAUNCH_SOUND);
        player.setFlying(true);
    }
}
