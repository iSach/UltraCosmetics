#!/usr/bin/python3
import os
import re

marker_pattern = re.compile("^\s+// corresponds to (net\.minecraft\.[\w\.]+)#(\w+)")
class_pattern = re.compile("^(net\.minecraft\.[\w\.]+) ->")
method_pattern = re.compile("^\s+(?:\d+:\d+:)?[\w\.]+ (\w+)\(?\)? -> (\w+)")
template = open("ObfuscatedFields_template.java")

def generate_fields(file):
    out_fields = open("ObfuscatedFields_" + file.replace(".txt", "") + ".java", "w")
    maps = open(file)
    template_iter = iter(template)
    for template_line in template_iter:
        out_fields.write(template_line)
        m = marker_pattern.search(template_line)
        if not m:
            continue
        search_class = m.group(1)
        search_field = m.group(2)
        fixed_line = next(template_iter)
        maps.seek(0)
        found_class = False
        for line in maps:
            if not found_class:
                class_line = class_pattern.match(line)
                if not class_line or class_line.group(1) != search_class:
                    continue
                print("Found matching class " + search_class)
                found_class = True
            else:
                method_line = method_pattern.match(line)
                if not method_line or method_line.group(1) != search_field:
                    continue
                fixed_line = fixed_line.replace("%placeholder%", method_line.group(2))
                print("Found matching field " + search_field)
                print(fixed_line)
                break
        out_fields.write(fixed_line)


for file in os.listdir('.'):
    if os.path.isdir(file):
        continue
    if not file.endswith(".txt") or file == "README.txt":
        continue
    generate_fields(file)
