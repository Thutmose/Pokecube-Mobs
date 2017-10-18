package pokecube.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.HappinessType;
import pokecube.core.items.UsableItemEffects.BerryUsable.BerryEffect;
import pokecube.core.items.berries.BerryManager;

public class BerryHelper implements IMoveConstants
{
    public static class DefaultBerryEffect implements BerryEffect
    {
        /** Called every tick while this item is the active held item for the
         * pokemob.
         * 
         * @param pokemob
         * @param stack
         * @return something happened */
        @Override
        public boolean onTick(IPokemob pokemob, ItemStack stack)
        {
            return onUse(pokemob, stack, pokemob.getEntity());
        }

        /** Called when this item is "used". Normally this means via right
         * clicking the pokemob with the itemstack. It can also be called via
         * onTick or onMoveTick, in which case user will be pokemob.getEntity()
         * 
         * @param user
         * @param pokemob
         * @param stack
         * @return something happened */
        @Override
        public boolean onUse(IPokemob pokemob, ItemStack stack, EntityLivingBase user)
        {
            return applyEffect(pokemob, user, stack);
        }
    }

    public static void initBerries()
    {
        DefaultBerryEffect effect = new DefaultBerryEffect();
        BerryManager.addBerry("cheri", 1, 10, 0, 0, 0, 0, effect);// Cures
                                                                  // Paralysis
        BerryManager.addBerry("chesto", 2, 0, 10, 0, 0, 0, effect);// Cures
                                                                   // sleep
        BerryManager.addBerry("pecha", 3, 0, 0, 10, 0, 0, effect);// Cures
                                                                  // poison
        BerryManager.addBerry("rawst", 4, 0, 0, 0, 10, 0, effect);// Cures burn
        BerryManager.addBerry("aspear", 5, 0, 0, 0, 0, 10, effect);// Cures
                                                                   // freeze
        BerryManager.addBerry("leppa", 6, 10, 0, 10, 10, 10, effect);// Restores
                                                                     // 10PP
        BerryManager.addBerry("oran", 7, 10, 10, 10, 10, 10, effect);// Restores
                                                                     // 10HP
        BerryManager.addBerry("persim", 8, 10, 10, 10, 0, 10, effect);// Cures
                                                                      // confusion
        BerryManager.addBerry("lum", 9, 10, 10, 10, 10, 0, effect);// Cures any
                                                                   // status
                                                                   // ailment
        BerryManager.addBerry("sitrus", 10, 0, 10, 10, 10, 10, effect);// Restores
                                                                       // 1/4 HP
        BerryManager.addBerry("nanab", 18, 0, 0, 10, 10, 0, effect);// Pokeblock
                                                                    // ingredient
        BerryManager.addBerry("pinap", 20, 10, 0, 0, 0, 10, effect);// Pokeblock
                                                                    // ingredient
        BerryManager.addBerry("pomeg", 21, 10, 0, 10, 10, 0, effect);// EV Berry
        BerryManager.addBerry("kelpsy", 22, 0, 10, 0, 10, 10, effect);// EV
                                                                      // Berry
        BerryManager.addBerry("qualot", 23, 10, 0, 10, 0, 10, effect);// EV
                                                                      // Berry
        BerryManager.addBerry("hondew", 24, 10, 10, 0, 10, 0, effect);// EV
                                                                      // Berry
        BerryManager.addBerry("grepa", 25, 0, 10, 10, 0, 10, effect);// EV Berry
        BerryManager.addBerry("tamato", 26, 20, 10, 0, 0, 0, effect);// EV Berry
        BerryManager.addBerry("cornn", 27, 0, 20, 10, 0, 0, effect);// Pokeblock
                                                                    // ingredient
        BerryManager.addBerry("enigma", 60, 40, 10, 0, 0, 0, effect);// Restores
                                                                     // 1/4 of
                                                                     // HP
        BerryManager.addBerry("jaboca", 63, 0, 0, 0, 40, 10, effect);// 4th gen.
                                                                     // Causes
                                                                     // recoil
                                                                     // damage
                                                                     // on foe
                                                                     // if
                                                                     // holder
                                                                     // is hit
                                                                     // by a
                                                                     // physical
                                                                     // move
        BerryManager.addBerry("rowap", 64, 10, 0, 0, 0, 40, effect);// 4th gen.
                                                                    // Causes
                                                                    // recoil
                                                                    // damage on
                                                                    // foe if
                                                                    // holder is
                                                                    // hit by a
                                                                    // special
                                                                    // move
    }

    public static boolean berryEffect(IPokemob pokemob, EntityLivingBase user, ItemStack berry)
    {

        byte status = pokemob.getStatus();
        int berryId = berry.getItemDamage();
        if (!BerryManager.berryNames.containsKey(berryId)) return false;
        if (berryId == 21)
        {
            HappinessType.applyHappiness(pokemob, HappinessType.EVBERRY);
            byte[] evs = pokemob.getEVs();
            evs[0] = (byte) Math.max(Byte.MIN_VALUE, ((int) evs[0]) - 10);
            pokemob.setEVs(evs);
            return true;
        }
        if (berryId == 22)
        {
            HappinessType.applyHappiness(pokemob, HappinessType.EVBERRY);
            byte[] evs = pokemob.getEVs();
            evs[1] = (byte) Math.max(Byte.MIN_VALUE, ((int) evs[1]) - 10);
            pokemob.setEVs(evs);
            return true;
        }
        if (berryId == 23)
        {
            HappinessType.applyHappiness(pokemob, HappinessType.EVBERRY);
            byte[] evs = pokemob.getEVs();
            evs[2] = (byte) Math.max(Byte.MIN_VALUE, ((int) evs[2]) - 10);
            pokemob.setEVs(evs);
            return true;
        }
        if (berryId == 24)
        {
            HappinessType.applyHappiness(pokemob, HappinessType.EVBERRY);
            byte[] evs = pokemob.getEVs();
            evs[3] = (byte) Math.max(Byte.MIN_VALUE, ((int) evs[3]) - 10);
            pokemob.setEVs(evs);
            return true;
        }
        if (berryId == 25)
        {
            HappinessType.applyHappiness(pokemob, HappinessType.EVBERRY);
            byte[] evs = pokemob.getEVs();
            evs[4] = (byte) Math.max(Byte.MIN_VALUE, ((int) evs[4]) - 10);
            pokemob.setEVs(evs);
            return true;
        }
        if (berryId == 26)
        {
            HappinessType.applyHappiness(pokemob, HappinessType.EVBERRY);
            byte[] evs = pokemob.getEVs();
            evs[5] = (byte) Math.max(Byte.MIN_VALUE, ((int) evs[5]) - 10);
            pokemob.setEVs(evs);
            return true;
        }

        if (status == STATUS_PAR && berryId == 1)
        {
            pokemob.healStatus();
            return true;
        }
        if (status == STATUS_SLP && berryId == 2)
        {
            pokemob.healStatus();
            return true;
        }
        if ((status == STATUS_PSN || status == STATUS_PSN2) && berryId == 3)
        {
            pokemob.healStatus();
            return true;
        }
        if (status == STATUS_BRN && berryId == 4)
        {
            pokemob.healStatus();
            return true;
        }
        if (status == STATUS_FRZ && berryId == 5)
        {
            pokemob.healStatus();
            return true;
        }
        if (status != STATUS_NON && berryId == 9)
        {
            pokemob.healStatus();
            return true;
        }
        EntityLivingBase entity = pokemob.getEntity();
        float HP = entity.getHealth();
        float HPmax = entity.getMaxHealth();

        boolean apply = (berryId == 7 || berryId == 10 || berryId == 60) && user instanceof EntityPlayer && HP != HPmax
                || HP < HPmax / 3;
        if (apply)
        {
            if (berryId == 7)
            {
                entity.heal(10);
                return true;
            }
            else if (berryId == 10 || berryId == 60)
            {
                entity.heal(HPmax / 4f);
                return true;
            }
        }
        return false;
    }

    public static boolean applyEffect(IPokemob pokemob, EntityLivingBase user, ItemStack stack)
    {
        boolean applied = berryEffect(pokemob, user, stack);
        int[] flavours = BerryManager.berryFlavours.get(stack.getItemDamage());
        if (applied && flavours != null)
        {
            for (int i = 0; i < 5; i++)
            {
                pokemob.setFlavourAmount(i, pokemob.getFlavourAmount(i) + flavours[i]);
            }
        }
        boolean useStack = applied;
        if (useStack && user instanceof EntityPlayer && ((EntityPlayer) user).capabilities.isCreativeMode)
            useStack = false;
        if (useStack) stack.splitStack(1);
        return applied;
    }
}
