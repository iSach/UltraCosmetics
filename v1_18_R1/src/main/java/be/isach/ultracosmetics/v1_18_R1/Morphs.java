package be.isach.ultracosmetics.v1_18_R1;

import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.v1_18_R1.morphs.MorphElderGuardian;
import be.isach.ultracosmetics.version.IMorphs;

/**
 * @author RadBuilder
 */
public class Morphs implements IMorphs {
    @Override
    public Class<? extends Morph> getElderGuardianClass() {
        return MorphElderGuardian.class;
    }
}
