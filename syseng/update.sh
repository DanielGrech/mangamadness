#!/bin/bash

# ----- Config -----
SERIES_CONSUMERS=2
CHAPTER_CONSUMERS=8
PAGE_CONSUMERS=16
IMAGE_CONSUMERS=32

# ----- Paths -----
BASE_PATH="../source"
LOGS_DIR="./logs"

echo "Update starting at $(date +%s) $(date)"

echo "Pumping series from db into beanstalk"
BASE_PATH/series_update_producer.py

echo "Starting $SERIES_CONSUMERS series consumers"
for (( i=0; i<$SERIES_CONSUMERS; i++ ))
do
   LOG_NAME="series_consumer-$(date +%s)-$i.txt"
   BASE_PATH/series_update_consumer  2>&1 | tee $LOG_NAME &
done

echo "Starting $CHAPTER_CONSUMERS chapter consumers"
for (( i=0; i<$CHAPTER_CONSUMERS; i++ ))
do
   LOG_NAME="chapter_consumer-$(date +%s)-$i.txt"
   BASE_PATH/chapter_update_consumer  2>&1 | tee $LOG_NAME &
done

echo "Starting $PAGE_CONSUMERS chapter consumers"
for (( i=0; i<$PAGE_CONSUMERS; i++ ))
do
   LOG_NAME="page_consumer-$(date +%s)-$i.txt"
   BASE_PATH/pages_update_consumer  2>&1 | tee $LOG_NAME &
done

echo "Starting $IMAGE_CONSUMERS image consumers"
for (( i=0; i<$CHAPTER_CONSUMERS; i++ ))
do
   LOG_NAME="image_consumer-$(date +%s)-$i.txt"
   BASE_PATH/image_download_consumer  2>&1 | tee $LOG_NAME &
done

echo ""
echo ""
echo ""
echo "All consumers started .. now we play the waiting game..."
echo ""
echo ""
echo ""
wait


# TODO: Run source --> production migration (only if everything is ok?!)