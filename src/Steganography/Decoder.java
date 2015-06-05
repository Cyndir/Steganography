package Steganography;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * This is the main decoding window for the Steganography project
 * Created by Connor on 4/11/2015.
 */
public class Decoder extends JFrame {

    JFileChooser fc;
    Analyzer a;
    BufferedImage bi;

    public Decoder()throws Exception{
        fc = new JFileChooser();
        fc.setDialogTitle("Select an image to extract from");
        a = new Analyzer();
        initGUI();
    }

    //set up the GUI, call the analyzer when needed
    void initGUI()throws Exception{
        Container pane;
        SpringLayout layout;
        pane = getContentPane();
        layout = new SpringLayout();
        pane.setLayout(layout);

        setTitle("Steganography Decoder");
        setSize(1200, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        int val = fc.showOpenDialog(Decoder.this);
        File f = new File("img.png");
        JLabel image = new JLabel();
        if (val == JFileChooser.APPROVE_OPTION) {
            f = fc.getSelectedFile();
            bi = ImageIO.read(f);
            image = new JLabel(new ImageIcon(scaleImage(bi,512,512)));
            pane.add(image);
        }
        if (val == JFileChooser.CANCEL_OPTION) {

            System.exit(0);
        }

        JLabel passwordLabel = new JLabel("Password");
        final JTextField password = new JTextField(15);
        final JTextArea message = new JTextArea(35,60);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane( message );

        JButton quitButton = new JButton("Quit");//exit program
        quitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        JButton decodeButton = new JButton("Decode");//save an image

        decodeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                try {
                    message.setText(a.decode(bi,password.getText()));
                }
                catch (Exception e) {
                }
                ;
            }
        });

        JButton encodeButton = new JButton("Switch to Encode");//change mode

        encodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                File f = new File("output.png");
                try {
                    setVisible(false);
                    Main m = new Main(false);
                }
                catch (Exception e) {
                }
            }
        });
        pane.add(encodeButton);
        pane.add(passwordLabel);
        pane.add(password);
        pane.add(quitButton);
        pane.add(decodeButton);
        pane.add(scrollPane);

        layout.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.NORTH, pane);
        layout.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, pane);

        layout.putConstraint(SpringLayout.SOUTH, quitButton, -5, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.EAST, quitButton, -5, SpringLayout.EAST, pane);

        layout.putConstraint(SpringLayout.SOUTH, decodeButton, -5, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.EAST, decodeButton, -5, SpringLayout.WEST, quitButton);

        layout.putConstraint(SpringLayout.SOUTH, encodeButton, -5, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.EAST, encodeButton, -5, SpringLayout.WEST, decodeButton);

        layout.putConstraint(SpringLayout.NORTH, image, 5, SpringLayout.NORTH, pane);
        layout.putConstraint(SpringLayout.WEST, image, 5, SpringLayout.EAST, scrollPane);

        layout.putConstraint(SpringLayout.NORTH, passwordLabel, 5, SpringLayout.SOUTH, scrollPane);
        layout.putConstraint(SpringLayout.WEST, passwordLabel, 5, SpringLayout.WEST, pane);

        layout.putConstraint(SpringLayout.NORTH, password, 0, SpringLayout.NORTH, passwordLabel);
        layout.putConstraint(SpringLayout.WEST, password, 5, SpringLayout.EAST, passwordLabel);

        setVisible(true);
    }

    BufferedImage scaleImage(BufferedImage image,double w, double h){
        AffineTransform at = new AffineTransform();
        double scale = Math.min( w / image.getWidth(), h / image.getHeight());
        BufferedImage scaled = new BufferedImage((int)(w), (int)(h), BufferedImage.TYPE_INT_ARGB);
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        scaled = scaleOp.filter(image, scaled);
        return scaled;
    }

}