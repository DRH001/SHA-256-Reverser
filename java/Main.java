package Main.java;

import java.beans.EventHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {


    final static boolean takeSHAorPassword = false; //true to take SHA, false to take password
    static final int maxDigitsToScan = 50;//max search depth before timing out



    //@todo
    //use OldMain to add files containing all words plus a digit at the end


    public static void scanFile(String fileString){//run on main thread
        try{
            if(!flag){
                inputStream = new FileInputStream(dir + fileString);
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
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static int i = 0;
    static volatile boolean flag = false;
    static volatile FileInputStream inputStream = null;
    static Scanner scan = null;
    static Scanner inputScanner = new Scanner(System.in);
    static String dir = null;
    static MessageDigest digest;
    static String passwordToCrackSHA = null;
    static volatile int finishedCount = 0;

    public static void main(String[]args){

        /*
          https://www.baeldung.com/java-read-lines-large-file


         */


        try{
            File root = new File(Thread.currentThread().getContextClassLoader().getResource("").toURI());
            dir = root +"/Main/resources/";
            root = null;
            digest = MessageDigest.getInstance("SHA-256");

            if(takeSHAorPassword){
                System.out.print("Enter your SHA-256 hash: ");
                passwordToCrackSHA = inputScanner.nextLine()
                        .replace(" ","")
                        .replace("\n","")
                        .toLowerCase();
            }else{
                System.out.print("Enter your password: ");
                String passwordToCrack = inputScanner.nextLine();
                //System.out.println(passwordToCrack);
                passwordToCrackSHA = getSHA(passwordToCrack);
            }




        }catch(Exception ignored){}


        long startTime = System.nanoTime();
        scanFile("commonPasswords.txt");
        scanFile("commonWords.txt");
        scanFile("extendedCommonWords.txt");
        scanFile("commonPasswordsCaseSwitched.txt");
        scanFile("commonWordsCaseSwitched.txt");
        scanFile("extendedCommonWordsCaseSwitched.txt");

        boolean started = true;
        if(flag){
            System.out.println("cracked in " + ((System.nanoTime() - startTime)/1000000000.0) + " seconds");
            System.out.println(i);
            started = false;
        }else{
            System.out.println("searching deeper...");
        }

        int noOfThreadsTotal = 12;//total of threads that should be finished in the first round

        //adds plain digits to all files
        new Thread(()->{scanFileWithDigits("commonPasswords.txt");}).start();
        new Thread(()->{scanFileWithDigits("commonWords.txt");}).start();
        new Thread(()->{scanFileWithDigits("extendedCommonWords.txt");}).start();
        new Thread(()->{scanFileWithDigits("commonPasswordsCaseSwitched.txt");}).start();
        new Thread(()->{scanFileWithDigits("commonWordsCaseSwitched.txt");}).start();
        new Thread(()->{scanFileWithDigits("extendedCommonWordsCaseSwitched.txt");}).start();

        //add popular suffixes to all files
        new Thread(()->{scanFileWithPopularSuffixes("commonPasswords.txt");}).start();
        new Thread(()->{scanFileWithPopularSuffixes("commonWords.txt");}).start();
        new Thread(()->{scanFileWithPopularSuffixes("extendedCommonWords.txt");}).start();
        new Thread(()->{scanFileWithPopularSuffixes("commonPasswordsCaseSwitched.txt");}).start();
        new Thread(()->{scanFileWithPopularSuffixes("commonWordsCaseSwitched.txt");}).start();
        new Thread(()->{scanFileWithPopularSuffixes("extendedCommonWordsCaseSwitched.txt");}).start();

        if(!flag){
            //explores all logical dates of birth (1920-2025)
            //@todo make this check !@#$%^&*()_+=-`~[]}{;'":,./?>< too....
            //replaces numbers with common substitutes
        }

        while(!flag && finishedCount!=noOfThreadsTotal){
            try{Thread.sleep(200);}catch(Exception ignored){}
            //System.out.println(finishedCount);
        }

        if(finishedCount == noOfThreadsTotal && !flag){
            System.out.println("Password SHA not found!");
            System.out.println("Total time: " + ((System.nanoTime() - startTime)/1000000000.0) + " seconds");

            //make this learn new passwords in a new file.....
            //System.out.println("Would you like to do a continuous search? This may never finish (Y/N)");
            //String response = inputScanner.nextLine();
            //if(response.equals("Y")){
                //compound search...........
                //links words together, combines digits with popular suffixes, adds dates of birth, replaces numbers with common substitutions
                //only finishes when done, prints the elapsed time every 5 seconds ish
            //}
        }
        if(flag && started){
            System.out.println("cracked in " + ((System.nanoTime() - startTime)/1000000000.0) + " seconds");
            System.exit(finishedCount == noOfThreadsTotal ? 0:5);//or make a better way to kill all running threads....
        }


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



        //it takes 6.0E-4 seconds to get 10 SHA values


    }


    public static String getSHA(String text){
        try {
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            //System.out.println(text + ": " + temp);
            return(bytesToHex(hash));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(null);
    }

    public static String getSHA(String text, MessageDigest digest2){
        try {
            byte[] hash = digest2.digest(text.getBytes(StandardCharsets.UTF_8));
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



    public static void scanFileWithDigits(String fileString){

        MessageDigest instanceDigest = null;
        try {
            instanceDigest = MessageDigest.getInstance("SHA-256");
        }catch(NoSuchAlgorithmException ignored){}
        Scanner instanceScanner;
        InputStream instanceInputStream;
        try{
            if(!flag){
                forLoop:
                for(int j=0;j<maxDigitsToScan+1;j++) {
                    instanceInputStream = new FileInputStream(dir + fileString);
                    instanceScanner = new Scanner(instanceInputStream, StandardCharsets.UTF_8);
                    while (instanceScanner.hasNextLine()) {
                        String possiblePassword = instanceScanner.nextLine().split("\t")[0];


                        if ((getSHA(possiblePassword + j, instanceDigest)).equals(passwordToCrackSHA)) {
                            System.out.println("Password cracked: " + (possiblePassword + j));
                            flag = true;
                            break forLoop;
                        }

                    }
                }
                finishedCount++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static int noOfSuffixes = 30 + 17;
    static String popularSuffix(int pos){
        ArrayList<String> suffixes = new ArrayList<>();
        String chars = "!@#$%^&*()-=_+[]\\{}|;'\":,./?><";//30
        for(int i=0;i<chars.length();i++){
            suffixes.add(""+chars.charAt(i));
        }
        String[] others = {"lol","42","69","22","33","44","55","66","77","88","99","100","101","111","420","42069","69420"};//17
        for(String a:others){
            suffixes.add(a);
        }
        return(suffixes.get(pos));
    }


    public static void scanFileWithPopularSuffixes(String fileString){

        MessageDigest instanceDigest = null;
        try {
            instanceDigest = MessageDigest.getInstance("SHA-256");
        }catch(NoSuchAlgorithmException ignored){}
        Scanner instanceScanner;
        InputStream instanceInputStream;
        try{
            if(!flag){
                forLoop:
                for(int j=0;j<noOfSuffixes;j++) {
                    instanceInputStream = new FileInputStream(dir + fileString);
                    instanceScanner = new Scanner(instanceInputStream, StandardCharsets.UTF_8);
                    while (instanceScanner.hasNextLine()) {
                        String possiblePassword = instanceScanner.nextLine().split("\t")[0];


                        if ((getSHA(possiblePassword + popularSuffix(j), instanceDigest)).equals(passwordToCrackSHA)) {
                            System.out.println("Password cracked: " + (possiblePassword + popularSuffix(j)));
                            flag = true;
                            break forLoop;
                        }

                    }
                }
                finishedCount++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }



}
