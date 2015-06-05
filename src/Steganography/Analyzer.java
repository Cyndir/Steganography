package Steganography;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Connor on 4/11/2015.
 */
public class Analyzer {
indexer IDer = new indexer();
    //Calculate the capacity of the image given with the set modes and password
    int checkSpace(JTextArea message, JTextField password, BufferedImage bi,int mode){
        int numPixels = bi.getHeight()*bi.getWidth();
        int passwordIndex = 0;
        int available=0;
        String pass = password.getText();
        int passLength = pass.length();
        if(mode==0)
            for (int i = 0; i <numPixels ; i+=IDer.denseIndex(pass.charAt(passwordIndex%passLength))) {
                available++;
                passwordIndex++;
            }
        else if(mode==1){
            for (int i = 0; i <numPixels ; i+=IDer.mediumIndex(pass.charAt(passwordIndex % passLength))) {
                available++;
                passwordIndex++;
            }
        }
        else if(mode==2){
            for (int i = 0; i <numPixels ; i+=IDer.sparseIndex(pass.charAt(passwordIndex % passLength))) {
                available++;
                passwordIndex++;
            }
        }
        return available;
    }
    //Decode a message from the image and password
    String decode(BufferedImage image, String password){

        int mode;
        int RGB = image.getRGB(0, 0);
        Color original = new Color(RGB);
        int red =original.getRed();
        String redString = pad(Integer.toBinaryString(red));
        int blue = original.getBlue();
        String blueString = pad(Integer.toBinaryString(blue));
        if(blueString.charAt(blueString.length()-2)=='0'){
            if(blueString.charAt(blueString.length()-1)=='0')
                mode = 0;
            else{
                mode = 1;
            }
        }
        else{
            mode =2;
        }

        if(redString.charAt(redString.length()-1)=='1'){//1 bit decode
            return oneBitDecode(mode,image,password);
        }
        else{//2 bit decode
            return twoBitDecode(mode,image,password);
        }


    }
    //Two bit decode mode
    String twoBitDecode(int mode, BufferedImage image, String passcode){
        int pixelIndex=1;
        int codeIndex=0;
        int x;
        int y;
        String decoded = "";

        while(true) {
            x = pixelIndex % image.getWidth();
            y = pixelIndex / image.getWidth();
           String bits;

            bits = get6Bits(image.getRGB(x,y));

            if((char)Integer.parseInt(bits,2)=='_'-32) break;
            decoded = decoded + (char)(Integer.parseInt(bits,2)+32);


            if(mode ==0)pixelIndex+=IDer.denseIndex(passcode.charAt((codeIndex++)%passcode.length()));
            if(mode ==1)pixelIndex+=IDer.mediumIndex(passcode.charAt((codeIndex++)%passcode.length()));
            if(mode ==2)pixelIndex += IDer.sparseIndex(passcode.charAt((codeIndex++)%passcode.length()));

        }
        return decoded;
    }
    //one bit decode mode
    String oneBitDecode(int mode, BufferedImage image, String passcode){
        int pixelIndex=1;
        int codeIndex=0;
        int x;
        int y;
        String decoded = "";
        while(true) {
            x = pixelIndex % image.getWidth();
            y = pixelIndex / image.getWidth();
            String bits = "";
            bits = bits + get3Bits(image.getRGB(x,y));
            if(mode ==0)pixelIndex+=IDer.denseIndex(passcode.charAt((codeIndex++)%passcode.length()));
            if(mode ==1)pixelIndex+=IDer.mediumIndex(passcode.charAt((codeIndex++)%passcode.length()));
            if(mode ==2)pixelIndex += IDer.sparseIndex(passcode.charAt((codeIndex++)%passcode.length()));
            x = pixelIndex % image.getWidth();
            y = pixelIndex / image.getWidth();
            bits = bits + get3Bits(image.getRGB(x, y));
            if(mode ==0)pixelIndex+=IDer.denseIndex(passcode.charAt((codeIndex++)%passcode.length()));
            if(mode ==1)pixelIndex+=IDer.mediumIndex(passcode.charAt((codeIndex++) % passcode.length()));
            if(mode ==2)pixelIndex += IDer.sparseIndex(passcode.charAt((codeIndex++) % passcode.length()));
            if((char)Integer.parseInt(bits,2)=='_'-32) break;
            decoded = decoded + (char)(Integer.parseInt(bits,2)+32);

        }
        return decoded;
    }
    //get a 6 bit binary string from low order bits
    String get6Bits(int RGB){
        Color c = new Color(RGB);
        int red =c.getRed();
        String redString = pad(Integer.toBinaryString(red));
        int blue = c.getBlue();
        String blueString = pad(Integer.toBinaryString(blue));
        int green=c.getGreen();
        String greenString = pad(Integer.toBinaryString(green));
        String result = "";

        result = result + Character.toString(redString.charAt(redString.length()-2));
        result = result + Character.toString(redString.charAt(redString.length()-1));
        result = result + Character.toString(blueString.charAt(blueString.length()-2));
        result = result + Character.toString(blueString.charAt(blueString.length()-1));
        result = result + Character.toString(greenString.charAt(greenString.length()-2));
        result = result + Character.toString(greenString.charAt(greenString.length()-1));

        return result;
    }
//get a 3 bit binary string from low order bits
    String get3Bits(int RGB){
        Color c = new Color(RGB);
        int red =c.getRed();
        String redString = pad(Integer.toBinaryString(red));
        int blue = c.getBlue();
        String blueString = pad(Integer.toBinaryString(blue));
        int green=c.getGreen();
        String greenString = pad(Integer.toBinaryString(green));

        return Character.toString(redString.charAt(redString.length()-1)) + Character.toString(blueString.charAt(blueString.length()-1)) + Character.toString(greenString.charAt(greenString.length() - 1));
    }

    String pad(String original){
        while(original.length() < 6){
            original = '0'+original;
        }
        return original;
    }
}
