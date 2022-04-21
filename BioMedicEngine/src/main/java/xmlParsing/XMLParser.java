/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlParsing;

import gr.uoc.csd.hy463.NXMLFileReader;
import gr.uoc.csd.hy463.Topic;
import gr.uoc.csd.hy463.TopicsReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class XMLParser {

    public void readNXMLFileExampleInstruction() throws IOException {

        File example = new File("./sample/MiniCollection/MiniCollection/treatment/Topic_27/0/1936313.nxml");
        NXMLFileReader xmlFile = new NXMLFileReader(example);
        String pmcid = xmlFile.getPMCID();
        String title = xmlFile.getTitle();
        String abstr = xmlFile.getAbstr();
        String body = xmlFile.getBody();
        String journal = xmlFile.getJournal();
        String publisher = xmlFile.getPublisher();
        ArrayList<String> authors = xmlFile.getAuthors();
        HashSet<String> categories = xmlFile.getCategories();

        System.out.println("- PMC ID: " + pmcid);
        System.out.println("- Title: " + title);
        System.out.println("- Abstract: " + abstr);
        System.out.println("- Body: " + body);
        System.out.println("- Journal: " + journal);
        System.out.println("- Publisher: " + publisher);
        System.out.println("- Authors: " + authors);
        System.out.println("- Categories: " + categories);
    }

    public void readTopicsXMLFile() throws Exception {
        ArrayList<Topic> topics = TopicsReader.readTopics("C:\\dataset\\ topics.xml");
        for (Topic topic : topics) {
            System.out.println(topic.getNumber());
            System.out.println(topic.getType());
            System.out.println(topic.getSummary());
            System.out.println(topic.getDescription());
            System.out.println("---------");
        }
    }
    
    
    
        
}
