import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;


public class Main extends JFrame {

    static final int num_Buttons = 2;
    static final String[] image_Buttons = new String[]{"resources/Rectangle.png","resources/Circle.png"};
    /**
     *
     */
    private static final long serialVersionUID = 4145047606482760810L;

    public Main() {
        super("Grid Layout Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        buttons_Create();

        // Display the window.
        setPreferredSize(new Dimension(400, 200));
        setLocation(new Point(100, 100));
        pack();
        setVisible(true);
    }

    private void buttons_Create(){
        JPanel panel = new JPanel(new GridLayout(num_Buttons, 1));
        for (int i = 0; i < num_Buttons; i++) {
            JButton button = new JButton();

            //Load and add image to a button
            try {
                Image img = ImageIO.read(getClass().getResource(image_Buttons[i]));
                Image scamled_img = img.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ;
                button.setIcon(new ImageIcon(scamled_img));
            } catch (Exception ex) {
                System.out.println(ex);
            }

            panel.add(button);
        }
        getContentPane().add(panel,"West");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(false);

        new Main();
    }

}