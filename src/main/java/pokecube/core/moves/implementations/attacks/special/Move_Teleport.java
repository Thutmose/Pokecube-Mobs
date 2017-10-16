package pokecube.core.moves.implementations.attacks.special;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import pokecube.core.events.handlers.EventsHandler;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.moves.templates.Move_Basic;
import pokecube.core.network.PokecubePacketHandler;
import pokecube.core.network.PokecubePacketHandler.PokecubeClientPacket;

public class Move_Teleport extends Move_Basic
{
    public Move_Teleport()
    {
        super("teleport");
    }

    @Override
    public void postAttack(MovePacket packet)
    {
        IPokemob attacker = packet.attacker;
        Entity attacked = packet.attacked;
        Entity target = attacker.getEntity().getAttackTarget();
        if (attacked == attacker.getEntity() && target != null) attacked = target;
        IPokemob attackedMob = CapabilityPokemob.getPokemobFor(attacked);
        boolean angry = attacker.getPokemonAIState(IMoveConstants.ANGRY);
        if (attacked instanceof EntityLiving)
        {
            ((EntityLiving) attacked).setAttackTarget(null);
        }
        if (attackedMob != null)
        {
            attackedMob.setPokemonAIState(IMoveConstants.ANGRY, false);
            attackedMob.getEntity().setAttackTarget(null);
        }
        if (attacker.getPokemonAIState(IMoveConstants.TAMED) && !angry)
        {
            if ((target == null && packet.attacked == null) || (packet.attacked == packet.attacker))
            {
                if (attacker.getPokemonOwner() instanceof EntityPlayer && attacker.getEntity().isServerWorld())
                {
                    EventsHandler.recallAllPokemobsExcluding((EntityPlayer) attacker.getPokemonOwner(),
                            (IPokemob) null);
                    PokecubeClientPacket mess = new PokecubeClientPacket(
                            new byte[] { PokecubeClientPacket.TELEPORTINDEX });
                    PokecubePacketHandler.sendToClient(mess, (EntityPlayer) attacker.getPokemonOwner());
                }
            }
        }
        super.postAttack(packet);
        attacker.setPokemonAIState(IMoveConstants.ANGRY, false);
        attacker.getEntity().setAttackTarget(null);
    }
}
