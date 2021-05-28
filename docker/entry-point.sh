#!/bin/bash

# start ssh daemon
service ssh start

# clear hadoop logs
rm -rf $HADOOP_HOME/logs/*

# format the filesystem
$HADOOP_HOME/bin/hdfs namenode -format

# start HDFS
$HADOOP_HOME/sbin/start-dfs.sh

# start YARN
$HADOOP_HOME/sbin/start-yarn.sh

# start history server
$HADOOP_HOME/sbin/mr-jobhistory-daemon.sh start historyserver

sleep 1

if [[ $1 == "-d" ]]; then
  while true; do sleep 1000; done
  # tail log directory
  tail -n 1000 -F $HADOOP_HOME/logs/*.log $NUTCH_HOME/logs/*.log
fi

if [[ $1 == "-bash" ]]; then
  /bin/bash
fi
