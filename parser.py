from html.parser import HTMLParser
from urllib.request import urlopen
import xml.etree.ElementTree as ET
from xml.etree.ElementTree import Element

#Start url
url = ['http://www.cse.ust.hk']
#Parsed urls
parsed = []

#Main html fetch class
class MyHTMLParser(HTMLParser):

    def handle_starttag(self, tag, attrs):
        #find tags with url links <a href="">
        if tag == 'a' and len(attrs) != 0 and attrs[0][0] == 'href':
            #Get the link content of the tag
            link = attrs[0][1]
            link = 'http://www.cse.ust.hk' + link if not 'http' in link else link
            url.append(link)

    def handle_endtag(self, tag):
        pass

    def handle_data(self, data):
        pass

print('Fetching url')

#modify to change the number of pages to crawl, the project requires 30
while len(parsed) < 30 or True:
    link = url.pop(0)
    parsed.append(link)
    print(link)
    try:
        #Request html content
        response = urlopen(link, timeout = 10)
        html = response.read()
        #Parse the html and look for hyperlink tags, then append to the list of urls to parse in BFS manner
        if html != '':
            parser = MyHTMLParser()
            parser.feed(str(html))
    except:
        print('\rError in link: ', link)
