/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataHandlers;

import processes.GlobalParam;
import processes.LogFactory;

import java.net.URL;
import java.util.List;
import java.util.regex.*;
import java.util.ArrayList;
import java.io.StringWriter;
import org.w3c.dom.Document;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.net.MalformedURLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author Sajith
 */
public class AccessLastFM {

    private static final String BASE_URL ="https://" + GlobalParam.getBaseLastFMURL();
    private static final String ACCESS_TOKEN = GlobalParam.getLastFMAPIKey();
    private static final Logger LOGGER = LogFactory.getNewLogger(AccessLastFM.class.getName());

    public static URL getURL(String methodParam) {
        LOGGER.log(Level.WARNING,"Response might be broken or unavailable");
        String url = BASE_URL + methodParam + "&api_key=" + ACCESS_TOKEN;
        URL tempURL = null;
        try {
            tempURL = new URL(url);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"URL is unreachable or broken",e);
        }
        return tempURL;
    }
    
    public static List<String> extractPattern(String regPattern, Document responseXML, int tagNameLength) {
        List<String> matchList = new ArrayList<>();

        String tempXML = docToString(responseXML);

        Pattern searchPattern = Pattern.compile(regPattern);
        Matcher patternMatcher = searchPattern.matcher(tempXML);

        while (patternMatcher.find()) {
            String item = patternMatcher.group();
            matchList.add(item.substring(tagNameLength + 2, (item.length() - (tagNameLength + 3))));
        }
        return matchList;
    }
    
    public static Document grabXML(URL url) {
        Document responseXML = null;
        try {
            LOGGER.log(Level.WARNING,"Response might be broken or unavailable");
            URLConnection response = url.openConnection();
            DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
            responseXML = docBuilder.parse(response.getInputStream());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,"URL is unreachable or broken",e);
        } 
        return responseXML;
    }

    private static String docToString(Document doc) {
        StringWriter writer = new StringWriter();

        DOMSource domSource = new DOMSource(doc);
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            transformer.transform(domSource, result);
        } catch (TransformerException e) {
            LOGGER.log(Level.SEVERE,"Document convertion failed",e);
        }
        return writer.toString();
    }

}