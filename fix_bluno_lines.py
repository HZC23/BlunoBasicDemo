import os

file_path = "c:\\Users\\Hadrien\\Documents\\Arduino\\NoNo\\Nono Controller\\app\\src\\main\\java\\com\\hzc\\nonocontroller\\BlunoLibrary.java"
temp_file_path = "c:\\Users\\Hadrien\\Documents\\Arduino\\NoNo\\Nono Controller\\app\\src\\main\\java\\com\\hzc\\nonocontroller\\BlunoLibrary_temp.java"

with open(file_path, 'r', encoding='utf-8') as f_read:
    lines = f_read.readlines()

modified_lines = []
for line in lines:
    # Replace literal \r with actual \r
    line = line.replace("\\r", "\r")
    # Replace literal \n with actual \n
    line = line.replace("\\n", "\n")
    # Replace literal \" with actual "
    line = line.replace("\\\"", "\"")
    # Replace literal \t with actual \t
    line = line.replace("\\t", "\t")
    
    # Replace specific problematic unicode escape sequences with empty string
    line = line.replace("\\u00e9\\u009c\\u0080\\u00e8\\u00a6\\u0081\\u00e7\\u0094\\u00b3\\u00e8\\u00af\\u00b7\\u00e7\\u009a\\u0084\\u00e6\\u009d\\u0083\\u00e9\\u0099\\u0090", "")
    line = line.replace("\\u00e8\\u00af\\u00b7\\u00e6\\u00b1\\u0082\\u00e5\\u008d\\u0095\\u00e4\\u00b8\\u00aa\\u00e6\\u009d\\u0083\\u00e9\\u0099\\u0090", "")
    line = line.replace("\\u00e5\\u0088\\u00a4\\u00e6\\u0096\\u00ad\\u00e6\\u0089\\u0080\\u00e6\\u009c\\u0089\\u00e6\\u009d\\u0e99\\u00e9\\u0099\\u0090", "")
    line = line.replace("\\u00e6\\u009d\\u0083\\u00e9\\u0099\\u0090\\u00e8\\u00af\\u00b7\\u00e6\\uce\\u00b2\\u00e5\\u00a4\\u00b1\\u00e8", "")

    modified_lines.append(line)

with open(temp_file_path, 'w', encoding='utf-8') as f_write:
    f_write.writelines(modified_lines)

os.replace(temp_file_path, file_path)
print("Replacements complete.")
