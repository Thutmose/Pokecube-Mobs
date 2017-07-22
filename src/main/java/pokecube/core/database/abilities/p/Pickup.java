package pokecube.core.database.abilities.p;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import pokecube.core.PokecubeItems;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;
import thut.lib.CompatWrapper;

public class Pickup extends Ability
{
    @Override
    public void onUpdate(IPokemob mob)
    {
        EntityLivingBase poke = (EntityLivingBase) mob;
        if (poke.ticksExisted % 200 == 0 && Math.random() < 0.1)
        {
            if (!CompatWrapper.isValid(poke.getHeldItemMainhand()))
            {
                List<?> items = new ArrayList<Object>(PokecubeItems.heldItems);
                Collections.shuffle(items);
                ItemStack item = (ItemStack) items.get(0);
                if (CompatWrapper.isValid(item)) poke.setHeldItem(EnumHand.MAIN_HAND, item.copy());
            }
        }
    }

}
