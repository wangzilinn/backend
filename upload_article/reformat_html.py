from bs4 import BeautifulSoup

with open(r"cache\Java多线程学习笔记.html", 'r', encoding="gb2312") as file:
    original_file = file.read()

sp = BeautifulSoup(original_file, 'lxml')
clear_tag_list = ["h1", "h2", "h3", "h4", "h5"]
for clear_tag in clear_tag_list:
    for tag in sp.find_all(clear_tag):
        new_tag = sp.new_tag(clear_tag)
        new_tag.string = tag.get_text()
        tag.replace_with(new_tag)

with open(r"cache\result.html", 'w', encoding="utf-8") as fp:
    # write the current soup content
    fp.write(str(sp))