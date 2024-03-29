indexer-jsonbatch plugin for Nutch 
==============================

**indexer-jsonbatch plugin** is used for writing json lines to a plain text file for EDAN loading. It does not work in distributed mode, the output is written to the local filesystem, not to HDFS. The configuration for the index writers is on **conf/index-writers.xml** file, included in the official Nutch distribution and it's as follow:

```xml
<writer id="<writer_id>" class="org.apache.nutch.indexwriter.jsonbatch.JsonBatchIndexWriter">
  <mapping>
    ...
  </mapping>
  <parameters>
    ...
  </parameters>   
</writer>
```

Each `<writer>` element has two mandatory attributes:

* `<writer_id>` is a unique identification for each configuration. This feature allows Nutch to distinguish each configuration, even when they are for the same index writer. In addition, it allows to have multiple instances for the same index writer, but with different configurations.

* `org.apache.nutch.indexwriter.jsonbatch.JsonBatchIndexWriter` corresponds to the canonical name of the class that implements the IndexWriter extension point. This value should not be modified for the **indexer-jsonbatch plugin**.

## Mapping

The mapping section is explained [here](https://cwiki.apache.org/confluence/display/NUTCH/IndexWriters#IndexWriters-Mappingsection). The structure of this section is general for all index writers.

## Parameters

Each parameter has the form `<param name="<name>" value="<value>"/>` and the parameters for this index writer are:

Parameter Name | Description | Default value
--|--|--
 path | Path where the file will be created. | ./jsonbatch-index.txt
 delete | If delete operations should be written to the file. | false
