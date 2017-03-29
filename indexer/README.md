Crawler now gives 5 tables which are stored in indexDB:

### Crawler

To crawl the websites, simply run `Crawler`

e.g.`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:.
Crawler`


### Index Data

To view index data, run `Database` with **1 argument**, which is one of the
hashtable names: 

+ `inverted`: Inverted Index table
+ `forward`: Forward Index table
+ `link`: Child links table

e.g.`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:.
Database inverted`


### MapTable data

To view mapTable data, run `Database` with **1 or 2 argument**, which is one of the hashtable names and the direction:

+ `word`: Word-ID => Word table
+ `word backward`: Word => Word-ID table 
+ `url`: URL-ID => URL table
+ `url backward`: URL => URL-ID table

e.g.`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:.
Database word backward`
