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

import org.jspecify.annotations.NullMarked;
import ru.brominemc.farewell.Farewell;

/**
 * Main Farewell module.
 *
 * @see Farewell
 */
@NullMarked
module Farewell.main {
    // Annotations.
    requires static org.jspecify;
    requires static org.jetbrains.annotations;
    requires static com.google.errorprone.annotations;

    // Paper.
    requires org.bukkit;

    // Libraries provided by Paper.
    requires com.google.common;
    requires net.kyori.adventure;
    //noinspection Java9RedundantRequiresStatement // <- For Adventure.
    requires net.kyori.examination.api;
    requires org.apache.logging.log4j;
}
