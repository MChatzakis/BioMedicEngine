/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import nxmlParsing.NXMLParser;

/**
 *
 * @author manos
 */
public class Main {

    public static void main(String[] args) throws IOException {
        new NXMLParser().readFileExampleInstruction();
    }
}
