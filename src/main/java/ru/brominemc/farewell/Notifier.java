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
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CompileTimeConstant;
import com.google.errorprone.annotations.DoNotCall;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

/**
 * Class that notifies about sunsetting on join.
 *
 * @author VidTu
 */
@NullMarked
final class Notifier implements Listener {
    /**
     * An immutable list of languages that will be shown the Russian text.
     */
    @Unmodifiable
    private static final ImmutableSet<String> RUSSIAN_LANGUAGES = ImmutableSet.of("ru", "ru-ru", "ru_ru", "rpr");

    /**
     * Book notifying about sunsetting the server in English.
     */
    @CompileTimeConstant
    private static final Book BOOK_EN = Book.book(Component.text("Sunsetting the server."), Component.text("BromineMC"),
            Component.empty()
                    .append(Component.text("> Sunsetting the server.", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("We've decided to close the server. Thanks to everyone who was playing/supporting!", NamedTextColor.BLACK))
                    .appendSpace()
                    .append(Component.text("❤", NamedTextColor.DARK_RED))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Server will stop working at January 1st, 2026.", NamedTextColor.BLACK))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("[More Info]", NamedTextColor.DARK_BLUE)
                            .clickEvent(ClickEvent.changePage(2))
                            .hoverEvent(Component.text("Click to get more info.")))
                    .appendSpace()
                    .append(Component.text("[Discord]", NamedTextColor.DARK_PURPLE)
                            .clickEvent(ClickEvent.openUrl("https://discord.gg/w28MHQWMJM"))
                            .hoverEvent(Component.text("Click to join the Discord server. (in Russian)"))),
            Component.empty()
                    .append(Component.text("> More Info", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("[What's now?]", NamedTextColor.DARK_PURPLE)
                            .clickEvent(ClickEvent.changePage(3))
                            .hoverEvent(Component.text("Click to get more info.")))
                    .appendNewline()
                    .append(Component.text("[What's next?]", NamedTextColor.GOLD)
                            .clickEvent(ClickEvent.changePage(4))
                            .hoverEvent(Component.text("Click to get more info.")))
                    .appendNewline()
                    .append(Component.text("[Why?]", NamedTextColor.DARK_BLUE)
                            .clickEvent(ClickEvent.changePage(5))
                            .hoverEvent(Component.text("Click to get more info.")))
                    .appendNewline()
                    .append(Component.text("[Thanks]", NamedTextColor.DARK_GREEN)
                            .clickEvent(ClickEvent.changePage(6))
                            .hoverEvent(Component.text("Click to get more info.")))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("<- Back", NamedTextColor.DARK_GRAY)
                            .clickEvent(ClickEvent.changePage(2))
                            .hoverEvent(Component.text("Click to go back."))),
            Component.empty()
                    .append(Component.text("> What's now?", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("You can't buy ranks, get an AC ban. Disabled the server protection, removed all bans/mutes, rules no longer apply.", NamedTextColor.BLACK))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("<- Back", NamedTextColor.DARK_GRAY)
                            .clickEvent(ClickEvent.changePage(2))
                            .hoverEvent(Component.text("Click to go back."))),
            Component.empty()
                    .append(Component.text("> What's next?", NamedTextColor.GOLD, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("In some time, the anti-cheat plugin will be disabled.", NamedTextColor.BLACK))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("<- Back", NamedTextColor.DARK_GRAY)
                            .clickEvent(ClickEvent.changePage(2))
                            .hoverEvent(Component.text("Click to go back."))),
            Component.empty()
                    .append(Component.text("> Why?", NamedTextColor.DARK_BLUE, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Server is continuously stagnating for the last two years.", NamedTextColor.BLACK))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("<- Back", NamedTextColor.DARK_GRAY)
                            .clickEvent(ClickEvent.changePage(2))
                            .hoverEvent(Component.text("Click to go back."))),
            Component.empty()
                    .append(Component.text("> Thanks", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Thanks to ", NamedTextColor.BLACK))
                    .append(Component.text("these people", NamedTextColor.DARK_PURPLE, TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.copyToClipboard("3fusii, AntonioYes, Bl_ite, CherryBloom, Dexp114, doublekind (aka libffi), Flanker_E, Holodec, Justiks, Kasspov, kendo, korobtwww, kraken_vagen, Meowlowww (aka Wasurette), miwwcy (aka miwcy), PhiberOptik23, putrin, smoANTpe4enka_, Tervinter, testygem6354, UnknownDJ, weepl_lpv, WhyNevermore (aka ImWhy), _xFunny_"))
                            .hoverEvent(Component.text("3fusii, AntonioYes, Bl_ite, CherryBloom, Dexp114, doublekind (aka libffi), Flanker_E, Holodec, Justiks, Kasspov, kendo, korobtwww, kraken_vagen, Meowlowww (aka Wasurette), miwwcy (aka miwcy), PhiberOptik23, putrin, smoANTpe4enka_, Tervinter, testygem6354, UnknownDJ, weepl_lpv, WhyNevermore (aka ImWhy), _xFunny_")))
                    .append(Component.text(" for their contributions. ", NamedTextColor.BLACK))
                    .append(Component.text("❤", NamedTextColor.RED))
                    .appendNewline()
                    .append(Component.text("Thanks to ", NamedTextColor.BLACK))
                    .append(Component.text("the other staff", NamedTextColor.DARK_GREEN, TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.copyToClipboard("AloneAtHell, Aurora_CPvP, Aylanx, deerkaa, Dima_Slonik, facelesw, Fllarium, FX_6300, iwtdie, KavanExport, kokopro, Kotik_Obormotik, Kse0nN_, LonelyGirl_, mnsvn, MorphMorph, nikitamono, qazdr0, ronyak3kk, Rsuy, Savmix000, SayAboutHurtMe, Shelper_GG, terpusha, unknownpng, YTA4KA, Zloebuchko, ZV_6446"))
                            .hoverEvent(Component.text("AloneAtHell, Aurora_CPvP, Aylanx, deerkaa, Dima_Slonik, facelesw, Fllarium, FX_6300, iwtdie, KavanExport, kokopro, Kotik_Obormotik, Kse0nN_, LonelyGirl_, mnsvn, MorphMorph, nikitamono, qazdr0, ronyak3kk, Rsuy, Savmix000, SayAboutHurtMe, Shelper_GG, terpusha, unknownpng, YTA4KA, Zloebuchko, ZV_6446")))
                    .append(Component.text(" for their work. ", NamedTextColor.BLACK))
                    .append(Component.text("🔥", NamedTextColor.GOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Thanks to everyone who was supporting us and was playing.", NamedTextColor.BLACK))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("<- Back", NamedTextColor.DARK_GRAY)
                            .clickEvent(ClickEvent.changePage(2))
                            .hoverEvent(Component.text("Click to go back."))));

    /**
     * Book notifying about sunsetting the server in Russian.
     */
    @CompileTimeConstant
    private static final Book BOOK_RU = Book.book(Component.text("Мы закрываемся."), Component.text("BromineMC"),
            Component.empty()
                    .append(Component.text("> Мы закрываемся.", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Мы приняли решение о закрытии сервера. Спасибо всем, кто играл/поддерживал! ", NamedTextColor.BLACK))
                    .appendSpace()
                    .append(Component.text("❤", NamedTextColor.DARK_RED))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Сервер прекратит свою работу 1 января 2026г.", NamedTextColor.BLACK))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("[Подробнее]", NamedTextColor.DARK_BLUE)
                            .clickEvent(ClickEvent.changePage(2))
                            .hoverEvent(Component.text("Нажмите, чтобы узнать больше.")))
                    .appendSpace()
                    .append(Component.text("[Discord]", NamedTextColor.DARK_PURPLE)
                            .clickEvent(ClickEvent.openUrl("https://discord.gg/w28MHQWMJM"))
                            .hoverEvent(Component.text("Нажмите, чтобы зайти в Discord."))),
            Component.empty()
                    .append(Component.text("> Подробнее", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("[Что уже произошло?]", NamedTextColor.DARK_PURPLE)
                            .clickEvent(ClickEvent.changePage(3))
                            .hoverEvent(Component.text("Нажмите, чтобы узнать больше.")))
                    .appendNewline()
                    .append(Component.text("[Что произойдёт позже?]", NamedTextColor.GOLD)
                            .clickEvent(ClickEvent.changePage(4))
                            .hoverEvent(Component.text("Нажмите, чтобы узнать больше.")))
                    .appendNewline()
                    .append(Component.text("[Почему?]", NamedTextColor.DARK_BLUE)
                            .clickEvent(ClickEvent.changePage(5))
                            .hoverEvent(Component.text("Нажмите, чтобы узнать больше.")))
                    .appendNewline()
                    .append(Component.text("[Благодарности]", NamedTextColor.DARK_GREEN)
                            .clickEvent(ClickEvent.changePage(6))
                            .hoverEvent(Component.text("Нажмите, чтобы узнать больше.")))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("<- Назад", NamedTextColor.DARK_GRAY)
                            .clickEvent(ClickEvent.changePage(1))
                            .hoverEvent(Component.text("Нажмите, чтобы вернуться назад."))),
            Component.empty()
                    .append(Component.text("> Какие изменения произошли уже сейчас?", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Вы не можете купить донат, получить бан от АЧ. Отключена защита сервера, сняты все баны и муты, правила теперь не действуют.", NamedTextColor.BLACK))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("<- Назад", NamedTextColor.DARK_GRAY)
                            .clickEvent(ClickEvent.changePage(2))
                            .hoverEvent(Component.text("Нажмите, чтобы вернуться назад."))),
            Component.empty()
                    .append(Component.text("> Какие изменения произойдут в будущем?", NamedTextColor.GOLD, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Через время будет полностью отключён анти-чит.", NamedTextColor.BLACK))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("<- Назад", NamedTextColor.DARK_GRAY)
                            .clickEvent(ClickEvent.changePage(2))
                            .hoverEvent(Component.text("Нажмите, чтобы вернуться назад."))),
            Component.empty()
                    .append(Component.text("> Почему?", NamedTextColor.DARK_BLUE, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Сервер стагнирует уже как два года.", NamedTextColor.BLACK))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("<- Назад", NamedTextColor.DARK_GRAY)
                            .clickEvent(ClickEvent.changePage(2))
                            .hoverEvent(Component.text("Нажмите, чтобы вернуться назад."))),
            Component.empty()
                    .append(Component.text("> Благодарности", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Спасибо ", NamedTextColor.BLACK))
                    .append(Component.text("этим людям", NamedTextColor.DARK_PURPLE, TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.copyToClipboard("3fusii, AntonioYes, Bl_ite, CherryBloom, Dexp114, doublekind (aka libffi), Flanker_E, Holodec, Justiks, Kasspov, kendo, korobtwww, kraken_vagen, Meowlowww (aka Wasurette), miwwcy (aka miwcy), PhiberOptik23, putrin, smoANTpe4enka_, Tervinter, testygem6354, UnknownDJ, weepl_lpv, WhyNevermore (aka ImWhy), _xFunny_"))
                            .hoverEvent(Component.text("3fusii, AntonioYes, Bl_ite, CherryBloom, Dexp114, doublekind (aka libffi), Flanker_E, Holodec, Justiks, Kasspov, kendo, korobtwww, kraken_vagen, Meowlowww (aka Wasurette), miwwcy (aka miwcy), PhiberOptik23, putrin, smoANTpe4enka_, Tervinter, testygem6354, UnknownDJ, weepl_lpv, WhyNevermore (aka ImWhy), _xFunny_")))
                    .append(Component.text(" за внесение вклада в развитие сервера. ", NamedTextColor.BLACK))
                    .append(Component.text("❤", NamedTextColor.RED))
                    .appendNewline()
                    .append(Component.text("Спасибо ", NamedTextColor.BLACK))
                    .append(Component.text("остальному стаффу", NamedTextColor.DARK_GREEN, TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.copyToClipboard("AloneAtHell, Aurora_CPvP, Aylanx, deerkaa, Dima_Slonik, facelesw, Fllarium, FX_6300, iwtdie, KavanExport, kokopro, Kotik_Obormotik, Kse0nN_, LonelyGirl_, mnsvn, MorphMorph, nikitamono, qazdr0, ronyak3kk, Rsuy, Savmix000, SayAboutHurtMe, Shelper_GG, terpusha, unknownpng, YTA4KA, Zloebuchko, ZV_6446"))
                            .hoverEvent(Component.text("AloneAtHell, Aurora_CPvP, Aylanx, deerkaa, Dima_Slonik, facelesw, Fllarium, FX_6300, iwtdie, KavanExport, kokopro, Kotik_Obormotik, Kse0nN_, LonelyGirl_, mnsvn, MorphMorph, nikitamono, qazdr0, ronyak3kk, Rsuy, Savmix000, SayAboutHurtMe, Shelper_GG, terpusha, unknownpng, YTA4KA, Zloebuchko, ZV_6446")))
                    .append(Component.text(" за работу. ", NamedTextColor.BLACK))
                    .append(Component.text("🔥", NamedTextColor.GOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Спасибо всем кто поддерживал и просто играл.", NamedTextColor.BLACK))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("<- Назад", NamedTextColor.DARK_GRAY)
                            .clickEvent(ClickEvent.changePage(2))
                            .hoverEvent(Component.text("Нажмите, чтобы вернуться назад."))));

    /**
     * Book item notifying about sunsetting the server in English.
     */
    private static final ItemStack BOOK_ITEM_EN = new ItemStack(Material.WRITTEN_BOOK);
    static {
        BOOK_ITEM_EN.editMeta(BookMeta.class, meta -> {
            meta.title(BOOK_EN.title());
            meta.author(BOOK_EN.author());
            meta.addPages(BOOK_EN.pages().toArray(Component[]::new));
        });
    }

    /**
     * Book item notifying about sunsetting the server in Russian.
     */
    private static final ItemStack BOOK_ITEM_RU = new ItemStack(Material.WRITTEN_BOOK);
    static {
        BOOK_ITEM_RU.editMeta(BookMeta.class, meta -> {
            meta.title(BOOK_RU.title());
            meta.author(BOOK_RU.author());
            meta.addPages(BOOK_RU.pages().toArray(Component[]::new));
        });
    }

    /**
     * Alleged spawn location. World is set in {@link #init()}, it is null on init.
     */
    private static final Location ALLEGED_SPAWN = new Location(null, 0.5d, 65.5d, 0.5d, 0.0f, 0.0f);

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
    Notifier(Farewell plugin) {
        // Assign.
        this.plugin = Preconditions.checkNotNull(plugin, "plugin");
    }

    /**
     * Initializes the handler.
     */
    @Contract(mutates = "this")
    void init() {
        // Set the world.
        ALLEGED_SPAWN.setWorld(SpawnWorldHolder.SPAWN_WORLD);

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

        // Unset the world.
        ALLEGED_SPAWN.setWorld(null);
    }

    /**
     * Shows the book on join.
     *
     * @param event Event to handle
     * @apiNote Do not call, called by Paper, internal use only
     */
    @DoNotCall("Called by Paper")
    @ApiStatus.Internal
    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        // Schedule in one second to avoid being closing by the main plugin.
        Player player = event.getPlayer(); // Implicit NPE for 'event'
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> openBook(player), Ticks.TICKS_PER_SECOND);
    }

    /**
     * Adds the book on spawn-teleport.
     *
     * @param event Event to handle
     * @apiNote Do not call, called by Paper, internal use only
     */
    @DoNotCall("Called by Paper")
    @ApiStatus.Internal
    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        // Check if teleported to spawn.
        if (!ALLEGED_SPAWN.equals(event.getTo())) return; // Implicit NPE for 'event'

        // Schedule a second later.
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            // Check if no longer at spawn.
            Player player = event.getPlayer();
            if (!SpawnWorldHolder.SPAWN_WORLD.equals(player.getWorld())) return;

            // Clear old books.
            PlayerInventory inv = player.getInventory();
            inv.remove(Material.WRITTEN_BOOK);

            // Select and give the book.
            String language = player.locale().toString().toLowerCase(Locale.ROOT);
            ItemStack book = (RUSSIAN_LANGUAGES.contains(language) ? BOOK_ITEM_RU : BOOK_ITEM_EN);
            inv.addItem(book);
        }, Ticks.TICKS_PER_SECOND);
    }

    /**
     * Allows to read the book even if disallowed by the main plugin.
     *
     * @param event Event to handle
     * @apiNote Do not call, called by Paper, internal use only
     */
    @DoNotCall("Called by Paper")
    @ApiStatus.Internal
    @EventHandler
    public void onUseBook(PlayerInteractEvent event) {
        // Check if at spawn.
        Player player = event.getPlayer(); // Implicit NPE for 'event'
        if (!player.getWorld().equals(SpawnWorldHolder.SPAWN_WORLD)) return;

        // Check the item.
        ItemStack item = event.getItem();
        if (!BOOK_ITEM_EN.isSimilar(item) || !BOOK_ITEM_RU.isSimilar(item)) return;

        // Open the book.
        event.setCancelled(true);
        openBook(player);
    }

    /**
     * Opens the book.
     *
     * @param player Target player
     */
    private static void openBook(Player player) {
        // NoboKik ahh language selection.
        String language = player.locale().toString().toLowerCase(Locale.ROOT); // Implicit NPE for 'player'
        ItemStack book = (RUSSIAN_LANGUAGES.contains(language) ? BOOK_ITEM_RU : BOOK_ITEM_EN);
        player.openBook(book);
    }
}
