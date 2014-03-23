mongo-spark
===========
This is forked off of original work by plaa/mongo-spark
The changes here relates to supporting use of direct .bson file as input to spark, this
was needed to conform to RDD requirement of an InputFormat as FileInputFormat<K,V> 

Why BSON, as BSON format is quite compressed compared to CSV and other formats.

The default BSONFileInputFormat in mongo-hadoop connector implements FileInputStream.
BSONFileInputFormatGeneric adapts it for spark.

Note, I have only tested Scala runtime with this change.

ScalaWordCountBSON.scala example:


To Run BSON version run ScalaWordCountBSON.scala
Use following steps to use BSON format

mongoimport -d beowulf -c input beowulf.json
#backup input to bson
mongodump -d beowulf -c input -o beowulf
#run BSON version
   sbt 'run-main ScalaWordCountBSON'
#restore output bson to mongo collectiont testoutput
   mongorestore -d beowulf -c testoutput ./beowulf/testoutput.bson/part-r-00000.bson 
#assert
   mongo beowulf --eval 'printjson(db.testoutput.find().toArray())'





Example application on how to use [mongo-hadoop][1] connector with [Apache Spark][2].

Read more details at http://codeforhire.com/2014/02/18/using-spark-with-mongodb/

[1]: https://github.com/mongodb/mongo-hadoop
[2]: https://spark.incubator.apache.org/


Prerequisites
-------------

* MongoDB installed and running on localhost
* Scala 2.10 and SBT installed


Running
-------

Import data into the database, run either `JavaWordCount` or `ScalaWordCount` and print the results.

    mongoimport -d beowulf -c input beowulf.json
    sbt 'run-main JavaWordCount'
    sbt 'run-main ScalaWordCount'
    mongo beowulf --eval 'printjson(db.output.find().toArray())' | less


License
-------

The code itself is released to the public domain according to the [Creative Commons CC0][3].

The example files are based on [Beowulf][4] from Project Gutenberg and is under its corresponding license.

[3]: http://creativecommons.org/publicdomain/zero/1.0/
[4]: http://www.gutenberg.org/ebooks/981
