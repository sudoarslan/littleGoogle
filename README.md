# littleGoogle

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
