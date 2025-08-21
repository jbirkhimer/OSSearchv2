package org.apache.nutch.indexer.metadata;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jbirkhimer
 */
public class TestDynamicNavigationsMultivalueFields {

    @Test
    public void testDynamicNavigationsMultivalueFields() {
        Map<String, String> map = new HashMap<String, String>();
        List<String> list = new ArrayList<String>();
        String filepath = "/home/jbirkhimer/IdeaProjects/OSSearchv2/docker/nutch/conf/dynamic-navigations-multivalue-fields.txt";
        File file = new File(filepath);
        BufferedReader reader = null;

        // checking if dyanmic file exist
        if (file.exists() && !file.isDirectory()) {
            try {
                reader = new BufferedReader(new FileReader(file));
                String text = null;
                while ((text = reader.readLine()) != null) {
                    String[] split = text.split("-dl-");
                    String metaname = (split.length > 0) ? split[0] : null;
                    String metavalue = (split.length > 1) ? split[1]: null;
                    if(metaname != null && metavalue != null){
                        map.put(metaname.toLowerCase(),metavalue);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                }
            }
        }

        System.out.println(map);
    }
}
