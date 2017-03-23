Crawler now gives 4 tables which are stored in indexDB:

Inverted Index
Forward Index
Word => Word-ID mapping
URL => URL-ID mapping


To crawl the websites, simply run Crawler
e.g. java [link libraries] Crawler

To view table data, run Database with 1 argument, which is one of the hashtable names:
"inverted" -- Inverted Index table
"forward"  -- Forward Index table
"word"	   -- Word => Word-ID table
"link"	   -- URL => URL-ID table

e.g.java [link libraries] Database inverted
