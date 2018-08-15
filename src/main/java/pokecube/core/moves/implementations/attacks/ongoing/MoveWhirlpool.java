package pokecube.core.moves.implementations.attacks.ongoing;

import java.util.Random;

import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.interfaces.entity.IOngoingAffected;
import pokecube.core.interfaces.entity.IOngoingAffected.IOngoingEffect;
import pokecube.core.moves.templates.Move_Ongoing;
import pokecube.core.utils.PokeType;

public class MoveWhirlpool extends Move_Ongoing
{

    public MoveWhirlpool()
    {
        super("whirlpool");
    }

    @Override
    public void doOngoingEffect(IOngoingAffected mob, IOngoingEffect effect)
    {
        IPokemob pokemob = CapabilityPokemob.getPokemobFor(mob.getEntity());
        if (pokemob != null && pokemob.isType(PokeType.getType("ghost"))) return;
        super.doOngoingEffect(mob, effect);
    }

    public int getDuration()
    {
        Random r = new Random();
        return 2 + r.nextInt(4);
    }

}
