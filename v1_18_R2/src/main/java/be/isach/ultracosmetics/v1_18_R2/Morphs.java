package be.isach.ultracosmetics.v1_18_R2;

import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.v1_18_R2.morphs.MorphElderGuardian;
import be.isach.ultracosmetics.version.IMorphs;

/**
 * @author RadBuilder
 */

// TODO: do we need a whole class for this? maybe even merge with Pets?

public class Morphs implements IMorphs {
    @Override
    public Class<? extends Morph> getElderGuardianClass() {
        return MorphElderGuardian.class;
    }
}
