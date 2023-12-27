package com.decduck3.tradecraft.commands;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.security.AccountLinkManager;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AuthorizedWebCommand extends @NotNull Command {
    public AuthorizedWebCommand() {
        super("authorize");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player player)){
            TradeCraft.logger().severe("Only run authorize from a player account!");
            return false;
        }

        if(strings.length < 1){
            String configExternal = TradeCraft.config().getString("network.externalUrl");
            if(configExternal != null){
                try {
                    URL providedExternalURL = new URL(configExternal);
                    AccountLinkManager.PendingAccountLink link = TradeCraft.accountLinkManager().generate();
                    URI externalUri = providedExternalURL.toURI().resolve("/link#"+link.getCode());
                    TradeCraft.accountLinkManager().linkPlayer(link.getCode(), player.getUniqueId());

                    player.sendMessage(Component.text("Open this link to sign into the web UI: ").append(Component.text("["+externalUri.getHost()+"...]").clickEvent(ClickEvent.openUrl(externalUri.toURL())).color(TextColor.color(0, 255, 255))));
                    return true;
                } catch (MalformedURLException | URISyntaxException e) {
                    TradeCraft.logger().severe(e.toString());
                }
            }

            // If we haven't returned by now, they don't have enough arguments
            player.sendMessage("Please provide a link code (like this: /authorize [code]).");
            return false;
        }

        String code = strings[0].toUpperCase();
        if(!TradeCraft.accountLinkManager().linkPlayer(code, player.getUniqueId())){
            player.sendMessage("There was a problem with linking your code. Double check your code and try again.");
            return false;
        }
        player.sendMessage("Linked this account with provided code! Finish the link on your browser.");
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return new ArrayList<>();
    }
}
