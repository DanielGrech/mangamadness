#!/bin/bash

# ----- Config -----
SERIES_CONSUMERS=2
CHAPTER_CONSUMERS=8
PAGE_CONSUMERS=16
IMAGE_CONSUMERS=32

START_TIME=`date +%s`
START_TIME_HUMAN=`date`
BASE_PATH="../source"

echo "Update starting at $START_TIME ($START_TIME_HUMAN)"

echo "Pumping series from db into beanstalk"
# BASE_PATH/series_update_producer.py

echo "Starting $SERIES_CONSUMERS series consumers"
for (( i=0; i<$SERIES_CONSUMERS; i++ ))
do
   echo "Welcome $i times"
done