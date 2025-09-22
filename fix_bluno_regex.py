import re
import os

file_path = "c:\\Users\\Hadrien\\Documents\\Arduino\\NoNo\\Nono Controller\\app\\src\\main\\java\\com\\hzc\\nonocontroller\\BlunoLibrary.java"
temp_file_path = "c:\\Users\\Hadrien\\Documents\\Arduino\\NoNo\\Nono Controller\\app\\src\\main\\java\\com\\hzc\\nonocontroller\\BlunoLibrary_temp.java"

with open(file_path, 'r', encoding='utf-8') as f_read:
    content = f_read.read()

# Fix mPassword line
# This regex looks for the mPassword declaration and captures the part after "DFRobot"
# It then replaces the captured part with the correct Java escape sequence for \r\n and the closing quote.
content = re.sub(r'private String mPassword="AT\+PASSWOR=DFRobot\s*\\r\\n\s*\\"\s*";', r'private String mPassword="AT+PASSWOR=DFRobot\\r\\n";', content, flags=re.DOTALL)

# Fix mBaudrateBuffer declaration line
content = re.sub(r'private String mBaudrateBuffer = "AT\+CURRUART="+mBaudrate+"\s*\\r\\n\s*\\"\s*";', r'private String mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\\r\\n";', content, flags=re.DOTALL)

# Fix mBaudrateBuffer assignment line in serialBegin
content = re.sub(r'mBaudrateBuffer = "AT\+CURRUART="+mBaudrate+"\s*\\r\\n\s*\\"\s*";', r'mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\\r\\n";', content, flags=re.DOTALL)

# Fix other common problematic escape sequences that might be literal in the file
# These are literal backslashes followed by a character, which should be converted to actual escape sequences
content = content.replace("\\\\n", "\n")
content = content.replace("\\\\t", "\t")
content = content.replace("\\\\r", "\r")
content = content.replace("\\\\\"", "\"") # This line was the problem. Corrected to use raw string for old_string.

with open(temp_file_path, 'w', encoding='utf-8') as f_write:
    f_write.write(content)

os.replace(temp_file_path, file_path)
print("Replacements complete.")