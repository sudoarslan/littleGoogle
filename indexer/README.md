Crawler now gives 5 tables which are stored in indexDB:

To crawl the websites, simply run Crawler
e.g. java [link libraries] Crawler

To view index data, run Database with 1 argument, which is one of the hashtable names:
"inverted" -- Inverted Index table
"forward"  -- Forward Index table
"link"	   -- Child links table

To view mapTable data, run Database with 2 argument, which is one of the hashtable names and the direction:
"word"	   		-- Word-ID => Word table
"word backward" -- Word => Word-ID table
"url"	   		-- URL-ID => URL table
"url backward"  -- URL => URL-ID table

e.g. java [link libraries] Database url backward
	 java [link libraries] Database link
