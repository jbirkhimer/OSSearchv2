Parse-metatags plugin

The parse-metatags plugin consists of a HTMLParserFilter which takes as parameter a list of metatag names with '*' as default value. The values are separated by ';'.
In order to extract the values of the metatags description and keywords, you must specify in nutch-site.xml

<property>
  <name>metatags.names</name>
  <value>description;keywords</value>
</property>

Prefixes the names with 'meta_' in the parse-metadata. For instance to index description and keywords, you need to activate the plugin index-metadata and set the value of the parameter 'index.parse.md' to 'meta_description;meta_keywords'.
  
This code has been developed by DigitalPebble Ltd and offered to the community by ANT.com




