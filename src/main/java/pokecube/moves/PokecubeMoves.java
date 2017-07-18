package pokecube.moves;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import pokecube.core.blocks.healtable.ContainerHealTable;
import pokecube.core.blocks.healtable.TileHealTable;
import pokecube.core.items.pokecubes.EntityPokecubeBase;
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

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ResourceLocation sound = new ResourceLocation(MODID + ":pokecube_caught");
        GameRegistry.register(EntityPokecubeBase.POKECUBESOUND = new SoundEvent(sound).setRegistryName(sound));
        sound = new ResourceLocation(MODID + ":pokecenter");
        GameRegistry.register(ContainerHealTable.HEAL_SOUND = new SoundEvent(sound).setRegistryName(sound));
        sound = new ResourceLocation(MODID + ":pokecenterloop");
        GameRegistry.register(TileHealTable.MUSICLOOP = new SoundEvent(sound).setRegistryName(sound));
    }

}
