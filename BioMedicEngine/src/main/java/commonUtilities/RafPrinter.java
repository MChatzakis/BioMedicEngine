/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonUtilities;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author manos
 */
public class RafPrinter {

    public static void printDocumentsRaf(String filename) throws IOException {
        String postFilename = filename;

        RandomAccessFile post = new RandomAccessFile(postFilename, "r");

        post.seek(0);
        String line;
        int counter = 0;

        System.out.println("Printing documents file " + postFilename);
        while ((line = post.readUTF()) != null) {

            if (line.equals("#end")) {
                break;
            }

            String[] contents = line.split(" ");
            String docID = contents[0];
            String path = contents[1];
            String norm = contents[2];
            System.out.println("[" + (counter++) + "]:" + docID + " " + path + " " + norm);

        }
        post.seek(0);
        post.close();
    }

    public static void printPostingsRaf(String filename) throws IOException {
        String postFilename = filename;

        RandomAccessFile post = new RandomAccessFile(postFilename, "r");

        post.seek(0);
        String line;
        int counter = 0;

        System.out.println("Printing post file " + postFilename);
        while ((line = post.readUTF()) != null) {

            if (line.equals("#end")) {
                break;
            }

            if (line.equals("#stop\n")) {
                System.out.println("[" + (counter) + "]: stop");
                continue;
            }

            String[] contents = line.split(" ");
            String doc = contents[0];
            String tf = contents[1];
            String pos = contents[2];
            System.out.println("[" + (counter++) + "]:" + doc + " " + tf + " " + pos);

        }
        post.seek(0);
        post.close();
    }

    public static void printVocabRaf(String filename) throws IOException {
        String vocabFilename = filename;

        RandomAccessFile vocab = new RandomAccessFile(vocabFilename, "r");

        vocab.seek(0);
        String line;
        int counter = 0;

        System.out.println("Printing vocab file " + vocabFilename);
        while ((line = vocab.readUTF()) != null) {

            if (line.equals("#end")) {
                break;
            }

            String[] contents = line.split(" ");
            String termValue = contents[0];
            String df = contents[1];
            String ptr = contents[2];

            System.out.println("[" + (counter++) + "]:" + termValue + " " + df + " " + ptr);
        }

        vocab.close();
    }

    public static void printRaf(String filename) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(filename, "r");

        raf.seek(0);
        String line;
        int counter = 0;

        System.out.println("Printing raf file " + filename);
        while ((line = raf.readUTF()) != null) {

            if (line.equals("#end")) {
                break;
            }

            System.out.println("[" + (counter++) + "]:" + line);
        }
        raf.seek(0);
        raf.close();
    }
}
