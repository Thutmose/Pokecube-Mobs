package pokecube.adventures.legends.conditions;

import net.minecraft.entity.Entity;
import pokecube.adventures.legends.Condition;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.stats.CaptureStats;
import pokecube.core.database.stats.KillStats;
import pokecube.core.database.stats.SpecialCaseRegister;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.utils.PokeType;

public class Entei extends Condition
{
    @Override
    public boolean canCapture(Entity trainer, IPokemob pokemon)
    {
        if (!canCapture(trainer)) return false;
        int count1 = CaptureStats.getUniqueOfTypeCaughtBy(trainer.getUniqueID(), PokeType.getType("fire"));
        int count2 = SpecialCaseRegister.countSpawnableTypes(PokeType.getType("fire"));
        if (((double) count1) / ((double) count2) >= 0.5) { return true; }
        if (pokemon != null && !trainer.getEntityWorld().isRemote)
        {
            sendNoTrust(trainer);
        }
        return false;
    }

    @Override
    public boolean canSpawn(Entity trainer)
    {
        if (CaptureStats.getTotalNumberOfPokemobCaughtBy(trainer.getUniqueID(), getEntry()) > 0) return false;
        if (KillStats.getTotalNumberOfPokemobKilledBy(trainer.getUniqueID(), getEntry()) > 0) return false;
        return true;
    }

    @Override
    public PokedexEntry getEntry()
    {
        return Database.getEntry("entei");
    }

}
