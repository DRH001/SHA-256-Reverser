package Main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Scanner;

public class Backup {

    //@todo
    //use OldMain to add files containing all words plus a digit at the end



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
        Scanner inputScanner = new Scanner(System.in);
        try {
            inputStream = new FileInputStream(dir + "commonPasswords.txt");
            //outputStream = new FileOutputStream(dir + "commonPasswords.txt",true);


            scan = new Scanner(inputStream, StandardCharsets.UTF_8);

            System.out.print("Enter your password: ");
            String passwordToCrack = inputScanner.nextLine();
            //System.out.println(passwordToCrack);
            String passwordToCrackSHA = getSHA(passwordToCrack);
            //String passwordToCrackSHA = inputScanner.nextLine();
            //System.out.println("here " + passwordToCrackSHA);

            int i = 0;
            boolean flag = false;
            long startTime = System.nanoTime();
            while (scan.hasNextLine()) {
                i++;
                String[] line = scan.nextLine().split("\t");

                if(line[1].equals(passwordToCrackSHA)){
                    System.out.println("Password cracked: " + line[0]);
                    flag = true;
                    break;

                }


                //if(i > 20){break;}
                //@todo remove this break and create a new file with [key, password]. Put this file in resources and read from
                //it to see if SHA keys match up!


            }

            if(!flag){
                inputStream = new FileInputStream(dir + "commonWords.txt");
                scan = new Scanner(inputStream, StandardCharsets.UTF_8);
                while (scan.hasNextLine()) {
                    i++;
                    String[] line = scan.nextLine().split("\t");

                    if(line[1].equals(passwordToCrackSHA)){
                        System.out.println("Password cracked: " + line[0]);
                        flag = true;
                        break;
                    }
                }
            }

            if(!flag){
                inputStream = new FileInputStream(dir + "extendedCommonWords.txt");
                scan = new Scanner(inputStream, StandardCharsets.UTF_8);
                while (scan.hasNextLine()) {
                    i++;
                    String[] line = scan.nextLine().split("\t");

                    if(line[1].equals(passwordToCrackSHA)){
                        System.out.println("Password cracked: " + line[0]);
                        flag = true;
                        break;
                    }
                }
            }


            if(!flag){
                System.out.println("Password SHA not found!");
            }else{
                System.out.println("cracked in " + ((System.nanoTime() - startTime)/1000000000.0) + " seconds");
                System.out.println(i);
            }
            // note that Scanner suppresses exceptions

            //it takes 6.0E-4 seconds to get 10 SHA values

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    //outputStream.close();
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
            //System.out.println(text + ": " + temp);
            return(bytesToHex(hash));
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
