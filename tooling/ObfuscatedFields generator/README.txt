This tool automatically generates a new ObfuscatedFields.java based on a mappings file.

ObfuscatedFields_template.java must be formatted like this:
```
    // corresponds to net.minecraft.CLASS#FIELD_OR_METHOD
    public static final String FIELDNAME = "%placeholder%";
```

Drop a Mojang mappings (*.txt) into this folder and run the script, and it will create "ObfuscatedFields_MAPPINGNAME.java"
