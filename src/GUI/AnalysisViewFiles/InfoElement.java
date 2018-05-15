package GUI.AnalysisViewFiles;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class InfoElement extends JPanel {

    int x,y,width,height;
    private BufferedImage image;
    String text, icon_path;

    public InfoElement(int x, int y, int width, int height, String text, String icon_path){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.icon_path = icon_path;
        this.setBackground(new Color(43,63,83));
        this.setLayout(null);
        this.setSize(this.width, this.height);
        this.setLocation(this.x, this.y);
        setIcon(this.icon_path);
        putText(35, 0 , this.text);

    }

    private void setIcon(String icon_path){
        try {
            image = ImageIO.read(new File(icon_path));
        } catch (IOException ex) {
            System.out.println("Error : Icon File Not Found");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, 30,30,this);
    }

    private void putText(int x, int y, String text){
        JLabel label =  new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        label.setLocation(x,y);
        label.setSize(30,30);
        this.add(label);
    }
}
