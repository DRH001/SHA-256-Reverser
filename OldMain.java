package Main.java;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Scanner;

public class OldMain {

    static MessageDigest digest;
    public static void main(String[]args){

        /*
          https://www.baeldung.com/java-read-lines-large-file


         */
        String dir = null;
        try{
            File root = new File(Thread.currentThread().getContextClassLoader().getResource("").toURI());
            dir = root +"/Main/resources/";
            root = null;
        }catch(Exception ignored){}


        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        Scanner scan = null;
        try {
            inputStream = new FileInputStream(dir + "null.txt");
            outputStream = new FileOutputStream(dir + "null.txt",true);


            scan = new Scanner(inputStream, StandardCharsets.UTF_8);
            int i = 0;
            while (scan.hasNextLine()) {
                String line = StringFunctions.convert(scan.nextLine().split("\t")[0]);
                //System.out.println(line);
                outputStream.write((line + "\t" + getSHA(line) + "\n").getBytes());
                System.out.println(i);

                i++;
                //if(i > 20){break;}
                //@todo remove this break and create a new file with [key, password]. Put this file in resources and read from
                //it to see if SHA keys match up!
            }
            // note that Scanner suppresses exceptions

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (scan != null) {
                scan.close();
            }
        }



    }


    public static String getSHA(String text){
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            String temp = bytesToHex(hash);
            //System.out.println(text + ": " + temp);
            return(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(null);
    }



    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


}
