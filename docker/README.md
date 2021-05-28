# Nutch Hadoop on Docker:

> NOTE: For faster testing the docker builds without downloading Nutch and Hadoop during each build you will need to download Nutch 1.18 and Hadoop 3.2.2 to `docker/` before trying to build/deploy, otherwise comment out lines between `### LOCAL-TESTING ###` and uncomment the `RUN mkdir... wget...` in `docker/Dockerfile-nutch-hadoop` file to enable automatically downloading Nutch and Hadoop at build time.


Start Nutch, Hadoop, Solr
```
docker-compse -d up
```

# Running a crawl:

First we need to give a crawler somewhere to start.

Create a directory for the seed file :

```
mkdir -p /opt/nutch/seeds/siarchives
```

and create a seed file with

```
echo https://siarchives.si.edu/ > /opt/nutch/seeds/siarchives/seed.txt \
&& echo https://siarchives.si.edu/collections/digitized-collections >> /opt/nutch/seeds/siarchives/seed.txt
```

Copy the seed file we created to HDFS with

```
hadoop fs -mkdir -p /siarchives/crawl

hadoop fs -copyFromLocal /opt/nutch/seeds/siarchives/seed.txt /siarchives
```

Or write seed directly to HDFS

```
echo https://siarchives.si.edu/ | hadoop fs -put - /siarchives/seed.txt \
&& echo https://siarchives.si.edu/collections/digitized-collections | hadoop fs -appendToFile - /siarchives/seed
```

we can then check the content of that file on HDFS with :

```
hadoop fs -text /siarchives/seed.txt
```

Start the crawl with

```
/opt/nutch/runtime/deploy/bin/crawl --index -s /siarchives/seed.txt /siarchives/crawl 1
```

One liner (sorta)
```
mkdir -p /opt/nutch/seeds/siarchives \
&& echo https://siarchives.si.edu/ > /opt/nutch/seeds/siarchives/seed.txt \
&& echo https://siarchives.si.edu/collections/digitized-collections >> /opt/nutch/seeds/siarchives/seed.txt \
&& hadoop fs -mkdir -p /siarchives/crawl \
&& hadoop fs -copyFromLocal /opt/nutch/seeds/siarchives/seed.txt /siarchives \
&& /opt/nutch/runtime/deploy/bin/crawl --index -s /siarchives/seed.txt /siarchives/crawl 1
```

The console will display various logs as the crawl progresses, but you should now be able to see the job information on http://localhost:8088/