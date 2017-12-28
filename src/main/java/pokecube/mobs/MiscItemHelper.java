package pokecube.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pokecube.core.PokecubeItems;
import pokecube.core.events.PostPostInit;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.interfaces.IPokemobUseable;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.items.UsableItemEffects;
import pokecube.core.items.UsableItemEffects.VitaminUsable.VitaminEffect;
import pokecube.core.items.vitamins.ItemVitamin;
import pokecube.core.utils.PokeType;
import pokecube.core.utils.Tools;

public class MiscItemHelper
{
    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(new MiscItemHelper());

        ItemVitamin.vitamins.add("carbos");
        ItemVitamin.vitamins.add("zinc");
        ItemVitamin.vitamins.add("protein");
        ItemVitamin.vitamins.add("calcium");
        ItemVitamin.vitamins.add("hpup");
        ItemVitamin.vitamins.add("iron");

        VitaminEffect value = new VitaminEffect()
        {
            @Override
            public boolean onUse(IPokemob pokemob, ItemStack stack, EntityLivingBase user)
            {
                return feedToPokemob(stack, pokemob.getEntity());
            }
        };

        UsableItemEffects.VitaminUsable.effects.put("carbos", value);
        UsableItemEffects.VitaminUsable.effects.put("zinc", value);
        UsableItemEffects.VitaminUsable.effects.put("protein", value);
        UsableItemEffects.VitaminUsable.effects.put("calcium", value);
        UsableItemEffects.VitaminUsable.effects.put("hpup", value);
        UsableItemEffects.VitaminUsable.effects.put("iron", value);
    }

    public static boolean feedToPokemob(ItemStack stack, Entity entity)
    {
        IPokemob pokemob = CapabilityPokemob.getPokemobFor(entity);
        if (pokemob != null)
        {
            if (Tools.isSameStack(stack, PokecubeItems.getStack("hpup")))
            {
                pokemob.addEVs(new byte[] { 10, 0, 0, 0, 0, 0 });
                return true;
            }
            if (Tools.isSameStack(stack, PokecubeItems.getStack("protein")))
            {
                pokemob.addEVs(new byte[] { 0, 10, 0, 0, 0, 0 });
                return true;
            }
            if (Tools.isSameStack(stack, PokecubeItems.getStack("iron")))
            {
                pokemob.addEVs(new byte[] { 0, 0, 10, 0, 0, 0 });
                return true;
            }
            if (Tools.isSameStack(stack, PokecubeItems.getStack("calcium")))
            {
                pokemob.addEVs(new byte[] { 0, 0, 0, 10, 0, 0 });
                return true;
            }
            if (Tools.isSameStack(stack, PokecubeItems.getStack("zinc")))
            {
                pokemob.addEVs(new byte[] { 0, 0, 0, 0, 10, 0 });
                return true;
            }
            if (Tools.isSameStack(stack, PokecubeItems.getStack("carbos")))
            {
                pokemob.addEVs(new byte[] { 0, 0, 0, 0, 0, 10 });
                return true;
            }
        }
        return false;
    }

    public static class CharcoalEffect implements IPokemobUseable, ICapabilityProvider
    {
        private static PokeType FIRE;

        public CharcoalEffect()
        {
            if (FIRE == null)
            {
                FIRE = PokeType.getType("fire");
            }
        }

        /** @param pokemob
         * @param stack
         * @return */
        @Override
        public boolean onMoveTick(IPokemob pokemob, ItemStack stack, MovePacket moveuse)
        {
            if (pokemob == moveuse.attacker && moveuse.pre)
            {
                if (moveuse.getMove().getType(pokemob) == FIRE)
                {
                    moveuse.PWR *= 1.2;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            return capability == IPokemobUseable.USABLEITEM_CAP;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            return hasCapability(capability, facing) ? IPokemobUseable.USABLEITEM_CAP.cast(this) : null;
        }

    }

    public static final ResourceLocation USABLE = new ResourceLocation(PokecubeMobs.MODID, "usables");
    private ItemStack                    theStack;

    @SubscribeEvent
    public void postPostInit(PostPostInit event)
    {
        // Charcoal
        PokecubeItems.addToHoldables(theStack = new ItemStack(Items.COAL, 1, 1));
        theStack = theStack.copy();
    }

    @SubscribeEvent
    public void registerCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        if (event.getCapabilities().containsKey(USABLE) || theStack == null || theStack == event.getObject()) return;
        if (Tools.isSameStack(theStack, event.getObject()))
        {
            event.addCapability(USABLE, new CharcoalEffect());
        }
    }

}
