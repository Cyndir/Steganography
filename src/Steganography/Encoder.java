package Steganography;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Connor on 4/11/2015.
 */
public class Encoder {
    File out;
    BufferedImage image;
    String passcode;
    String text;
    int bits;
    int modifiedBits;
    int encoding;
    indexer Idxer;

    public Encoder(File f,BufferedImage bi,JTextField password,JTextArea message,int mode,int numBits){
        out = f;
        image = bi;
        passcode = password.getText();
        text = message.getText();
        encoding = mode;
        bits = numBits;
        modifiedBits = 0;
        Idxer = new indexer();
        text = text +"_";//add a flag character
        makeHeader();

        if(numBits ==1) singleBitEncode();
        else{ twoBitEncode();}
        try {
            ImageIO.write(image, "png", f);
        }
        catch(Exception e){
            System.out.println("Write Failed");
        }
    }
    //modify the header pixel as necessary
    void makeHeader(){
        Color original = new Color(image.getRGB(0,0));

        String redString = pad(Integer.toBinaryString(original.getRed()));
        String blueString = pad(Integer.toBinaryString(original.getBlue()));
        String modeString = pad(Integer.toBinaryString(encoding));
        if(bits==1 &&redString.charAt(redString.length()-1)!=1){
            modifiedBits++;
            redString = replaceChar(redString,redString.length()-1,'1');
        }
        if(bits==2 &&redString.charAt(redString.length()-1)!=0){
            modifiedBits++;
            redString = replaceChar(redString,redString.length()-1,'0');
        }
        if(blueString.charAt(blueString.length()-2)!=modeString.charAt(0)){
            modifiedBits++;
            blueString = replaceChar(blueString,blueString.length()-2,modeString.charAt(0));
        }
        if(blueString.charAt(blueString.length()-1)!=modeString.charAt(1)){
            modifiedBits++;
            blueString = replaceChar(blueString,blueString.length()-1,modeString.charAt(1));
        }

        image.setRGB(0,0,new Color(Integer.parseInt(redString,2),original.getGreen(),Integer.parseInt(blueString,2)).getRGB());
    }
    //loop for 2 bit encoding
    void twoBitEncode(){
        int pixelIndex=1;
        int codeIndex=0;
        int x;
        int y;

        for (int i = 0; i <text.length() ; i++) {
            x = pixelIndex%image.getWidth();
            y = pixelIndex/image.getWidth();

            image.setRGB(x,y,modify2Bit(image.getRGB(x,y),text.charAt(i)));
            if(encoding ==0)pixelIndex+=Idxer.denseIndex(passcode.charAt((codeIndex++)%passcode.length()));
            if(encoding ==1)pixelIndex+=Idxer.mediumIndex(passcode.charAt((codeIndex++)%passcode.length()));
            if(encoding ==2)pixelIndex += Idxer.sparseIndex(passcode.charAt((codeIndex++)%passcode.length()));
        }

    }
    //loop for one bit encoding
    void singleBitEncode(){
        int pixelIndex=1;
        int codeIndex=0;
        int x;
        int y;
        for (int i = 0; i <text.length() ; i++) {
            x = pixelIndex % image.getWidth();
            y = pixelIndex / image.getWidth();

            image.setRGB(x,y,modify1Bit(image.getRGB(x,y),text.charAt(i),1));
            if(encoding ==0)pixelIndex+=Idxer.denseIndex(passcode.charAt((codeIndex++)%passcode.length()));
            if(encoding ==1)pixelIndex+=Idxer.mediumIndex(passcode.charAt((codeIndex++)%passcode.length()));
            if(encoding ==2)pixelIndex += Idxer.sparseIndex(passcode.charAt((codeIndex++)%passcode.length()));

            x = pixelIndex % image.getWidth();
            y = pixelIndex / image.getWidth();

            image.setRGB(x,y,modify1Bit(image.getRGB(x, y), text.charAt(i),2));
            if(encoding ==0)pixelIndex+=Idxer.denseIndex(passcode.charAt((codeIndex++)%passcode.length()));
            if(encoding ==1)pixelIndex+=Idxer.mediumIndex(passcode.charAt((codeIndex++)%passcode.length()));
            if(encoding ==2)pixelIndex += Idxer.sparseIndex(passcode.charAt((codeIndex++)%passcode.length()));

        }

    }
    //changes low order bit in each color and return the RGB int value
    int modify1Bit(int RGB, char c,int half){
        c = Character.toUpperCase(c);
        c=(char)(c-32);
        String charString = pad(Integer.toBinaryString(c));
        Color original = new Color(RGB);
        int red =original.getRed();
        String redString = pad(Integer.toBinaryString(red));
        int blue = original.getBlue();
        String blueString = pad(Integer.toBinaryString(blue));
        int green=original.getGreen();
        String greenString = pad(Integer.toBinaryString(green));

        if(half ==1){
            if(redString.charAt(redString.length()-1)!=charString.charAt(0)){
                modifiedBits++;
                redString = replaceChar(redString,redString.length()-1,charString.charAt(0));
            }
            if(blueString.charAt(blueString.length()-1)!=charString.charAt(1)){
                modifiedBits++;
                blueString = replaceChar(blueString,blueString.length()-1,charString.charAt(1));
            }
            if(greenString.charAt(greenString.length()-1)!=charString.charAt(2)){
                modifiedBits++;
                greenString = replaceChar(greenString,greenString.length()-1,charString.charAt(2));
            }
        }
        //second half of a character
        else{
            if(redString.charAt(redString.length()-1)!=charString.charAt(3)){
                modifiedBits++;
                redString = replaceChar(redString,redString.length()-1,charString.charAt(3));
            }
            if(blueString.charAt(blueString.length()-1)!=charString.charAt(4)){
                modifiedBits++;
                blueString = replaceChar(blueString,blueString.length()-1,charString.charAt(4));
            }
            if(greenString.charAt(greenString.length()-1)!=charString.charAt(5)){
                modifiedBits++;
                greenString = replaceChar(greenString,greenString.length()-1,charString.charAt(5));
            }

        }

        Color encoded = new Color(Integer.parseInt(redString,2),Integer.parseInt(greenString,2),Integer.parseInt(blueString,2));

        return encoded.getRGB();
    }
    //changes the RGB value for 2 bit encoding and returns the modified RGB
    int modify2Bit(int RGB, char c){

        c = Character.toUpperCase(c);
        c=(char)(c-32);
        String charString = pad(Integer.toBinaryString(c));
        Color original = new Color(RGB);
        int red =original.getRed();
        String redString = pad(Integer.toBinaryString(red));
        if(redString.charAt(redString.length()-2)!=charString.charAt(0)){
            modifiedBits++;
            redString = replaceChar(redString,redString.length()-2,charString.charAt(0));
        }
        if(redString.charAt(redString.length()-1)!=charString.charAt(1)){
            modifiedBits++;
            redString = replaceChar(redString,redString.length()-1,charString.charAt(1));
        }

        int blue = original.getBlue();
        String blueString = pad(Integer.toBinaryString(blue));
        if(blueString.charAt(blueString.length()-2)!=charString.charAt(2)){
            modifiedBits++;
            blueString = replaceChar(blueString,blueString.length()-2,charString.charAt(2));
        }
        if(blueString.charAt(blueString.length()-1)!=charString.charAt(3)){
            modifiedBits++;
            blueString = replaceChar(blueString,blueString.length()-1,charString.charAt(3));
        }

        int green=original.getGreen();
        String greenString = pad(Integer.toBinaryString(green));
        if(greenString.charAt(greenString.length()-2)!=charString.charAt(4)){
            modifiedBits++;
            greenString = replaceChar(greenString,greenString.length()-2,charString.charAt(4));
        }
        if(greenString.charAt(greenString.length()-1)!=charString.charAt(5)){
            modifiedBits++;
            greenString = replaceChar(greenString,greenString.length()-1,charString.charAt(5));
        }
        Color encoded = new Color(Integer.parseInt(redString,2),Integer.parseInt(greenString,2),Integer.parseInt(blueString,2));

        return encoded.getRGB();
    }
    //extend a bitsting to 6 bits for encoding
    String pad(String original){
        while(original.length() < 6){
            original = '0'+original;
        }
        return original;
    }
    //change a character in a string
    String replaceChar(String in,int index,char replacement){
        return in.substring(0,index)+ replacement+in.substring(index+1);
    }
}
