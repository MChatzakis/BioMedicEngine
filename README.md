# BioMedicEngine
BioMedicEngine is a vector-model based search engine over a BioMedical document collection.

Manos Chatzakis (emmanouil.chatzakis@epfl.ch)

## How to run
BioMedicEngine is a command line application.

### Index Creation
To index a medical file directory, use: 
```shell
mkdir ${COLLECTION_OUTPUT_DIRECTORY}partialIndexing
java -jar $EXEJAR_NAME -mode indexer -input $COLLECTION_INPUT_DIRECTORY -output $COLLECTION_OUTPUT_DIRECTORY -gr $GR_STOPWORDS -en $EN_STOPWORDS
```

### Query Answering
For simple query answering using the vector model implementation, run:
```shell
java -jar $EXEJAR_NAME -mode retriever -collection $BIG_COLLECTION_OUTPUT_DIRECTORY -gr $GR_STOPWORDS -en $EN_STOPWORDS
```

For medical-type weighted query answering, run:
```shell
java -jar $EXEJAR_NAME -mode topicRetriever -collection $BIG_COLLECTION_OUTPUT_DIRECTORY -gr $GR_STOPWORDS -en $EN_STOPWORDS
```

## Run Automation
The following bash script file is provided to automate the run, located in BiomedicEngine/bash/run.sh:
```shell
EXEJAR_NAME=../target/BioMedicEngine-1.0-SNAPSHOT-exejar.jar
COLLECTION_INPUT_DIRECTORY=../sample/
COLLECTION_OUTPUT_DIRECTORY=/mnt/c/Users/manos/Desktop/simple_example/
BIG_COLLECTION_OUTPUT_DIRECTORY=/mnt/c/BioMedicIndexer_2/
GR_STOPWORDS=../stopwords/stopwordsGr.txt
EN_STOPWORDS=../stopwords/stopwordsEn.txt

mkdir ${COLLECTION_OUTPUT_DIRECTORY}partialIndexing
java -jar $EXEJAR_NAME -mode indexer -input $COLLECTION_INPUT_DIRECTORY -output $COLLECTION_OUTPUT_DIRECTORY -gr $GR_STOPWORDS -en $EN_STOPWORDS

#java -jar $EXEJAR_NAME -mode retriever -collection $BIG_COLLECTION_OUTPUT_DIRECTORY -gr $GR_STOPWORDS -en $EN_STOPWORDS

java -jar $EXEJAR_NAME -mode topicRetriever -collection $BIG_COLLECTION_OUTPUT_DIRECTORY -gr $GR_STOPWORDS -en $EN_STOPWORDS
```

## Experimental Evaluation
The experimental evaluation contacted on a machine of 8 cores and 16GB memory. The results are presented in the pdf report file demonstrating query answering times for a selected collection of queries, accuracy measuserements based on BPREF and other performance evaluation components, such as index creation time and memory usage.

## About
This project was the final assignment of the undergraduate/graduate course cs463-Information Retrieval Systems, taught at Computer Science Department of University of Crete.
