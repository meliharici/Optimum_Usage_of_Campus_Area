package GUI.AnalysisViewFiles;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.ArrayList;

public class MenuButton extends JButton {

    int x,y,width,height;
    ArrayList<JComboBox> combos;

    public MenuButton(int x, int y, int width, int height, String title){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setBackground(Color.lightGray);
        this.setLayout(null);
        this.setText(title);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.GRAY, Color.darkGray));
        this.setSize(this.width, this.height);
        this.setLocation(this.x, this.y);
        combos = new ArrayList<>();
    }

    public void addLabel(int x, int y, int width, int height, String text){
        JLabel label =  new JLabel(text);
        label.setLocation(x,y);
        label.setSize(width,height);
        this.add(label);
    }

    public void addCombo(int x, int y, int width, int height, String[] values){
        JComboBox<String> combo = new JComboBox<String>(values);
        combo.setLocation(x,y);
        combo.setSize(width,height);
        combos.add(combo);
        this.add(combo);
    }

    public ArrayList<JComboBox> getCombos(){
        return combos;
    }
}
