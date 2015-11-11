# UltraCosmetics
UltraCosmetics is a Spigot 1.8.8 plugin which adds cool cosmetics for your server hub.

You must use Spigot, and not CraftBukkit for this plugin!

THE (“UltraCosmetics”) SOURCE IS PROVIDED AS IS AT NO MONETARY COST FOR PERSONAL USE ONLY. ANY COMMERCIAL DISTRIBUTION/USE OF THE (“UltraCosmetics”) SOURCE IS STRICTLY PROHIBITED.

Adding a new Cosmetics is really simple.
Create the GadgetType/PetType/etc.. of your cosmetic in the Gadget.class/Pet.class/etc.. with the permission and the config path name.

Create the file extending Gadget/Pet/etc... and make your gadget. (you can have a look to other gadgets if you don't
know how to do.

Register it in Core with gadgetList/petList/etc.add(new YourClass(null));

Done! :player
