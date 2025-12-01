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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Lazy holder for spawn world. Lazy because {@link Bukkit#getWorlds()} is empty at outer class init.
 *
 * @author VidTu
 */
// HACK: This is dependent on class-loading order.
@NullMarked
final class SpawnWorldHolder {
    /**
     * The spawn world.
     */
    static final World SPAWN_WORLD = Bukkit.getWorlds().getFirst();

    /**
     * The spawn location.
     */
    static final Location SPAWN = new Location(SPAWN_WORLD, 0.5d, 65.5d, 0.5d, 0.0f, 0.0f);

    /**
     * An instance of this class cannot be created.
     *
     * @throws AssertionError Always
     * @deprecated Always throws
     */
    @Deprecated(forRemoval = true)
    @Contract(value = "-> fail", pure = true)
    private SpawnWorldHolder() {
        throw new AssertionError("Farewell: No instances.");
    }
}
