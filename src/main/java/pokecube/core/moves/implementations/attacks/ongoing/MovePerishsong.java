package pokecube.core.moves.implementations.attacks.ongoing;

import net.minecraft.entity.EntityLiving;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.moves.templates.Move_Ongoing;

public class MovePerishsong extends Move_Ongoing
{

    public MovePerishsong()
    {
        super("perishsong");
    }

    @Override
    public void doOngoingEffect(EntityLiving mob)
    {
        Move_Ongoing move = this;
        IPokemob pokemob = CapabilityPokemob.getPokemobFor(mob);
        if (pokemob != null)
        {
            int duration = pokemob.getOngoingEffects().get(move);
            if (duration == 0)
            {
                mob.setHealth(0);
            }
            // TODO perish counter here.
        }
        else
        {
            // TODO Insert code for an on-screen message here.
        }
    }

    @Override
    public int getDuration()
    {
        return 3;
    }

    @Override
    public boolean onSource()
    {
        return true;
    }

}
