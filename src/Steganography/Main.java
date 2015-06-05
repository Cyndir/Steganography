package Steganography;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/* This is the main encoding window for the steganography project
    It will set up all the tools needed for the algorithm
 */

public class Main extends JFrame {
    JFileChooser fc;
    BufferedImage bi;
    File input;

    int selection;
    int numBits;
    Analyzer a;

    boolean first;
    public Main(boolean b)throws Exception{
        fc = new JFileChooser();
        fc.setDialogTitle("Select an image to embed to");
        a = new Analyzer();
        first = b;
        initGUI();
    }

    void initGUI()throws Exception{
        Container pane;
        SpringLayout layout;
        pane = getContentPane();
        setTitle("Steganography Encoder");
        setSize(1200, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        int val = fc.showOpenDialog(Main.this);
        File f = new File("img.png");
        JLabel image = new JLabel();
        if (val == JFileChooser.APPROVE_OPTION) {
            f = fc.getSelectedFile();
            input = f;
            bi = ImageIO.read(f);
            ImageIcon ii = new ImageIcon(scaleImage(bi,512,512));
            image = new JLabel(ii);
            pane.add(image);
        }
        if (val == JFileChooser.CANCEL_OPTION) {
            if (first) {
                Decoder d = new Decoder();
            }
            return;
        }
        layout = new SpringLayout();
        pane.setLayout(layout);
        JLabel passwordLabel = new JLabel("Password");
        JLabel density = new JLabel ("Encoding Density");
        JLabel availSpace = new JLabel ("Available number of characters");
        final JTextField chars = new JTextField(5);
        chars.setEditable(false);
        final JTextField password = new JTextField(15);
        final JTextArea messageContent = new JTextArea("Place message here",35,60);
        JScrollPane message = new JScrollPane( messageContent );
        messageContent.setLineWrap(true);
        messageContent.setWrapStyleWord(true);

        //radio buttons
        JRadioButton denseButton = new JRadioButton("Dense");
        denseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setSelection(0);
            }
        });
        JRadioButton mediumButton = new JRadioButton("Medium");
        mediumButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setSelection(1);
            }
        });
        JRadioButton sparseButton = new JRadioButton("Sparse");
        sparseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setSelection(2);
            }
        });
        denseButton.setSelected(true);
        setSelection(0);
        JRadioButton oneBit = new JRadioButton("Single bit");
        oneBit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setNumBits(1);
            }
        });
        JRadioButton twoBit = new JRadioButton("Two Bits");
        twoBit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setNumBits(2);
            }
        });
        twoBit.setSelected(true);
        setNumBits(2);
        ButtonGroup bits = new ButtonGroup();
        bits.add(oneBit);
        bits.add(twoBit);
        ButtonGroup group = new ButtonGroup();
        group.add(denseButton);
        group.add(mediumButton);
        group.add(sparseButton);

        //click buttons
        JButton quitButton = new JButton("Quit");//exit program
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        JButton checkButton = new JButton("Check Available Space");//exit program
        checkButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                int spaces = 0;
                try {
                    spaces = a.checkSpace(messageContent, password, bi, getSelection());
                    if (numBits == 1) spaces = spaces / 2;
                    chars.setText(Integer.toString(spaces - messageContent.getText().length()));
                }
                catch(Exception e){
                    messageContent.setText("You must enter a password\n");
                }

            }
        });
        JButton saveButton = new JButton("Encode and Save Image");//save an image

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                File f = new File("output.png");
                try {
                    Encoder e = new Encoder(f,ImageIO.read(input),password,messageContent,getSelection(),getNumBits());
                    messageContent.setText( messageContent.getText() + "\n" + e.modifiedBits + " bits changed");


                } catch (Exception e) {
                    messageContent.setText("You must enter a password\n");
                }
            }
        });
        JButton decodeButton = new JButton("Switch to Decode");//save an image

        decodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                File f = new File("output.png");
                try {
                    setVisible(false);
                    Decoder d = new Decoder();
                }
                catch (Exception e) {
                }
            }
        });

        //Add everything to the display
        pane.add(passwordLabel);
        pane.add(password);
        pane.add(quitButton);
        pane.add(saveButton);
        pane.add(checkButton);
        pane.add(decodeButton);
        pane.add(message);
        pane.add(density);
        pane.add(denseButton);
        pane.add(mediumButton);
        pane.add(sparseButton);
        pane.add(oneBit);
        pane.add(twoBit);
        pane.add(availSpace);
        pane.add(chars);

        //Arrange the layout in a reasonable fashion
        layout.putConstraint(SpringLayout.NORTH, passwordLabel, 5, SpringLayout.SOUTH, message);
        layout.putConstraint(SpringLayout.WEST, passwordLabel, 5, SpringLayout.WEST, pane);

        layout.putConstraint(SpringLayout.NORTH, password, 0, SpringLayout.NORTH, passwordLabel);
        layout.putConstraint(SpringLayout.WEST, password, 5, SpringLayout.EAST, passwordLabel);

        layout.putConstraint(SpringLayout.NORTH, availSpace, 5, SpringLayout.SOUTH, password);
        layout.putConstraint(SpringLayout.WEST, availSpace, 5, SpringLayout.WEST, pane);

        layout.putConstraint(SpringLayout.NORTH, chars, 0, SpringLayout.NORTH, availSpace);
        layout.putConstraint(SpringLayout.WEST, chars, 5, SpringLayout.EAST, availSpace);

        layout.putConstraint(SpringLayout.NORTH, checkButton, 0, SpringLayout.NORTH, availSpace);
        layout.putConstraint(SpringLayout.WEST, checkButton, 5, SpringLayout.EAST, chars);

        layout.putConstraint(SpringLayout.NORTH, density, 0, SpringLayout.NORTH, password);
        layout.putConstraint(SpringLayout.WEST, density, 25, SpringLayout.EAST, password);

        layout.putConstraint(SpringLayout.NORTH, denseButton, 0, SpringLayout.NORTH, density);
        layout.putConstraint(SpringLayout.WEST, denseButton, 5, SpringLayout.EAST, density);

        layout.putConstraint(SpringLayout.NORTH, mediumButton, 0, SpringLayout.NORTH, density);
        layout.putConstraint(SpringLayout.WEST, mediumButton, 5, SpringLayout.EAST, denseButton);

        layout.putConstraint(SpringLayout.NORTH, sparseButton, 0, SpringLayout.NORTH, density);
        layout.putConstraint(SpringLayout.WEST, sparseButton, 5, SpringLayout.EAST, mediumButton);

        layout.putConstraint(SpringLayout.NORTH, oneBit, 0, SpringLayout.NORTH, density);
        layout.putConstraint(SpringLayout.WEST, oneBit, 25, SpringLayout.EAST, sparseButton);

        layout.putConstraint(SpringLayout.NORTH, twoBit, 0, SpringLayout.NORTH, density);
        layout.putConstraint(SpringLayout.WEST, twoBit, 5, SpringLayout.EAST,oneBit);

        layout.putConstraint(SpringLayout.NORTH, image, 5, SpringLayout.NORTH, pane);
        layout.putConstraint(SpringLayout.WEST, image, 5, SpringLayout.EAST, message);

        layout.putConstraint(SpringLayout.NORTH, message, 5, SpringLayout.NORTH, pane);
        layout.putConstraint(SpringLayout.WEST, message, 5, SpringLayout.WEST, pane);

        layout.putConstraint(SpringLayout.SOUTH, quitButton, -5, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.EAST, quitButton, -5, SpringLayout.EAST, pane);

        layout.putConstraint(SpringLayout.SOUTH, saveButton, -5, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.EAST, saveButton, -5, SpringLayout.WEST, quitButton);

        layout.putConstraint(SpringLayout.SOUTH, decodeButton, -5, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.EAST, decodeButton, -5, SpringLayout.WEST, saveButton);

        setVisible(true);
    }
    //Scale an image to the desired size to fit o nthe display
    BufferedImage scaleImage(BufferedImage image,double w, double h){
        AffineTransform at = new AffineTransform();
        double scale = Math.min( w / image.getWidth(), h / image.getHeight());
        BufferedImage scaled = new BufferedImage((int)(w), (int)(h), BufferedImage.TYPE_INT_ARGB);
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        scaled = scaleOp.filter(image, scaled);
        return scaled;
    }

    //accessor and modifier methods
    void setSelection(int i){
        selection = i;
    }
    int getSelection(){return selection;}
    int getNumBits(){return numBits;}
    void setNumBits(int i){numBits = i;}

    public static void main(String[] args) throws Exception{
        Main m = new Main(true);
    }
}
