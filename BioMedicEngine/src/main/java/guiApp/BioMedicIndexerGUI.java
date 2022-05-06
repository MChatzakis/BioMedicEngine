/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package guiApp;

import generalStructures.IndexResult;
import index.BioMedicIndexer;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 *
 * @author manos
 */
public class BioMedicIndexerGUI extends JFrame {

    Container generalPanel = new Container();

    JLabel resultsFolder = new JLabel("Output Folder:");
    JLabel inputFolder = new JLabel("Input Folder:");
    JLabel englishStopWords = new JLabel("StopWords(EN):");
    JLabel greekStopWords = new JLabel("StopWords(GR):");

    JTextField resultsFolderText = new JTextField("add a path..");
    JTextField englishStopWordsText = new JTextField("add a file..");
    JTextField greekStopWordsText = new JTextField("add a file..");
    JTextField inputFolderText = new JTextField("add a path..");

    JButton submit = new JButton("Index Directory");

    JButton selectFolder = new JButton("Output Folder");
    JButton inFolder = new JButton("Input Folder");

    JButton selectStopWordsGR = new JButton("Stop Words File GR");
    JButton selectStopWordsEN = new JButton("Stop Words File EN");

    JTextArea output = new JTextArea();

    public BioMedicIndexerGUI(int w, int h) {

        setTitle("BioMedic Indexer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(100, 90, w, h);

        generalPanel = getContentPane();
        generalPanel.setLayout(new GridLayout(0, 1));

        addSubmitButtonFunc();
        addDirButtonsFunc();

        generalPanel.add(resultsFolder);
        generalPanel.add(resultsFolderText);
        generalPanel.add(selectFolder);

        generalPanel.add(englishStopWords);
        generalPanel.add(englishStopWordsText);
        generalPanel.add(selectStopWordsEN);

        generalPanel.add(greekStopWords);
        generalPanel.add(greekStopWordsText);
        generalPanel.add(selectStopWordsGR);

        generalPanel.add(submit);
        generalPanel.add(output);

        setResizable(false);
        setVisible(true);
    }

    private void addSubmitButtonFunc() {
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String gr = greekStopWordsText.getText();
                String en = englishStopWordsText.getText();
                String in = inputFolderText.getText();
                String out = resultsFolderText.getText();

                BioMedicIndexer bmi = new BioMedicIndexer();

                try {
                    bmi.loadStopWords(gr);
                    bmi.loadStopWords(en);

                    IndexResult res = bmi.indexNXMLDirectory(in, out);
                    output.append(res.toString());
                } catch (Exception ex) {
                    Logger.getLogger(BioMedicIndexerGUI.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    private void addDirButtonsFunc() {
        selectFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        
        selectFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        
        selectFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        
        selectFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void main(String[] args) {
        new BioMedicIndexerGUI(500, 250);
    }
}
