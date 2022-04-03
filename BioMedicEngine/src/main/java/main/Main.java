/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.UTF_8;
import mitos.stemmer.Stemmer;
import xmlParsing.XMLParser;

import java.io.PrintStream;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 *
 * @author manos
 */
public class Main {

    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
      
        //new XMLParser().readNXMLFileExampleInstruction();
        Stemmer.Initialize();
        System.out.println(Stemmer.Stem("ending"));
        System.out.println(Stemmer.Stem("συγχωνευμένος"));

        //String s = "एक गाव में एक किसान";
        
        //String out = new String(st.getBytes("UTF-8"), "ISO-8859-1");
        //System.out.println(out);

        //PrintStream out = new PrintStream(System.out, true, UTF_8); // true = autoflush  
        //out.println("读写汉字");
        //System.out.println(System.getProperty("file.encoding"));
        //System.out.println();
       String st = "μάνος";
        System.out.println(new String(st.getBytes(UTF_8)));
        
    }
}
