package com.decduck3.tradecraft.commands;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.security.AccountLinkManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        String code = strings[0].toUpperCase();
        if(!TradeCraft.accountLinkManager().linkPlayer(code, player.getUniqueId())){
            player.sendMessage("There was a problem with linking your code. Double check your code and try again.");
            return false;
        }
        player.sendMessage("Linked this account with provided code! Finish the link on your browser.");
        return true;
    }
}
