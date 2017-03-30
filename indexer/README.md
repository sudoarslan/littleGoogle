#Indexer

To compile everything, run

`javac -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar *.java IRUtilities/*.java` 

### Crawler

To crawl the websites, simply run `Crawler`

`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Crawler` 

### Index Data

To view index data, run `Database` with **1 argument**, which is one of the
hashtable names: 

+ `inverted`: Inverted Index table
+ `forward`: Forward Index table
+ `link`: Child links table

`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Database inverted`
`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Database forward`
`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Database link`

### MapTable data

To view mapTable data, run `Database` with **1 or 2 argument**, which is one of the hashtable names and the direction:

+ `word`: Word-ID => Word table
+ `word backward`: Word => Word-ID table 
+ `url`: URL-ID => URL table
+ `url backward`: URL => URL-ID table

`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Database word backward`
`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Database word forward`
`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Database url backward`
`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Database url forward`
