#!/bin/bash

RUNTIME=30
PAYLOAD_SIZE=0

rm -if results.txt

for THREADS in 1 2 4 8 16 32 64 128 256 512
do
    echo $THREADS "threads" $RUNTIME "s" $PAYLOAD_SIZE "bytes/msg" | tee results.txt
    bin/start-client.sh $1 $THREADS $RUNTIME $PAYLOAD_SIZE | tee results.txt
done