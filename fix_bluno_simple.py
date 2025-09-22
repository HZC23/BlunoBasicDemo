import os

file_path = "c:\\Users\\Hadrien\\Documents\\Arduino\\NoNo\\Nono Controller\\app\\src\\main\\java\\com\\hzc\\nonocontroller\\BlunoLibrary.java"
temp_file_path = "c:\\Users\\Hadrien\\Documents\\Arduino\\NoNo\\Nono Controller\\app\\src\\main\\java\\com\\hzc\\nonocontroller\\BlunoLibrary_temp.java"

with open(file_path, 'r', encoding='utf-8') as f_read:
    content = f_read.read()

# Replace all occurrences of \ with \
content = content.replace("\\\\", "\\")

# Replace all occurrences of " with "
content = content.replace("\\\"", "\"")

with open(temp_file_path, 'w', encoding='utf-8') as f_write:
    f_write.write(content)

os.replace(temp_file_path, file_path)
print("Replacements complete.")
