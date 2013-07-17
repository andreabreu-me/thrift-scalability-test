#!/bin/bash

# pass in remote server invocation command via second arg
# e.g., java -jar /home/ubuntu/thrifttest/server/target/thrifttest-server-1.0-SNAPSHOT.jar -XX:+UseParallelGC -Xms15G -Xmx15G -XX:+PrintGCDetails 

RUNTIME=30
PAYLOAD_SIZE=0

rm -if results.txt

for THREADS in 1 2 4 8 16 32 64 128 256 512
do
    ssh $1 $2 &
    echo $THREADS "threads" $RUNTIME "s" $PAYLOAD_SIZE "bytes/msg" | tee results.txt
    bin/start-client.sh $1 $THREADS $RUNTIME $PAYLOAD_SIZE | tee results.txt
    kill -9 $!
done