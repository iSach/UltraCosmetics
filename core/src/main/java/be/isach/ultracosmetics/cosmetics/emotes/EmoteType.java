package be.isach.ultracosmetics.cosmetics.emotes;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.cosmetics.emotes
 * Created by: Sacha
 * Created on: 17th June, 2016
 * at 02:45
 */
public class EmoteType {

    public static final EmoteType TEST = new EmoteType();
    public static final EmoteType TEST_1 = new EmoteType();
    public static final EmoteType TEST_2 = new EmoteType();
    public static final EmoteType TEST_3 = new EmoteType();

    static {
        TEST.addTextureToAnimation("0");
        TEST.addTextureToAnimation("1");
        TEST.addTextureToAnimation("2");
        TEST.addTextureToAnimation("3");

        TEST_1.addTextureToAnimation("0");
        TEST_1.addTextureToAnimation("1");
        TEST_1.addTextureToAnimation("2");
        TEST_1.addTextureToAnimation("3");

        TEST_2.addTextureToAnimation("0");
        TEST_2.addTextureToAnimation("1");
        TEST_2.addTextureToAnimation("2");
        TEST_2.addTextureToAnimation("3");

        TEST_3.addTextureToAnimation("0");
        TEST_3.addTextureToAnimation("1");
        TEST_3.addTextureToAnimation("2");
        TEST_3.addTextureToAnimation("3");
    }

    public void addTextureToAnimation(String texture) {
        //...
    }

}
