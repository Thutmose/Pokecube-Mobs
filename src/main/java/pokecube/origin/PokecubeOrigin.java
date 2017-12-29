package pokecube.origin;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pokecube.core.PokecubeCore;
import pokecube.core.client.render.entity.RenderPokemobs;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.events.PostPostInit;
import pokecube.mobs.PokecubeMobs;
import pokecube.mobs.Reference;
import pokecube.origin.models.ModelPichu;
import pokecube.origin.models.ModelPikachu;
import thut.core.common.config.ConfigBase;
import thut.core.common.config.Configure;

@Mod(modid = PokecubeOrigin.MODID, name = "Pokecube Origin", version = Reference.VERSION, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "*")
public class PokecubeOrigin
{
    public static final String MODID = "pokecube_origin";
    private Config             config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        config = new Config(PokecubeCore.core.getPokecubeConfig(e).getConfigFile());
        MinecraftForge.EVENT_BUS.register(this);
        doMetastuff();
    }

    @EventHandler
    public void init(FMLInitializationEvent e)
    {
        if (!config.active) return;
        PokedexEntry pichu = Database.getEntry("pichu");
        PokedexEntry pikachu = Database.getEntry("pikachu");

        if (pichu == null || pikachu == null) return;
        pichu.setModId(MODID);
        pikachu.setModId(MODID);
    }

    private void doMetastuff()
    {
        ModMetadata meta = FMLCommonHandler.instance().findContainerFor(this).getMetadata();
        meta.parent = PokecubeMobs.MODID;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void postInit(PostPostInit evt)
    {
        MinecraftForge.EVENT_BUS.unregister(this);
        if (!config.active) return;
        PokedexEntry pichu = Database.getEntry("pichu");
        PokedexEntry pikachu = Database.getEntry("pikachu");

        if (pichu == null || pikachu == null) return;

        pichu.texturePath = "textures/entity/";
        pikachu.texturePath = "textures/entity/";
        RenderPokemobs.addModel(pichu.getName() + "" + pichu.getModId(), new ModelPichu());
        RenderPokemobs.addModel(pikachu.getName() + "" + pikachu.getModId(), new ModelPikachu());
    }

    public static class Config extends ConfigBase
    {
        @Configure(category = "misc")
        private boolean active = true;

        public Config()
        {
            super(null);
        }

        public Config(File configFile)
        {
            super(configFile, new Config());
            MinecraftForge.EVENT_BUS.register(this);
            populateSettings();
            applySettings();
            save();
        }

        @Override
        protected void applySettings()
        {
        }
    }
}
