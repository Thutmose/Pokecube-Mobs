package pokecube.core.moves.implementations.attacks.special;

import net.minecraft.entity.EntityLiving;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.moves.templates.Move_Basic;

public class MoveWhirlwind extends Move_Basic
{

    public MoveWhirlwind()
    {
        super("whirlwind");
    }

    @Override
    public void postAttack(MovePacket packet)
    {
        super.postAttack(packet);
        if (packet.canceled || packet.failed) return;
        IPokemob attacked = CapabilityPokemob.getPokemobFor(packet.attacked);
        if (attacked != null)
        {
            if (attacked.getLevel() > packet.attacker.getLevel())
            {
                // TODO message here for move failing;
                return;
            }
            attacked.setPokemonAIState(IMoveConstants.ANGRY, false);
            if (attacked.getPokemonAIState(IMoveConstants.TAMED)) attacked.returnToPokecube();
        }
        // ends the battle
        if (packet.attacked instanceof EntityLiving)
        {
            ((EntityLiving) packet.attacked).setAttackTarget(null);
        }
        ((EntityLiving) packet.attacker.getEntity()).setAttackTarget(null);
        packet.attacker.setPokemonAIState(IMoveConstants.ANGRY, false);
    }
}
