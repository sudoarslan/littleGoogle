# littleGoogle
Web interface is based on Spark (Java8)
The project is using Maven to manage dependencies

##For more info about Spark http://sparkjava.com/documentation.html

Run the Java program and visit http://localhost:8000/home.html to access Little Google

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
