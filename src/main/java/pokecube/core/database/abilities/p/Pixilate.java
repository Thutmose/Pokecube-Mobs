package pokecube.core.database.abilities.p;

import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.utils.PokeType;

public class Pixilate extends Ability
{
    @Override
    public void onMoveUse(IPokemob mob, MovePacket move)
    {
        if (!move.pre) return;
        if (move.attackType == PokeType.getType("normal") && mob == move.attacker)
        {
            move.attackType = PokeType.getType("fairy");
        }
    }
}
