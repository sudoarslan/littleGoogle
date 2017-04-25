## Indexer

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

### MapTable data

To view mapTable data, run `Database` with **1 or 2 argument**, which is one of the hashtable names and the direction:

+ `word`: Word-ID => Word table
+ `word backward`: Word => Word-ID table 
+ `url`: URL-ID => URL table
+ `url backward`: URL => URL-ID table

`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Database word backward` 

### Tester program

To generate the crawling result, simply run `Test`

`java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Test` 

A text file `spider_result.txt` will be generated with the required format


##Indexer

The indexer folder contains web crawler, indexer and database insertion functionalities.
Changes are still required depending on the course requirements.
To compile and run the entire process:
~~~
cd indexer
javac -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar *.java IRUtilities/*.java
java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Crawler
~~~

To compile and run the test program:
~~~
cd indexer
javac -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar *.java IRUtilities/*.java
java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Test
~~~
The result file is named "spider_result.txt".

To compile and run the querier:
~~~
cd indexer
javac -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar *.java IRUtilities/*.java
java -cp htmlparser1_6/lib/htmlparser.jar:jdbm-1.0/lib/jdbm-1.0.jar:. Querier <query phrase>







