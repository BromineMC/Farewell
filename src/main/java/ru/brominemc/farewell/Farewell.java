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

import com.google.errorprone.annotations.CompileTimeConstant;
import com.google.errorprone.annotations.DoNotCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.Command;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;

/**
 * Main Farewell class.
 *
 * @author VidTu
 */
@NullMarked
public final class Farewell extends JavaPlugin {
    /**
     * The amount of nanoseconds inside a millisecond.
     * <p>
     * Equals to {@code 1_000_000} (1 million).
     */
    @CompileTimeConstant
    private static final long NANOS_IN_MS = 1_000_000L;

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LogManager.getLogger("Farewell");

    /**
     * Notifier handler.
     */
    private final Notifier notifier = new Notifier(this);

    /**
     * Additional spawn features.
     */
    private final SpawnFeaturesPlus spawnFeaturesPlus = new SpawnFeaturesPlus(this);

    /**
     * Creates a new plugin.
     *
     * @apiNote Do not call, called by Paper, internal use only
     */
    @ApiStatus.Internal
    public Farewell() {
        // Empty.
    }

    /**
     * Initializes the plugin.
     *
     * @apiNote Do not call, called by Paper, internal use only
     */
    @DoNotCall("Called by Paper")
    @ApiStatus.Internal
    @Override
    public void onEnable() {
        // Wrap.
        try {
            // Log.
            long start = System.nanoTime();
            LOGGER.info("Initializing the plugin...");

            // Initialize.
            this.notifier.init();
            this.spawnFeaturesPlus.init();

            // Log.
            LOGGER.info("Initialized the plugin. Hi! ({} ms)", Math.ceilDiv(System.nanoTime() - start, NANOS_IN_MS));
        } catch (Throwable t) {
            // Log, shutdown, rethrow.
            LOGGER.error("Unable to initialize the plugin.", t); // Duplicate logging is intentional in plugin lifecycle.
            this.getServer().shutdown();
            throw new RuntimeException("LowTier: Unable to initialize the plugin.", t);
        }
    }

    /**
     * Closes the plugin.
     *
     * @apiNote Do not call, called by Paper, internal use only
     */
    @DoNotCall("Called by Paper")
    @ApiStatus.Internal
    @Override
    public void onDisable() {
        // Wrap.
        try {
            // Log.
            long start = System.nanoTime();
            LOGGER.info("Closing the plugin...");

            // Close.
            this.spawnFeaturesPlus.close();
            this.notifier.close();

            // Clean-up any possible leftovers.
            Collection<Command> commands = this.getServer().getCommandMap().getKnownCommands().values();
            commands.removeIf(command -> (command.getClass().getModule() == this.getClass().getModule()) ||
                    command.getClass().getPackageName().startsWith(this.getClass().getPackageName()));
            HandlerList.unregisterAll(this);

            // Log.
            LOGGER.info("Closed the plugin. Bye! ({} ms)", Math.ceilDiv(System.nanoTime() - start, NANOS_IN_MS));
        } catch (Throwable t) {
            // Log, shutdown, rethrow.
            LOGGER.error("Unable to close the plugin.", t); // Duplicate logging is intentional in plugin lifecycle.
            this.getServer().shutdown();
            throw new RuntimeException("LowTier: Unable to close the plugin.", t);
        }
    }

    @Contract(pure = true)
    @Override
    public String toString() {
        return "Farewell{" +
                "notifier=" + this.notifier +
                ", spawnFeaturesPlus=" + this.spawnFeaturesPlus +
                '}';
    }
}
