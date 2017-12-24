package pokecube.mobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pokecube.adventures.utils.DBLoader;
import pokecube.core.PokecubeCore;
import pokecube.core.PokecubeItems;
import pokecube.core.database.Database;
import pokecube.core.database.Database.EnumDatabase;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.PokedexEntry.EvolutionData;
import pokecube.core.database.abilities.p.Pickup;
import pokecube.core.database.recipes.XMLRecipeHandler;
import pokecube.core.database.stats.CaptureStats;
import pokecube.core.database.stats.EggStats;
import pokecube.core.database.stats.StatsCollector;
import pokecube.core.events.CaptureEvent.Post;
import pokecube.core.events.CaptureEvent.Pre;
import pokecube.core.events.EvolveEvent;
import pokecube.core.events.handlers.EventsHandler;
import pokecube.core.events.onload.InitDatabase;
import pokecube.core.events.onload.RegisterPokecubes;
import pokecube.core.handlers.HeldItemHandler;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokecube;
import pokecube.core.interfaces.IPokecube.DefaultPokecubeBehavior;
import pokecube.core.interfaces.IPokecube.NormalPokecubeBehavoir;
import pokecube.core.interfaces.IPokecube.PokecubeBehavior;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.Stats;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.items.ItemHeldItems;
import pokecube.core.items.berries.BerryManager;
import pokecube.core.items.pokecubes.EntityPokecube;
import pokecube.core.items.pokecubes.PokecubeManager;
import pokecube.core.utils.Tools;
import pokecube.modelloader.CommonProxy;
import pokecube.modelloader.IMobProvider;
import pokecube.modelloader.ModPokecubeML;
import pokecube.modelloader.client.render.ModelWrapperEvent;
import pokecube.origin.render.ModelWrapperSpinda;
import thut.api.maths.Vector3;
import thut.core.client.ClientProxy;
import thut.lib.CompatWrapper;

@Mod(modid = PokecubeMobs.MODID, name = "Pokecube Mobs", version = Reference.VERSION, dependencies = "required-after:pokecube;required-after:pokecube_adventures", updateJSON = PokecubeMobs.UPDATEURL, acceptableRemoteVersions = Reference.MINVERSION, acceptedMinecraftVersions = Reference.MCVERSIONS)
public class PokecubeMobs implements IMobProvider
{
    public static class UpdateNotifier
    {
        public UpdateNotifier()
        {
            MinecraftForge.EVENT_BUS.register(this);
        }

        @SubscribeEvent
        public void onPlayerJoin(TickEvent.PlayerTickEvent event)
        {
            if (event.player.getEntityWorld().isRemote
                    && event.player == FMLClientHandler.instance().getClientPlayerEntity())
            {
                MinecraftForge.EVENT_BUS.unregister(this);
                Object o = Loader.instance().getIndexedModList().get(PokecubeMobs.MODID);
                CheckResult result = ForgeVersion.getResult(((ModContainer) o));
                if (result.status == Status.OUTDATED)
                {
                    ITextComponent mess = ClientProxy.getOutdatedMessage(result, "Pokecube Mobs");
                    (event.player).addChatMessage(mess);
                }
            }
        }
    }

    Map<PokedexEntry, Integer> genMap    = Maps.newHashMap();
    public static final String MODID     = "pokecube_mobs";
    public static final String UPDATEURL = "https://gist.githubusercontent.com/Thutmose/4d7320c36696cd39b336/raw/mobs.json";

    public PokecubeMobs()
    {
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_1/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_2/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_3/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_4/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_5/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_6/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_7/entity/models/");
        CommonProxy.registerModelProvider(MODID, this);

        HeldItemHandler.megaVariants.add("megastone");
        HeldItemHandler.megaVariants.add("shiny_charm");
        HeldItemHandler.megaVariants.add("omegaorb");
        HeldItemHandler.megaVariants.add("alphaorb");
        HeldItemHandler.megaVariants.add("absolmega");
        HeldItemHandler.megaVariants.add("aerodactylmega");
        HeldItemHandler.megaVariants.add("aggronmega");
        HeldItemHandler.megaVariants.add("alakazammega");
        HeldItemHandler.megaVariants.add("altariamega");
        HeldItemHandler.megaVariants.add("ampharosmega");
        HeldItemHandler.megaVariants.add("banettemega");
        HeldItemHandler.megaVariants.add("beedrillmega");
        HeldItemHandler.megaVariants.add("blastoisemega");
        HeldItemHandler.megaVariants.add("blazikenmega");
        HeldItemHandler.megaVariants.add("cameruptmega");
        HeldItemHandler.megaVariants.add("charizardmega-y");
        HeldItemHandler.megaVariants.add("charizardmega-x");
        HeldItemHandler.megaVariants.add("dianciemega");
        HeldItemHandler.megaVariants.add("gallademega");
        HeldItemHandler.megaVariants.add("garchompmega");
        HeldItemHandler.megaVariants.add("gardevoirmega");
        HeldItemHandler.megaVariants.add("gengarmega");
        HeldItemHandler.megaVariants.add("glaliemega");
        HeldItemHandler.megaVariants.add("gyaradosmega");
        HeldItemHandler.megaVariants.add("heracrossmega");
        HeldItemHandler.megaVariants.add("houndoommega");
        HeldItemHandler.megaVariants.add("kangaskhanmega");
        HeldItemHandler.megaVariants.add("latiasmega");
        HeldItemHandler.megaVariants.add("latiosmega");
        HeldItemHandler.megaVariants.add("lopunnymega");
        HeldItemHandler.megaVariants.add("lucariomega");
        HeldItemHandler.megaVariants.add("manectricmega");
        HeldItemHandler.megaVariants.add("mawilemega");
        HeldItemHandler.megaVariants.add("mewtwomega-y");
        HeldItemHandler.megaVariants.add("mewtwomega-x");
        HeldItemHandler.megaVariants.add("metagrossmega");
        HeldItemHandler.megaVariants.add("pidgeotmega");
        HeldItemHandler.megaVariants.add("pinsirmega");
        HeldItemHandler.megaVariants.add("sableyemega");
        HeldItemHandler.megaVariants.add("salamencemega");
        HeldItemHandler.megaVariants.add("sceptilemega");
        HeldItemHandler.megaVariants.add("scizormega");
        HeldItemHandler.megaVariants.add("sharpedomega");
        HeldItemHandler.megaVariants.add("slowbromega");
        HeldItemHandler.megaVariants.add("steelixmega");
        HeldItemHandler.megaVariants.add("swampertmega");
        HeldItemHandler.megaVariants.add("tyranitarmega");
        HeldItemHandler.megaVariants.add("venusaurmega");
        HeldItemHandler.sortMegaVariants();

        HeldItemHandler.fossilVariants.add("omanyte");
        HeldItemHandler.fossilVariants.add("kabuto");
        HeldItemHandler.fossilVariants.add("aerodactyl");
        HeldItemHandler.fossilVariants.add("lileep");
        HeldItemHandler.fossilVariants.add("anorith");
        HeldItemHandler.fossilVariants.add("cranidos");
        HeldItemHandler.fossilVariants.add("shieldon");
        HeldItemHandler.fossilVariants.add("tyrunt");
        HeldItemHandler.fossilVariants.add("amaura");

        ItemHeldItems.variants.add("waterstone");
        ItemHeldItems.variants.add("firestone");
        ItemHeldItems.variants.add("leafstone");
        ItemHeldItems.variants.add("thunderstone");
        ItemHeldItems.variants.add("moonstone");
        ItemHeldItems.variants.add("sunstone");
        ItemHeldItems.variants.add("shinystone");
        ItemHeldItems.variants.add("ovalstone");
        ItemHeldItems.variants.add("everstone");
        ItemHeldItems.variants.add("duskstone");
        ItemHeldItems.variants.add("dawnstone");
        ItemHeldItems.variants.add("kingsrock");
        ItemHeldItems.variants.add("dubiousdisc");
        ItemHeldItems.variants.add("electirizer");
        ItemHeldItems.variants.add("magmarizer");
        ItemHeldItems.variants.add("reapercloth");
        ItemHeldItems.variants.add("prismscale");
        ItemHeldItems.variants.add("protector");
        ItemHeldItems.variants.add("upgrade");
        ItemHeldItems.variants.add("metalcoat");

        MinecraftForge.EVENT_BUS.register(this);
        BerryHelper.initBerries();
        if (Loader.isModLoaded("thut_wearables")) MegaWearablesHelper.initExtraWearables();
        DBLoader.trainerDatabases.add("trainers.xml");
        DBLoader.tradeDatabases.add("trades.xml");
        MiscItemHelper.init();
        checkConfigFiles();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration config = PokecubeCore.instance.getPokecubeConfig(event);
        config.load();
        String var = config.getString("pickuploottable", Configuration.CATEGORY_GENERAL, "",
                "If Set, this is the loot table that pickup will use.");
        if (!var.isEmpty())
        {
            Pickup.lootTable = new ResourceLocation(var);
        }
        config.save();
        if (event.getSide() == Side.CLIENT)
        {
            new UpdateNotifier();
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandGenStuff());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void initModel(ModelWrapperEvent evt)
    {
        if (evt.name.equalsIgnoreCase("spinda"))
        {
            evt.wrapper = new ModelWrapperSpinda(evt.wrapper.model, evt.wrapper.renderer);
        }
    }

    @Override
    public String getModelDirectory(PokedexEntry entry)
    {
        int gen = getGen(entry);
        switch (gen)
        {
        case 1:
            return "gen_1/entity/models/";
        case 2:
            return "gen_2/entity/models/";
        case 3:
            return "gen_3/entity/models/";
        case 4:
            return "gen_4/entity/models/";
        case 5:
            return "gen_5/entity/models/";
        case 6:
            return "gen_6/entity/models/";
        case 7:
            return "gen_7/entity/models/";
        }
        return "entity/models/";
    }

    private int getGen(PokedexEntry entry)
    {
        int gen;
        if (genMap.containsKey(entry))
        {
            gen = genMap.get(entry);
        }
        else
        {
            gen = entry.getGen();
            PokedexEntry real = entry;
            if (entry.getBaseForme() != null) entry = entry.getBaseForme();
            for (EvolutionData d : entry.getEvolutions())
            {
                int gen1 = d.evolution.getGen();
                if (genMap.containsKey(d.evolution))
                {
                    gen1 = genMap.get(d.evolution);
                }
                if (gen1 < gen)
                {
                    gen = gen1;
                }
                for (EvolutionData d1 : d.evolution.getEvolutions())
                {
                    gen1 = d1.evolution.getGen();
                    if (genMap.containsKey(d1.evolution))
                    {
                        gen1 = genMap.get(d1.evolution);
                    }
                    if (d.evolution == entry && gen1 < gen)
                    {
                        gen = gen1;
                    }
                }
            }
            for (PokedexEntry e : Database.allFormes)
            {
                int gen1 = e.getGen();
                if (genMap.containsKey(e))
                {
                    gen1 = genMap.get(e);
                }
                for (EvolutionData d : e.getEvolutions())
                {
                    if (d.evolution == entry && gen1 < gen)
                    {
                        gen = gen1;
                    }
                }
            }
            genMap.put(real, gen);
        }
        return gen;
    }

    @Override
    public String getTextureDirectory(PokedexEntry entry)
    {
        int gen = getGen(entry);
        switch (gen)
        {
        case 1:
            return "gen_1/entity/textures/";
        case 2:
            return "gen_2/entity/textures/";
        case 3:
            return "gen_3/entity/textures/";
        case 4:
            return "gen_4/entity/textures/";
        case 5:
            return "gen_5/entity/textures/";
        case 6:
            return "gen_6/entity/textures/";
        case 7:
            return "gen_7/entity/textures/";
        }
        return "entity/textures/";
    }

    @Override
    public Object getMod()
    {
        return this;
    }

    @SubscribeEvent
    public void registerPokecubes(RegisterPokecubes event)
    {
        final PokecubeHelper helper = new PokecubeHelper();
        PokecubeBehavior.DEFAULTCUBE = new ResourceLocation("pokecube", "poke");

        event.behaviors.add(new NormalPokecubeBehavoir(1).setRegistryName(PokecubeBehavior.DEFAULTCUBE));
        event.behaviors.add(new NormalPokecubeBehavoir(1.5).setRegistryName("pokecube", "great"));
        event.behaviors.add(new NormalPokecubeBehavoir(2).setRegistryName("pokecube", "ultra"));
        event.behaviors.add(new NormalPokecubeBehavoir(255).setRegistryName("pokecube", "master"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.dusk(mob);
            }
        }.setRegistryName("pokecube", "dusk"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.quick(mob);
            }
        }.setRegistryName("pokecube", "quick"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.timer(mob);
            }
        }.setRegistryName("pokecube", "timer"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.net(mob);
            }
        }.setRegistryName("pokecube", "net"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.nest(mob);
            }
        }.setRegistryName("pokecube", "nest"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.dive(mob);
            }
        }.setRegistryName("pokecube", "dive"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.premier(mob);
            }
        }.setRegistryName("pokecube", "premier"));
        event.behaviors.add(new NormalPokecubeBehavoir(1).setRegistryName("pokecube", "cherish"));
        event.behaviors.add(new NormalPokecubeBehavoir(1.5).setRegistryName("pokecube", "safari"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.level(mob);
            }
        }.setRegistryName("pokecube", "level"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.lure(mob);
            }
        }.setRegistryName("pokecube", "lure"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.moon(mob);
            }
        }.setRegistryName("pokecube", "moon"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public void onPostCapture(Post evt)
            {
                IPokemob mob = evt.caught;
                mob.addHappiness(200 - mob.getHappiness());
            }

            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return 1;
            }
        }.setRegistryName("pokecube", "friend"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.love(mob);
            }
        }.setRegistryName("pokecube", "love"));
        event.behaviors.add(new NormalPokecubeBehavoir(1)
        {
            @Override
            public int getAdditionalBonus(IPokemob mob)
            {
                return helper.heavy(mob);
            }
        }.setRegistryName("pokecube", "heavy"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.fast(mob);
            }
        }.setRegistryName("pokecube", "fast"));
        event.behaviors.add(new NormalPokecubeBehavoir(1.5).setRegistryName("pokecube", "sport"));
        event.behaviors.add(new NormalPokecubeBehavoir(1)
        {
            @Override
            public void onUpdate(IPokemob mob)
            {
                helper.luxury(mob);
            }
        }.setRegistryName("pokecube", "luxury"));
        event.behaviors.add(new NormalPokecubeBehavoir(1)
        {
            @Override
            public void onPostCapture(Post evt)
            {
                IPokemob mob = evt.caught;
                mob.getEntity().setHealth(mob.getEntity().getMaxHealth());
                mob.healStatus();
            }
        }.setRegistryName("pokecube", "heal"));
        event.behaviors.add(new NormalPokecubeBehavoir(255).setRegistryName("pokecube", "park"));
        event.behaviors.add(new NormalPokecubeBehavoir(255).setRegistryName("pokecube", "dream"));

        PokecubeBehavior snag = new PokecubeBehavior()
        {

            @Override
            public void onPostCapture(Post evt)
            {
                IPokemob mob = evt.caught;
                evt.pokecube.entityDropItem(PokecubeManager.pokemobToItem(mob), 1.0F);
                evt.setCanceled(true);
            }

            @Override
            public void onPreCapture(Pre evt)
            {
                boolean tameSnag = !evt.caught.isPlayerOwned() && evt.caught.getPokemonAIState(IMoveConstants.TAMED);

                if (evt.caught.isShadow())
                {
                    EntityPokecube cube = (EntityPokecube) evt.pokecube;

                    IPokemob mob = CapabilityPokemob.getPokemobFor(
                            PokecubeCore.instance.createPokemob(evt.caught.getPokedexEntry(), cube.getEntityWorld()));
                    cube.tilt = Tools.computeCatchRate(mob, 1);
                    cube.time = cube.tilt * 20;

                    if (!tameSnag) evt.caught.setPokecube(evt.filledCube);

                    cube.setEntityItemStack(PokecubeManager.pokemobToItem(evt.caught));
                    PokecubeManager.setTilt(cube.getEntityItem(), cube.tilt);
                    Vector3.getNewVector().set(evt.pokecube).moveEntity(cube);
                    evt.caught.getEntity().setDead();
                    cube.motionX = cube.motionZ = 0;
                    cube.motionY = 0.1;
                    cube.getEntityWorld().spawnEntityInWorld(cube.copy());
                    evt.pokecube.setDead();
                }
                evt.setCanceled(true);
            }

            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return 0;
            }
        };

        PokecubeBehavior repeat = new PokecubeBehavior()
        {
            @Override
            public void onPostCapture(Post evt)
            {

            }

            @Override
            public void onPreCapture(Pre evt)
            {
                if (evt.getResult() == Result.DENY) return;

                EntityPokecube cube = (EntityPokecube) evt.pokecube;

                IPokemob mob = CapabilityPokemob.getPokemobFor(
                        PokecubeCore.instance.createPokemob(evt.caught.getPokedexEntry(), cube.getEntityWorld()));
                Vector3 v = Vector3.getNewVector();
                Entity thrower = cube.shootingEntity;
                int has = CaptureStats.getTotalNumberOfPokemobCaughtBy(thrower.getUniqueID(), mob.getPokedexEntry());
                has = has + EggStats.getTotalNumberOfPokemobHatchedBy(thrower.getUniqueID(), mob.getPokedexEntry());
                double rate = has > 0 ? 3 : 1;
                cube.tilt = Tools.computeCatchRate(mob, rate);
                cube.time = cube.tilt * 20;
                evt.caught.setPokecube(evt.filledCube);
                cube.setEntityItemStack(PokecubeManager.pokemobToItem(evt.caught));
                PokecubeManager.setTilt(cube.getEntityItem(), cube.tilt);
                v.set(evt.pokecube).moveEntity(cube);
                v.moveEntity(mob.getEntity());
                evt.caught.getEntity().setDead();
                cube.motionX = cube.motionZ = 0;
                cube.motionY = 0.1;
                cube.getEntityWorld().spawnEntityInWorld(cube.copy());
                evt.setCanceled(true);
                evt.pokecube.setDead();
            }

            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return 0;
            }

        };

        event.behaviors.add(snag.setRegistryName("pokecube", "snag"));
        event.behaviors.add(repeat.setRegistryName("pokecube", "repeat"));
    }

    @SubscribeEvent
    public void makeShedinja(EvolveEvent.Post evt)
    {
        Entity owner;
        if ((owner = evt.mob.getPokemonOwner()) instanceof EntityPlayer)
        {
            makeShedinja(evt.mob, (EntityPlayer) owner);
        }
    }

    @SubscribeEvent
    public void livingUpdate(LivingUpdateEvent evt)
    {
        IPokemob shuckle = CapabilityPokemob.getPokemobFor(evt.getEntityLiving());
        if (shuckle != null && shuckle.getPokedexNb() == 213)
        {
            if (evt.getEntityLiving().getEntityWorld().isRemote) return;

            ItemStack item = evt.getEntityLiving().getHeldItemMainhand();
            if (!CompatWrapper.isValid(item)) return;
            Item itemId = item.getItem();
            boolean berry = item.isItemEqual(BerryManager.getBerryItem("oran"));
            Random r = new Random();
            if (berry && r.nextGaussian() > EventsHandler.juiceChance)
            {
                if (shuckle.getPokemonOwner() != null)
                {
                    String message = "A sweet smell is coming from "
                            + shuckle.getPokemonDisplayName().getFormattedText();
                    ((EntityPlayer) shuckle.getPokemonOwner()).addChatMessage(new TextComponentString(message));
                }
                shuckle.setHeldItem(new ItemStack(PokecubeItems.berryJuice));
                return;
            }
            berry = itemId == PokecubeItems.berryJuice;
            if (berry && (r.nextGaussian() > EventsHandler.candyChance))
            {
                ItemStack candy = PokecubeItems.makeCandyStack();
                if (!CompatWrapper.isValid(candy)) return;

                if (shuckle.getPokemonOwner() != null && shuckle.getPokemonOwner() instanceof EntityPlayer)
                {
                    String message = "The smell coming from " + shuckle.getPokemonDisplayName().getFormattedText()
                            + " has changed";
                    ((EntityPlayer) shuckle.getPokemonOwner()).addChatMessage(new TextComponentString(message));
                }
                shuckle.setHeldItem(candy);
                return;
            }
        }
    }

    @SubscribeEvent
    public void evolveTyrogue(EvolveEvent.Pre evt)
    {
        if (evt.mob.getPokedexEntry() == Database.getEntry("Tyrogue"))
        {
            int atk = evt.mob.getStat(Stats.ATTACK, false);
            int def = evt.mob.getStat(Stats.DEFENSE, false);
            if (atk > def) evt.forme = Database.getEntry("Hitmonlee");
            else if (def > atk) evt.forme = Database.getEntry("Hitmonchan");
            else evt.forme = Database.getEntry("Hitmontop");
        }
    }

    void makeShedinja(IPokemob evo, EntityPlayer player)
    {
        if (evo.getPokedexEntry() == Database.getEntry("ninjask"))
        {
            InventoryPlayer inv = player.inventory;
            boolean hasCube = false;
            boolean hasSpace = false;
            ItemStack cube = CompatWrapper.nullStack;
            int m = -1;
            for (int n = 0; n < inv.getSizeInventory(); n++)
            {
                ItemStack item = inv.getStackInSlot(n);
                if (item == CompatWrapper.nullStack) hasSpace = true;
                ResourceLocation key = PokecubeItems.getCubeId(item);
                if (!hasCube && key != null && IPokecube.BEHAVIORS.containsKey(key) && !PokecubeManager.isFilled(item))
                {
                    hasCube = true;
                    cube = item;
                    m = n;
                }
                if (hasCube && hasSpace) break;

            }
            if (hasCube && hasSpace)
            {
                Entity pokemon = PokecubeMod.core.createPokemob(Database.getEntry("shedinja"), player.getEntityWorld());
                if (pokemon != null)
                {
                    ItemStack mobCube = cube.copy();
                    CompatWrapper.setStackSize(mobCube, 1);
                    IPokemob poke = CapabilityPokemob.getPokemobFor(pokemon);
                    poke.setPokecube(mobCube);
                    poke.setPokemonOwner(player);
                    poke.setExp(Tools.levelToXp(poke.getExperienceMode(), 20), true);
                    poke.getEntity().setHealth(poke.getEntity().getMaxHealth());
                    ItemStack shedinja = PokecubeManager.pokemobToItem(poke);
                    StatsCollector.addCapture(poke);
                    CompatWrapper.increment(cube, -1);
                    if (!CompatWrapper.isValid(cube)) inv.setInventorySlotContents(m, CompatWrapper.nullStack);
                    inv.addItemStackToInventory(shedinja);
                }
            }
        }
    }

    @SubscribeEvent
    public void registerDatabases(InitDatabase.Pre evt)
    {
        Database.addDatabase("pokemobs_pokedex.json", EnumDatabase.POKEMON);
        Database.addDatabase("pokemobs_spawns.json", EnumDatabase.POKEMON);
        Database.addDatabase("pokemobs_drops.json", EnumDatabase.POKEMON);
        Database.addDatabase("pokemobs_interacts.json", EnumDatabase.POKEMON);
    }

    public static void checkConfigFiles()
    {
        writeDefaultConfig();
        return;
    }

    static String CONFIGLOC  = Database.CONFIGLOC;
    static String DBLOCATION = Database.DBLOCATION;

    private static void writeDefaultConfig()
    {
        try
        {
            File temp = new File(Database.CONFIGLOC);
            if (!temp.exists())
            {
                temp.mkdirs();
            }
            DBLOCATION = Database.DBLOCATION.replace("pokecube", "pokecube_mobs");
            copyDatabaseFile("moves.json");
            copyDatabaseFile("animations.json");

            CONFIGLOC = CONFIGLOC + "pokemobs" + File.separator;
            temp = new File(Database.CONFIGLOC);
            if (!temp.exists())
            {
                temp.mkdirs();
            }
            DBLOCATION = DBLOCATION + "pokemobs/";
            copyDatabaseFile("pokemobs_pokedex.json");
            copyDatabaseFile("pokemobs_spawns.json");
            copyDatabaseFile("pokemobs_drops.json");
            copyDatabaseFile("pokemobs_interacts.json");

            DBLOCATION = Database.DBLOCATION.replace("pokecube", "pokecube_mobs");
            CONFIGLOC = Database.CONFIGLOC;
            copyDatabaseFile("pokecubes_recipes.xml");
            copyDatabaseFile("pokemob_item_recipes.xml");
            XMLRecipeHandler.recipeFiles.add("pokecubes_recipes");
            XMLRecipeHandler.recipeFiles.add("pokemob_item_recipes");
            DBLOCATION = Database.DBLOCATION.replace("pokecube", "pokecube_adventures");
            CONFIGLOC = Database.CONFIGLOC.replace("database", "trainers");
            temp = new File(Database.CONFIGLOC);
            if (!temp.exists())
            {
                temp.mkdirs();
            }
            copyDatabaseFile("trainers.xml");
            copyDatabaseFile("trades.xml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    static void copyDatabaseFile(String name)
    {
        File temp1 = new File(CONFIGLOC + name);
        if (temp1.exists() && !Database.FORCECOPY)
        {
            PokecubeMod.log("Not Overwriting old database: " + temp1);
            return;
        }
        ArrayList<String> rows = getFile(DBLOCATION + name);
        int n = 0;
        try
        {
            File file = new File(CONFIGLOC + name);
            file.getParentFile().mkdirs();
            PokecubeMod.log("Copying Database File: " + file);
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            for (int i = 0; i < rows.size(); i++)
            {
                out.write(rows.get(i) + "\n");
                n++;
            }
            out.close();
        }
        catch (Exception e)
        {
            PokecubeMod.log(Level.SEVERE, name + " " + n, e);
        }
    }

    public static ArrayList<String> getFile(String file)
    {
        InputStream res = (PokecubeMobs.class).getResourceAsStream(file);

        ArrayList<String> rows = new ArrayList<String>();
        BufferedReader br = null;
        String line = "";
        try
        {

            br = new BufferedReader(new InputStreamReader(res));
            while ((line = br.readLine()) != null)
            {
                rows.add(line);
            }

        }
        catch (FileNotFoundException e)
        {
            PokecubeMod.log(Level.SEVERE, "Missing a Database file " + file, e);
        }
        catch (NullPointerException e)
        {
            try
            {
                FileReader temp = new FileReader(new File(file));
                br = new BufferedReader(temp);
                while ((line = br.readLine()) != null)
                {
                    rows.add(line);
                }
            }
            catch (Exception e1)
            {
                PokecubeMod.log(Level.SEVERE, "Error with " + file, e1);
            }

        }
        catch (Exception e)
        {
            PokecubeMod.log(Level.SEVERE, "Error with " + file, e);
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (Exception e)
                {
                    PokecubeMod.log(Level.SEVERE, "Error with " + file, e);
                }
            }
        }

        return rows;
    }
}
