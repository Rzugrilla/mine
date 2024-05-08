package com.twoja_firma.nazwa_moda;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "szybkosc_kopania_mod", version = "1.0")
public class SzybkoscKopaniaMod {

    private static float szybkoscKopania = 1.0f; // Domyślna szybkość kopania

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this); // Rejestracja modułu w zdarzeniach gry
    }

    // Obsługa komendy /fm
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSzybkoscKopania());
    }

    // Event nasłuchujący na zmianę szybkości kopania
    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        event.setNewSpeed(event.getNewSpeed() * szybkoscKopania); // Zmiana szybkości kopania
    }

    // Komenda /fm do ustawiania szybkości kopania
    public class CommandSzybkoscKopania extends CommandBase {

        @Override
        public String getName() {
            return "fm";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "/fm <szybkosc>";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length != 1) {
                sender.sendMessage(new TextComponentString("Zła liczba argumentów! Użycie: " + getUsage(sender)));
                return;
            }

            try {
                float nowaSzybkosc = parseFloat(args[0], 0.0f, 100.0f); // Parsowanie argumentu do float

                // Sprawdzenie, czy szybkość mieści się w zakresie 0% - 100%
                if (nowaSzybkosc < 0.0f || nowaSzybkosc > 100.0f) {
                    sender.sendMessage(new TextComponentString("Szybkość kopania musi być między 0% a 100%!"));
                    return;
                }

                szybkoscKopania = nowaSzybkosc / 100.0f; // Ustawienie nowej szybkości kopania

                // Informowanie gracza o zmianie
                sender.sendMessage(new TextComponentString("Ustawiono szybkość kopania na " + nowaSzybkosc + "%"));
            } catch (NumberInvalidException e) {
                sender.sendMessage(new TextComponentString("Niepoprawna wartość! Użycie: " + getUsage(sender)));
            }
        }

        // Parsowanie stringa do float z zakresem
        private float parseFloat(String str, float min, float max) throws NumberInvalidException {
            try {
                float val = Float.parseFloat(str);
                if (val < min || val > max)
                    throw new NumberInvalidException("Wartość poza zakresem", val);
                return val;
            } catch (NumberFormatException e) {
                throw new NumberInvalidException("Niepoprawna wartość", str);
            }
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 2; // Wymagany poziom uprawnień (OP)
        }
    }
}
