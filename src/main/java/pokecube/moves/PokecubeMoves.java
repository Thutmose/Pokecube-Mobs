package pokecube.moves;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import pokecube.mobs.PokecubeMobs;

@Mod(modid = PokecubeMoves.MODID, name = "Pokecube Movess", version = PokecubeMoves.VERSION, dependencies = "required-after:pokecube", acceptableRemoteVersions = "*", acceptedMinecraftVersions = PokecubeMoves.MCVERSIONS)
public class PokecubeMoves
{
    public static final String MODID      = "pokecube_moves";
    public static final String VERSION    = "@VERSION@";
    public final static String MCVERSIONS = "*";

    public PokecubeMoves()
    {
        // TODO Auto-generated constructor stub
    }

    private void doMetastuff()
    {
        ModMetadata meta = FMLCommonHandler.instance().findContainerFor(this).getMetadata();
        meta.parent = PokecubeMobs.MODID;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt)
    {
        doMetastuff();
    }

}
