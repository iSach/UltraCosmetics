package be.isach.ultracosmetics.v1_9_R2;

import be.isach.ultracosmetics.v1_9_R2.morphs.MorphElderGuardian;
import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.version.IMorphs;

public class Morphs implements IMorphs{
    @Override
    public Class<? extends Morph> getElderGuardianClass() {
        return MorphElderGuardian.class;
    }
}
