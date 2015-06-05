package Steganography;

/**
 * Created by Connor on 4/11/2015.
 */
public class indexer {

    int denseIndex(char c){// check greatest prime factor, return accordingly
        if ((c)%7==0) return 1;
        if ((c)%5==0) return 2;
        if ((c)%3==0) return 3;
        if ((c)%2==0) return 4;
        return 5;
    }

    int mediumIndex(char c){//Spark of creativity gone.


        return (c%10)+1;
    }

    int sparseIndex(char c){

        return (c%15)+1;
    }


}
