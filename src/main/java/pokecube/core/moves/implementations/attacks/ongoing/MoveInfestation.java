package pokecube.core.moves.implementations.attacks.ongoing;

import pokecube.core.interfaces.entity.IOngoingAffected;
import pokecube.core.interfaces.entity.IOngoingAffected.IOngoingEffect;
import pokecube.core.moves.templates.Move_Ongoing;

public class MoveInfestation extends Move_Ongoing
{

    public MoveInfestation()
    {
        super("infestation");
    }

    @Override
    public void doOngoingEffect(IOngoingAffected mob, IOngoingEffect effect)
    {
        float thisMaxHP = mob.getEntity().getMaxHealth();
        int damage = Math.max(1, (int) (0.125 * thisMaxHP));
        damageTarget(mob.getEntity(), null, damage);
    }
}
