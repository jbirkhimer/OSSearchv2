index-blacklist-whitelist plugin
---------------------------------

The index-blacklist-whitelist plugin takes a list of content elements as parameters to define which parts of the page will be indexed.

1. Enable the blacklist/whitelist plugin:
Edit your nutch-site.xml file and adding "|index-blacklist-whitelist" to the property "plugin.includes".
Example:
<property>
  <name>plugin.includes</name>
  <value>protocol-http|urlfilter-regex|parse-html|index-(basic|anchor)|query-(basic|site|url)|response-(json|xml)|summary-basic|scoring-opic|urlnormalizer-(pass|regex|basic)|index-blacklist-whitelist</value>
</property>

2. Define the blacklist or whitelist:
To define a blacklist or whitelist, specify either a property 'parser.html.blacklist' or 'parser.html.whitelist' in your nutch-site.xml file.
In the list you can provide the name of the element and optional provide the name or class of the element. 
Depending on which configuration is available, the corresponding list will be used. If both configurations are 
available, only the whitelist is used. 
Examples: to define a blacklist for header and footer or a whitelist for the div 'post', use:
<property>
  <name>parser.html.blacklist</name>
  <value>div#header,div#footer</value>
  <description>
	A comma-delimited list of css like tags to identify the elements which should
	NOT be parsed. Use this to tell the HTML parser to ignore the given elements, e.g. site navigation.
	It is allowed to only specify the element type (required), and optional its class name ('.')
	or ID ('#'). More complex expressions will not be parsed.
	Valid examples: div.header,span,p#test,div#main,ul,div.footercol
	Invalid expressions: div#head#part1,#footer,.inner#post
	Note that the elements and their children will be silently ignored by the parser,
	so verify the indexed content with Luke to confirm results.
	Use either 'parser.html.blacklist' or 'parser.html.whitelist', but not both of them at once. If so,
	only the whitelist is used.
  </description>
</property>

<property>
  <name>parser.html.whitelist</name>
  <value>div.post</value>
  <description>
	A comma-delimited list of css like tags to identify the elements which should
	be parsed. Use this to tell the HTML parser to only use the given elements, e.g. content.
	It is allowed to only specify the element type (required), and optional its class name ('.')
	or ID ('#'). More complex expressions will not be parsed.
	Valid examples: div.header,span,p#test
	Invalid expressions: div#head#part1,#footer,.inner#post
	Note that the elements and their children will be silently ignored by the parser,
	so verify the indexed content with Luke to confirm results.
	Use either 'parser.html.blacklist' or 'parser.html.whitelist', but not both of them at once. If so,
	only the whitelist is used.
  </description>
</property>

3. Add the field with the content the list has been applied to to be indexed in Solr:
In order to have the stripped content indexed by Solr, edit your Solr schema.xml and include the following line:
<!-- fields for the blacklist/whitelist plugin -->
<field name="strippedContent" type="text" stored="true" indexed="true"/>
