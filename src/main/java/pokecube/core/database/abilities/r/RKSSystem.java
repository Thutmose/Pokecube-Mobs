package pokecube.core.database.abilities.r;

import net.minecraft.item.ItemStack;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.utils.PokeType;
import thut.lib.CompatWrapper;

public class RKSSystem extends Ability
{
    private static PokeType normal = null;

    @Override
    public void onUpdate(IPokemob mob)
    {
        PokedexEntry entry = mob.getPokedexEntry();
        if (normal == null) normal = PokeType.getType("normal");
        if (!entry.getName().contains("Silvally")) return;
        ItemStack held = mob.getHeldItem();
        if (CompatWrapper.isValid(held) && held.getItem().getRegistryName().getResourceDomain().contains("pokecube")
                && held.getItem().getRegistryName().getResourcePath().contains("badge") && held.hasTagCompound())
        {
            String name = held.getTagCompound().getString("type");
            String typename = name.replace("badge", "");
            PokeType type = PokeType.getType(typename);
            if (type != PokeType.unknown)
            {
                mob.setType1(type);
                return;
            }
        }
        if (normal != null) mob.setType1(normal);
    }
}
