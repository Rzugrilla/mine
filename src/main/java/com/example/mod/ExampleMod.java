package rzuf.fastmine;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
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
        event.newSpeed *= szybkoscKopania; // Zmiana szybkości kopania
    }
    
    // Komenda /fm do ustawiania szybkości kopania
    public class CommandSzybkoscKopania extends CommandBase {
        
        @Override
        public String getCommandName() {
            return "fm";
        }
        
        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/fm <szybkosc>";
        }
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1) {
                sender.addChatMessage(new ChatComponentText("Zła liczba argumentów! Użycie: " + getCommandUsage(sender)));
                return;
            }
            
            try {
                float nowaSzybkosc = parseFloat(args[0], 0.0f, 100.0f); // Parsowanie argumentu do float
                
                // Sprawdzenie, czy szybkość mieści się w zakresie 0% - 100%
                if (nowaSzybkosc < 0.0f || nowaSzybkosc > 100.0f) {
                    sender.addChatMessage(new ChatComponentText("Szybkość kopania musi być między 0% a 100%!"));
                    return;
                }
                
                szybkoscKopania = nowaSzybkosc / 100.0f; // Ustawienie nowej szybkości kopania
                
                // Informowanie gracza o zmianie
                sender.addChatMessage(new ChatComponentText("Ustawiono szybkość kopania na " + nowaSzybkosc + "%"));
            } catch (NumberInvalidException e) {
                sender.addChatMessage(new ChatComponentText("Niepoprawna wartość! Użycie: " + getCommandUsage(sender)));
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
