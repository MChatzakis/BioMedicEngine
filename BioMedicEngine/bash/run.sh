#!/bin/bash

EXEJAR_NAME=../target/BioMedicEngine-1.0-SNAPSHOT-exejar.jar
COLLECTION_INPUT_DIRECTORY=../sample/
COLLECTION_OUTPUT_DIRECTORY=/mnt/c/Users/manos/Desktop/simple_example/
BIG_COLLECTION_OUTPUT_DIRECTORY=/mnt/c/BioMedicIndexer_2/
GR_STOPWORDS=../stopwords/stopwordsGr.txt
EN_STOPWORDS=../stopwords/stopwordsEn.txt

#Indexer
mkdir ${COLLECTION_OUTPUT_DIRECTORY}partialIndexing
java -jar $EXEJAR_NAME -mode indexer -input $COLLECTION_INPUT_DIRECTORY -output $COLLECTION_OUTPUT_DIRECTORY -gr $GR_STOPWORDS -en $EN_STOPWORDS

#Retriever
#java -jar $EXEJAR_NAME -mode retriever -collection $BIG_COLLECTION_OUTPUT_DIRECTORY -gr $GR_STOPWORDS -en $EN_STOPWORDS

#Topic Retriever
#java -jar $EXEJAR_NAME -mode topicRetriever -collection $BIG_COLLECTION_OUTPUT_DIRECTORY -gr $GR_STOPWORDS -en $EN_STOPWORDS
