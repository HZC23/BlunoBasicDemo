
import re

file_path = "c:\\Users\\Hadrien\\Documents\\Arduino\\NoNo\\Nono Controller\\app\\src\\main\\java\\com\\hzc\\nonocontroller\\BlunoLibrary.java"

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace literal \r\n with actual \r\n
content = content.replace("\\\\r\\\\n", "\r\n")
# Replace literal \" with actual \"
content = content.replace("\\\\\"", "\"")
# Replace literal \t with actual \t
content = content.replace("\\\\t", "\t")

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Replacements complete.")
