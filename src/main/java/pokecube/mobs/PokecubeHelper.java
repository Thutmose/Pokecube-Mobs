package pokecube.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.utils.PokeType;

public class PokecubeHelper
{

    public double dive(IPokemob mob)
    {
        double x = 1;
        Entity entity = (Entity) mob;
        if (entity.getEntityWorld().getBlockState(entity.getPosition()).getBlock() == Blocks.WATER
                && mob.getType1() == PokeType.getType("water"))
        {
            x = 3.5;
        }
        if (entity.getEntityWorld().getBlockState(entity.getPosition()).getBlock() == Blocks.WATER
                && mob.getType2() == PokeType.getType("water"))
        {
            x = 3.5;
        }
        return x;
    }

    public double dusk(IPokemob mob)
    {
        double x = 1;
        Entity entity = (Entity) mob;
        int light = entity.getEntityWorld().getLight(entity.getPosition());
        if (light < 5)
        {
            x = 3.5;
        }
        return x;
    }

    public double nest(IPokemob mob)
    {
        double x = 1;
        if (mob.getLevel() < 20)
        {
            x = 3;
        }
        if (mob.getLevel() > 19 && mob.getLevel() < 30)
        {
            x = 2;
        }
        return x;
    }

    public double net(IPokemob mob)
    {
        double x = 1;
        if (mob.getType1() == PokeType.getType("bug"))
        {
            x = 2;
        }
        if (mob.getType1() == PokeType.getType("water"))
        {
            x = 2;
        }
        if (mob.getType2() == PokeType.getType("bug"))
        {
            x = 2;
        }
        if (mob.getType2() == PokeType.getType("water"))
        {
            x = 2;
        }
        return x;
    }

    public double quick(IPokemob mob)
    {
        double x = 1;
        Entity entity = (Entity) mob;
        double alive = entity.ticksExisted;
        if (mob.getPokemonAIState(IMoveConstants.ANGRY) == false && alive < 601)
        {
            x = 4;
        }
        return x;
    }

    public double timer(IPokemob mob)
    {
        double x = 1;
        Entity entity = (Entity) mob;
        double alive = entity.ticksExisted;
        if (alive > 1500 && alive < 3001)
        {
            x = 2;
        }
        if (alive > 3000 && alive < 4501)
        {
            x = 3;
        }
        if (alive > 4500)
        {
            x = 4;
        }
        return x;
    }
}
