package at.scintillation.nutch;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.util.NodeWalker;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class to parse the content and apply a blacklist or whitelist. The content is stored in
 * the index in the field "strippedContent".<br/>
 * If a blacklist configuration is provided, all elements plus their subelements are not included in the
 * final content field which is indexed. If a whitelist configuration is provided, only the elements
 * and their subelements are included in the indexed field.<br/><br/>
 * On the basis of {@link https://issues.apache.org/jira/browse/NUTCH-585}
 *
 * @author Elisabeth Adler
 */
public class BlacklistWhitelistParser implements HtmlParseFilter {

    public static final Log LOG = LogFactory.getLog("at.scintillation.nutch");

    private Configuration conf;

    private String[] blacklist;

    private String[] whitelist;

    private String blacklistElementSelector;
    private String[] blacklistType;
    private String[] blacklistId;
    private String[] blacklistClass;

    private Set<String> blockNodes;

    @Override
    public ParseResult filter(Content content, ParseResult parseResult, HTMLMetaTags metaTags, DocumentFragment doc) {
        Parse parse = parseResult.get(content.getUrl());

        DocumentFragment rootToIndex = null;
        StringBuffer strippedContent = new StringBuffer();
        boolean useBlacklistElementSelector = false;

        if ((this.whitelist != null) && (this.whitelist.length > 0)) {
            LOG.info("Applying whitelist...");
            rootToIndex = (DocumentFragment) doc.cloneNode(false);
            whitelisting(doc, rootToIndex);
        } else if ((this.blacklist != null) && (this.blacklist.length > 0)) {
            LOG.info("Applying blacklist...");
            rootToIndex = (DocumentFragment) doc.cloneNode(true);
            blacklisting(rootToIndex);
        } else if ((this.blacklistElementSelector != null) && (!this.blacklistElementSelector.isEmpty())) {
            LOG.info("Applying blacklist element selector...");
            rootToIndex = (DocumentFragment) doc.cloneNode(true);
            useBlacklistElementSelector = true;
        }

        getText(strippedContent, rootToIndex, useBlacklistElementSelector); // extract text to index
        parse.getData().getContentMeta().set("strippedContent", strippedContent.toString());

        return parseResult;
    }

    /**
     * Traverse through the document and set all elements matching the given
     * blacklist configuration to empty
     *
     * @param pNode Root node
     */
    private void blacklisting(Node pNode) {
        boolean wasStripped = false;
        String nodeName = pNode.getNodeName().toLowerCase();
        String id = null;
        String className = null;
        if (pNode.hasAttributes()) {
            Node node = pNode.getAttributes().getNamedItem("id");
            id = (node != null) ? node.getNodeValue().toLowerCase() : null;

            node = pNode.getAttributes().getNamedItem("class");
            className = (node != null) ? node.getNodeValue().toLowerCase() : null;
        }

        String typeAndId = nodeName + "#" + id;
        String typeAndClass = nodeName + "." + className;

        // check if the given element is in blacklist: either only the element nodeName, or nodeName and id or nodeName and class
        boolean inList = false;
        if (nodeName != null && Arrays.binarySearch(this.blacklist, nodeName) >= 0)
            inList = true;
        else if (nodeName != null && id != null && Arrays.binarySearch(this.blacklist, typeAndId) >= 0)
            inList = true;
        else if (nodeName != null && className != null && Arrays.binarySearch(this.blacklist, typeAndClass) >= 0)
            inList = true;

        if (LOG.isTraceEnabled())
            LOG.trace("In blacklist: " + inList + " (" + nodeName + " or " + typeAndId + " or " + typeAndClass + ")");

        if (inList) {
            // can't remove this node, but we can strip it
            if (LOG.isTraceEnabled())
                LOG.trace("Removing " + nodeName + (id != null ? "#" + id : (className != null ? "." + className : "")));
            pNode.setNodeValue("");
            // remove all children for this node
            while (pNode.hasChildNodes())
                pNode.removeChild(pNode.getFirstChild());
            wasStripped = true;
        }

        if (!wasStripped) {
            // process the children recursively
            NodeList children = pNode.getChildNodes();
            if (children != null) {
                int len = children.getLength();
                for (int i = 0; i < len; i++) {
                    blacklisting(children.item(i));
                }
            }
        }
    }

    /**
     * Traverse through the document and copy all elements matching the given
     * whitelist configuration to the new node parameter, which will then only
     * contain all allowed nodes including all their children.
     *
     * @param pNode   Root node
     * @param newNode node containing only the allowed elements
     */
    private void whitelisting(Node pNode, Node newNode) {
        boolean wasStripped = false;
        String type = pNode.getNodeName().toLowerCase();
        String id = null;
        String className = null;
        if (pNode.hasAttributes()) {
            Node node = pNode.getAttributes().getNamedItem("id");
            id = (node != null) ? node.getNodeValue().toLowerCase() : null;

            node = pNode.getAttributes().getNamedItem("class");
            className = (node != null) ? node.getNodeValue().toLowerCase() : null;
        }

        String typeAndId = type + "#" + id;
        String typeAndClass = type + "." + className;

        // check if the given element is in whitelist: either only the element type, or type and id or type and class
        boolean inList = false;
        if (type != null && Arrays.binarySearch(this.whitelist, type) >= 0)
            inList = true;
        else if (type != null && id != null && Arrays.binarySearch(this.whitelist, typeAndId) >= 0)
            inList = true;
        else if (type != null && className != null && Arrays.binarySearch(this.whitelist, typeAndClass) >= 0)
            inList = true;

        if (LOG.isTraceEnabled())
            LOG.trace("In whitelist: " + inList + " (" + type + " or " + typeAndId + " or " + typeAndClass + ")");

        if (inList) {
            // can't remove this node, but we can strip it
            if (LOG.isTraceEnabled())
                LOG.trace("Using " + type + (id != null ? "#" + id : (className != null ? "." + className : "")));
            newNode.appendChild(pNode.cloneNode(true));
            wasStripped = true;
        }

        if (!wasStripped) {
            // process the children recursively
            NodeList children = pNode.getChildNodes();
            if (children != null) {
                int len = children.getLength();
                for (int i = 0; i < len; i++) {
                    whitelisting(children.item(i), newNode);
                }
            }
        }
    }

    /**
     * copied from {@link org.apache.nutch.parse.html.DOMContentUtils}
     */
    private boolean getText(StringBuffer sb, Node node, boolean useTextBlacklist) {
        boolean abortOnNestedAnchors = false;
        int anchorDepth = 0;
        boolean abort = false;
        NodeWalker walker = new NodeWalker(node);

        while (walker.hasNext()) {

            Node currentNode = walker.nextNode();
            String nodeName = currentNode.getNodeName();
            short nodeType = currentNode.getNodeType();
            NamedNodeMap attrs = currentNode.getAttributes();
            Node previousSibling = currentNode.getPreviousSibling();

            if (previousSibling != null && blockNodes.contains(previousSibling.getNodeName().toLowerCase())) {
                appendParagraphSeparator(sb);
            } else if (blockNodes.contains(nodeName.toLowerCase())) {
                appendParagraphSeparator(sb);
            }

            if (useTextBlacklist) {
                if (attrs != null) {

                    for (int i = 0; i < attrs.getLength(); i++) {

                        Node attr = attrs.item(i);

                        if ("class".equalsIgnoreCase(attr.getNodeName())) {
                            String[] tagClasses = attr.getNodeValue().toLowerCase().split(" ");

                            for (int j = 0; j < tagClasses.length; j++) {
                                if (Arrays.stream(this.blacklistClass).anyMatch(tagClasses[j]::equalsIgnoreCase)) {
                                    walker.skipChildren();
                                }
                            }
                        }

                        if ("id".equalsIgnoreCase(attr.getNodeName())) {
                            if (Arrays.stream(this.blacklistId).anyMatch(attr.getNodeValue()::equalsIgnoreCase)) {
                                walker.skipChildren();
                            }
                        }
                    }
                }
            }
            if ("script".equalsIgnoreCase(nodeName)) {
                walker.skipChildren();
            }
            if ("style".equalsIgnoreCase(nodeName)) {
                walker.skipChildren();
            }
            if (useTextBlacklist) {
                if (Arrays.stream(this.blacklistType).anyMatch(nodeName::equalsIgnoreCase)) {
                    walker.skipChildren();
                }
            }
            if (abortOnNestedAnchors && "a".equalsIgnoreCase(nodeName)) {
                anchorDepth++;
                if (anchorDepth > 1) {
                    abort = true;
                    break;
                }
            }
            if (nodeType == Node.COMMENT_NODE) {
                walker.skipChildren();
            }
            if (nodeType == Node.TEXT_NODE) {
                // cleanup and trim the value
                String text = currentNode.getNodeValue();
                text = text.replaceAll("\\s+", " ");
                text = text.trim();
                if (text.length() > 0) {
                    appendSpace(sb);
                    sb.append(text);
                } else {
                    appendParagraphSeparator(sb);
                }
            }
        }

        return abort;
    }

    /**
     * Conditionally append a paragraph/line break to StringBuffer unless last
     * character a already indicates a paragraph break. Also remove trailing space
     * before paragraph break.
     *
     * @param buffer StringBuffer to append paragraph break
     */
    private void appendParagraphSeparator(StringBuffer buffer) {
        if (buffer.length() == 0) {
            return;
        }
        char lastChar = buffer.charAt(buffer.length() - 1);
        if ('\n' != lastChar) {
            // remove white space before paragraph break
            while (lastChar == ' ') {
                buffer.deleteCharAt(buffer.length() - 1);
                lastChar = buffer.charAt(buffer.length() - 1);
            }
            if ('\n' != lastChar) {
                buffer.append('\n');
            }
        }
    }


    /**
     * Conditionally append a space to StringBuffer unless last character is a
     * space or line/paragraph break.
     *
     * @param buffer StringBuffer to append space
     */
    private void appendSpace(StringBuffer buffer) {
        if (buffer.length() == 0) {
            return;
        }
        char lastChar = buffer.charAt(buffer.length() - 1);
        if (' ' != lastChar && '\n' != lastChar) {
            buffer.append(' ');
        }
    }

    public Configuration getConf() {
        return this.conf;
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
        // parse configuration for blacklist
        this.blacklist = null;
        String elementsToExclude = getConf().get("parser.html.blacklist", null);
        if ((elementsToExclude != null) && (elementsToExclude.trim().length() > 0)) {
            elementsToExclude = elementsToExclude.toLowerCase(); // convert to lower case so that there's no case
            // problems
            LOG.info("Configured using [parser.html.blacklist] to ignore elements [" + elementsToExclude + "]...");
            this.blacklist = elementsToExclude.split(",");
            Arrays.sort(this.blacklist); // required for binary search
        }

        // parse configuration for whitelist
        this.whitelist = null;
        String elementsToInclude = getConf().get("parser.html.whitelist", null);
        if ((elementsToInclude != null) && (elementsToInclude.trim().length() > 0)) {
            elementsToInclude = elementsToInclude.toLowerCase(); // convert to lower case so that there's no case
            // problems
            LOG.info("Configured using [parser.html.whitelist] to only use elements [" + elementsToInclude + "]...");
            this.whitelist = elementsToInclude.split(",");
            Arrays.sort(this.whitelist); // required for binary search
        }

        blockNodes = new HashSet<>(conf.getTrimmedStringCollection("parser.html.line.separators"));

        this.blacklistElementSelector = getConf().get("parser.html.text.blacklist", null);
        List<String> elementTypeSelectorList = new ArrayList<>();

        this.blacklistType = null;
        this.blacklistId = null;
        this.blacklistClass = null;

        if ((this.blacklistElementSelector != null) && (!this.blacklistElementSelector.isEmpty())) {
            Arrays.asList(this.blacklistElementSelector.split("\\|"))
                    .forEach(tag -> {
                        if (tag.startsWith("type")) {
                            this.blacklistType = StringUtils.substringBetween(tag, "(", ")").toLowerCase().split(",");
                        } else if (tag.startsWith("id")) {
                            this.blacklistId = StringUtils.substringBetween(tag, "(", ")").toLowerCase().split(",");
                        } else if (tag.startsWith("class")) {
                            this.blacklistClass = StringUtils.substringBetween(tag, "(", ")").toLowerCase().split(",");
                        }
                    });
        }
        
    }

}
