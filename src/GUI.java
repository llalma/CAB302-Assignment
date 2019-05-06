import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import static java.lang.Math.abs;
import static java.lang.Math.round;

//LF file formats check mine is right
// check saving can have any number of decimals
//display error when changing size of screen, drawing on small moving to large. maybe fix by checking if screen size changes redraw.
//In load file change selected tool to eraser

public class GUI extends JFrame {

    //Find vector images with no background
    //List of buttons to include, needs to be na images file
    static final String[] tool_Buttons = new String[]{"resources/Line.jpg","resources/Rectangle.png","resources/Circle.png"};
    static final int num_Tool_Buttons = tool_Buttons.length;

    static final String[] file_Buttons = new String[]{"resources/Save.png", "resources/Load.png"};
    static final int num_File_Buttons = file_Buttons.length;


    //Positions of mouse pointer
    private int x_Previous,y_Previous,x_Current,y_Current;

    //Stores shapes drawn in the drawing area
    enum shape_Type{
        LINE, RECTANGLE, CIRCLE
    }
    private ArrayList<Drawn_Shapes> drawn_Shapes = new ArrayList<>();
    public class Drawn_Shapes{
        shape_Type Type;
        ArrayList<Double> coordinates;

        Drawn_Shapes(shape_Type Type, ArrayList<Double> coordinates){
            this.Type = Type;
            this.coordinates = coordinates;
        }
    }

    //Selected drawing tool, defaults to Line
    private int selected_Tool = 0;

    public GUI() {
        super("Paint");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        buttons_Create();
        drawing_area();

        // Display the window.
        setPreferredSize(new Dimension(400, 200));
        setLocation(new Point(100, 100));
        pack();
        setVisible(true);
    }

    private void drawing_area(){
        JPanel panel = new Draw_Panel();

        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                MouseClicked(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                MouseReleased(evt);
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                x_Current = e.getX();
                y_Current = e.getY();
                repaint();
            }
        });
        getContentPane().add(panel,"Center");
    }

    private void buttons_Create(){
        //Tool Buttons, on west edge
        JPanel panel = new JPanel(new GridLayout(num_Tool_Buttons, 1));
        for (int i = 0; i < num_Tool_Buttons; i++) {
            JButton button = new JButton();

            //Load and add image to a button
            try {
                Image img = ImageIO.read(getClass().getResource(tool_Buttons[i]));
                Image scaled_img = img.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ;

                //Sets whats the button will do when pressed, this case change the tool selected for drawing
                button.setAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String x =e.getActionCommand();
                        selected_Tool = Integer.parseInt(x);
                        System.out.println(selected_Tool);
                    }
                });

                //These need to be after setAction
                button.setActionCommand(Integer.toString(i));
                button.setIcon(new ImageIcon(scaled_img));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            panel.add(button);
        }
        getContentPane().add(panel,"West");

        /* Need to make it so buttons can be a smaller size */
        //Save buttons, new buttons ect.
        JPanel file_panel = new JPanel(new GridLayout(1, num_File_Buttons));
        for (int i = 0; i < num_File_Buttons; i++) {
            JButton button = new JButton();
            //Load and add image to a button
            try {
                Image img = ImageIO.read(getClass().getResource(file_Buttons[i]));
                Image scaled_img = img.getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH ) ;

                //Sets whats the button will do when pressed, this save the file
                if(file_Buttons[i].contains("Save")){
                    //This is the save button, Give save functionality
                    button.setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            save_File();
                        }
                    });
                }else if(file_Buttons[i].contains("Load")){
                    //This is the load button, Give load functionality
                    button.setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            load_File();
                        }
                    });
                }


                button.setIcon(new ImageIcon(scaled_img));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            button.setPreferredSize(new Dimension(40, 40));
            file_panel.add(button);
        }
        getContentPane().add(file_panel,"North");
    }

    private void save_File(){
        //Saves the file to the path specified by the user
        //Only shows directories & Vec files to select, Default saves as VEC format, no other options are avaliable.
        JFileChooser chooser= new JFileChooser(System.getProperty("user.dir"));
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("VEC", "VEC");
        chooser.addChoosableFileFilter(filter);

        //Show GUI and return the path when the choice is confirmed
        int choice = chooser.showDialog(null,"Select");
        if (choice != JFileChooser.APPROVE_OPTION) return;
        File directory_Path = chooser.getSelectedFile();

        try {
            PrintWriter writer;
            if(directory_Path.toString().contains("VEC")){
                writer = new PrintWriter(directory_Path, "UTF-8");
            }else{
                writer = new PrintWriter(directory_Path + ".VEC", "UTF-8");
            }

            //Loop through each object drawn
            for (Drawn_Shapes shape:drawn_Shapes) {
                String line = shape.Type + " " + shape.coordinates.get(0) + " " + shape.coordinates.get(1) + " " +  shape.coordinates.get(2) + " " +  shape.coordinates.get(3);
                writer.println(line);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void load_File(){
        //Prewritten GUI for file browsing, shows the users current directory on opening.
        //Only shows VEC files.
        JFileChooser chooser= new JFileChooser(System.getProperty("user.dir"));
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Only VEC Files", "VEC");
        chooser.addChoosableFileFilter(filter);

        //Show GUI and return the path when the choice is confirmed
        int choice = chooser.showOpenDialog(null);
        if (choice != JFileChooser.APPROVE_OPTION) return;
        File chosenFile = chooser.getSelectedFile();

        //Clear the drawing area by emptying drawn_Shapes, change selected tool to the eraser as the shaped
        //drawn immedietly before loading is still drawn.
        drawn_Shapes = new ArrayList<>();
        selected_Tool = 2;

        //Load the file
        try {
            Scanner sc = new Scanner(chosenFile);

            //Read each line in the file & add to drawn_Shapes
            while (sc.hasNextLine()){
                //Reag each string seperated by space char
                String data[] = sc.nextLine().split(" ");
                add_Shape(shape_Type.valueOf(data[0]).ordinal() , Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3]), Double.parseDouble(data[4]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Repaint screen with new shapes
        repaint();
    }

    private void MouseClicked(MouseEvent event){
        //get the x and y coordinates when the click occours
        x_Previous = event.getX();
        y_Previous = event.getY();
        x_Current = x_Previous;
        y_Current = y_Previous;
    }

    private void MouseReleased(MouseEvent event){

        x_Current = event.getX();
        y_Current = event.getY();
        //System.out.println("mouse released");

        //Add shape to previously drawn of shapes
        add_Shape(selected_Tool,x_Previous,y_Previous,x_Current,y_Current);
        repaint();

    }

    private void add_Shape(int tool, double x1, double y1, double x2, double y2){
        //Size of drawing area, used convert coordinates to percentage of screen size
        double height = getContentPane().getComponent(2).getHeight();
        double width = getContentPane().getComponent(2).getWidth();

        //Add shape to previously drawn of shapes
        ArrayList<Double> Coords = new ArrayList<>();
        if(x1 == x_Previous && x2 == x_Current && y1 == y_Previous && y2 == y_Current){
            //Adding from users inputs
            Coords.add(x1/width);
            Coords.add(y1/height);
            Coords.add(x2/width);
            Coords.add(y2/height);
        }else{
            //Adding from loading from file
            Coords.add(x1);
            Coords.add(y1);
            Coords.add(x2);
            Coords.add(y2);
        }

        //Creates a new element to insert into the list of drawn shapes
        Drawn_Shapes shape = new Drawn_Shapes(shape_Type.values()[tool], Coords);
        drawn_Shapes.add(shape);
    }

    class Draw_Panel extends JPanel {

        Draw_Panel() {
            //Initilise Drawing panel
            setBackground(new java.awt.Color(255, 255, 255));
            setBorder(BorderFactory.createLineBorder(Color.black));
            setName("Paint Area");
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            //These are used to convert the percentage into the current screen size coordinates
            double height = getContentPane().getComponent(2).getHeight();
            double width = getContentPane().getComponent(2).getWidth();
            int x1,x2,y1,y2;

            //Draws shapes currently drawn to the screen
            for (Drawn_Shapes shape:drawn_Shapes) {
                x1 = (int) round(shape.coordinates.get(0) * width);
                y1 = (int) round(shape.coordinates.get(1) * height);
                x2 = (int) round(shape.coordinates.get(2) * width);
                y2 = (int) round(shape.coordinates.get(3) * height);

                switch (shape.Type){
                    case LINE:
                        g.drawLine(x1,y1,x2,y2);
                        break;
                    case RECTANGLE:
                        //Is longer than Line as width and height are used not x and y coords
                        g.drawRect(Math.min(x1, x2),Math.min(y1, y2),Math.abs(x1 - x2),Math.abs(y1 - y2));
                        break;
                    default:
                        System.out.println("Not a shape");
                        //put exception throw here.
                        break;
                }
            }

            //Draw shape currently being drawn
            switch (shape_Type.values()[selected_Tool]) {
                case LINE:
                    g.drawLine(x_Previous,y_Previous,x_Current,y_Current);
                    break;
                case RECTANGLE:
                    g.drawRect(Math.min(x_Previous, x_Current),Math.min(y_Previous, y_Current),Math.abs(x_Previous - x_Current),Math.abs(y_Previous - y_Current));
                    break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(false);
        new GUI();
    }

}