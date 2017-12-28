package pokecube.mobs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.interfaces.IPokecube;;

public class CommandGenStuff extends CommandBase
{

    @Override
    public String getName()
    {
        return "pokemobsfiles";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/pokemobsfiles";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        sender.sendMessage(new TextComponentString("Starting File Output"));
        for (PokedexEntry e : Database.allFormes)
        {
            registerAchievements(e);
        }
        sender.sendMessage(new TextComponentString("Advancements Done"));
        File dir = new File("./mods/pokecube/assets/pokecube_mobs/");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, "sounds.json");
        String json = SoundJsonGenerator.generateSoundJson();
        try
        {
            FileWriter write = new FileWriter(file);
            write.write(json);
            write.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        sender.sendMessage(new TextComponentString("Sounds Done"));
        generatePokecubesJsons();

        sender.sendMessage(new TextComponentString("Finished File Output"));
    }

    /** Comment these out to re-generate advancements. */
    public static void registerAchievements(PokedexEntry entry)
    {
        if (!entry.base) return;
        make(entry, "catch", "pokecube_mobs:capture/get_first_pokemob", "capture");
        make(entry, "kill", "pokecube_mobs:kill/root", "kill");
        make(entry, "hatch", "pokecube_mobs:hatch/root", "hatch");
    }

    protected static void make(PokedexEntry entry, String id, String parent, String path)
    {
        ResourceLocation key = new ResourceLocation(entry.getModId(), id + "_" + entry.getName());
        String json = AdvancementGenerator.makeJson(entry, id, parent);
        File dir = new File("./mods/pokecube/assets/pokecube_mobs/advancements/" + path + "/");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, key.getResourcePath() + ".json");
        try
        {
            FileWriter write = new FileWriter(file);
            write.write(json);
            write.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void generatePokecubesJsons()
    {
        for (ResourceLocation l : IPokecube.BEHAVIORS.getKeys())
        {
            String cube = l.getResourcePath();
            JsonObject blockJson = new JsonObject();
            blockJson.addProperty("parent", "pokecube:block/pokecubes");
            JsonObject textures = new JsonObject();
            textures.addProperty("top", "pokecube:items/" + cube + "cube" + "top");
            textures.addProperty("bottom", "pokecube:items/" + cube + "cube" + "bottom");
            textures.addProperty("front", "pokecube:items/" + cube + "cube" + "front");
            textures.addProperty("side", "pokecube:items/" + cube + "cube" + "side");
            textures.addProperty("back", "pokecube:items/" + cube + "cube" + "back");
            blockJson.add("textures", textures);

            File dir = new File("./mods/pokecube/assets/pokecube/models/block/");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, cube + "cube" + ".json");
            String json = AdvancementGenerator.GSON.toJson(blockJson);
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("parent", "pokecube:block/" + cube + "cube");
            JsonObject display = new JsonObject();
            JsonObject thirdPerson = new JsonObject();
            JsonArray rotation = new JsonArray();
            JsonArray translation = new JsonArray();
            JsonArray scale = new JsonArray();

            rotation.add(new JsonPrimitive(10));
            rotation.add(new JsonPrimitive(-45));
            rotation.add(new JsonPrimitive(170));

            translation.add(new JsonPrimitive(0));
            translation.add(new JsonPrimitive(1.5));
            translation.add(new JsonPrimitive(-2.75));

            scale.add(new JsonPrimitive(0.375));
            scale.add(new JsonPrimitive(0.375));
            scale.add(new JsonPrimitive(0.375));

            thirdPerson.add("rotation", rotation);
            thirdPerson.add("translation", translation);
            thirdPerson.add("scale", scale);
            display.add("thirdperson", thirdPerson);
            itemJson.add("display", display);

            dir = new File("./mods/pokecube/assets/pokecube/models/item/");
            if (!dir.exists()) dir.mkdirs();
            file = new File(dir, cube + "cube" + ".json");
            json = AdvancementGenerator.GSON.toJson(itemJson);
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    public static class SoundJsonGenerator
    {
        public static String generateSoundJson()
        {
            JsonObject soundJson = new JsonObject();
            List<PokedexEntry> baseFormes = Lists.newArrayList(Database.baseFormes.values());
            Collections.sort(baseFormes, new Comparator<PokedexEntry>()
            {
                @Override
                public int compare(PokedexEntry o1, PokedexEntry o2)
                {
                    return o1.getPokedexNb() - o2.getPokedexNb();
                }
            });
            for (PokedexEntry entry : baseFormes)
            {
                String soundName = entry.getSoundEvent().getSoundName().getResourcePath().replaceFirst("mobs.", "");
                JsonObject soundEntry = new JsonObject();
                soundEntry.addProperty("category", "hostile");
                soundEntry.addProperty("subtitle", entry.getUnlocalizedName());
                JsonArray sounds = new JsonArray();
                for (int i = 0; i < 3; i++)
                {
                    JsonObject sound = new JsonObject();
                    sound.addProperty("name", "pokecube_mobs:mobs/" + soundName);
                    sound.addProperty("volume", (i == 0 ? 0.8 : i == 1 ? 0.9 : 1));
                    sound.addProperty("pitch", (i == 0 ? 0.9 : i == 1 ? 0.95 : 1));
                    sounds.add(sound);
                }
                soundEntry.add("sounds", sounds);
                soundJson.add("mobs." + entry.getTrimmedName(), soundEntry);
            }
            return AdvancementGenerator.GSON.toJson(soundJson);
        }
    }

    public static class AdvancementGenerator
    {
        static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

        public static JsonObject fromInfo(PokedexEntry entry, String id)
        {
            JsonObject displayJson = new JsonObject();
            JsonObject icon = new JsonObject();
            icon.addProperty("item", "pokecube:pokecube");
            JsonObject title = new JsonObject();
            title.addProperty("translate", "achievement.pokecube." + id);
            JsonArray item = new JsonArray();
            JsonObject pokemobName = new JsonObject();
            pokemobName.addProperty("translate", entry.getUnlocalizedName());
            item.add(pokemobName);
            title.add("with", item);
            JsonObject description = new JsonObject();
            description.addProperty("translate", "achievement.pokecube." + id + ".desc");
            description.add("with", item);
            displayJson.add("icon", icon);
            displayJson.add("title", title);
            displayJson.add("description", description);
            if (entry.legendary) displayJson.addProperty("frame", "challenge");
            return displayJson;
        }

        public static String[][] makeRequirements(PokedexEntry entry)
        {
            return new String[][] { { entry.getName() } };
        }

        public static JsonObject fromCriteria(PokedexEntry entry, String id)
        {
            JsonObject critmap = new JsonObject();
            JsonObject sub = new JsonObject();
            sub.addProperty("trigger", "pokecube:" + id);
            JsonObject conditions = new JsonObject();
            if (id.equals("catch") || id.equals("kill")) conditions.addProperty("lenient", true);
            conditions.addProperty("entry", entry.getName());
            sub.add("conditions", conditions);
            critmap.add(id + "_" + entry.getName(), sub);
            return critmap;
        }

        public static String makeJson(PokedexEntry entry, String id, String parent)
        {
            JsonObject json = new JsonObject();
            json.add("display", fromInfo(entry, id));
            json.add("criteria", fromCriteria(entry, id));
            if (parent != null)
            {
                json.addProperty("parent", parent);
            }
            return GSON.toJson(json);
        }
    }
}
