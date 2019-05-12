import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import static java.lang.Math.*;

//LF file formats check mine is right
// check saving can have any number of decimals

public class GUI extends JFrame {

    //Find vector images with no background
    //List of buttons to include, needs to be na images file
    private static final String[] tool_Buttons = new String[]{"resources/Line.jpg","resources/Rectangle.png","resources/Plot.png","resources/Ellipse.png","resources/Polygon.png"};
    private static final int num_Tool_Buttons = tool_Buttons.length;

    private static final String[] file_Buttons = new String[]{"resources/Save.png", "resources/Load.png", "resources/Undo.png"};
    private static final int num_File_Buttons = file_Buttons.length;

    private static final Color[] colour_Buttons = new Color[]{Color.BLACK,Color.RED,Color.GREEN,Color.YELLOW,
                                                              Color.WHITE,Color.BLUE,Color.ORANGE,Color.MAGENTA};
    private static final int num_Colour_Buttons = colour_Buttons.length;

    private static final String[] colour_Special_Buttons = new String[]{"resources/NoFill.jpg","resources/Rainbow.jpg"};
    private static final int num_Colour_Special_Buttons = colour_Special_Buttons.length;

    //Positions of mouse pointer
    private int x_Previous,y_Previous,x_Current,y_Current;

    //Stores shapes drawn in the drawing area
    enum shape_Type{
        LINE, RECTANGLE,PLOT,ELLIPSE,POLYGON,PEN,FILL
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

    //Array list for a single Polygons
    ArrayList<Double> polygon = new ArrayList<>();
    boolean polygon_Completed = true;
    boolean mouse_Pressed = false;

    //Selected drawing tool, defaults to Line
    private int selected_Tool = 0;

    //Selected Colour
    private Color pen_Colour = Color.BLACK;

    //Check the shape is to be filled
    boolean fill = false;
    private Color fill_Colour = Color.BLACK;

    public GUI()  {
        super("Paint");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        buttons_Create();
        drawing_area();

        // Display the window.
        setPreferredSize(new Dimension(400, 400));
        setLocation(new Point(100, 100));
        pack();
        setVisible(true);

       new_Drawn_Shapes();
    }

    private void drawing_area() {
        JPanel panel = new Draw_Panel();


        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                mouse_Pressed = true;
                MouseClicked(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                if(selected_Tool != shape_Type.POLYGON.ordinal()) {
                    mouse_Pressed = false;
                    MouseReleased(evt);
                }
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                //Dont want to repaint for dragging with the polygon tool as that cna allow the user to draw in a different position
                if(selected_Tool != shape_Type.POLYGON.ordinal()){
                    x_Current = e.getX();
                    y_Current = e.getY();
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                //Dont want to repaint for dragging with the polygon tool as that cna allow the user to draw in a different position
                if(selected_Tool == shape_Type.POLYGON.ordinal() && !polygon_Completed){
                    x_Current = e.getX();
                    y_Current = e.getY();
                    repaint();
                }
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
                        //If the user selects another tool before polygon is completed make sure it ends
                            polygon_Completed = true;
                            //Clear the polygon
                            polygon = new ArrayList<>();

                        String x =e.getActionCommand();
                        selected_Tool = Integer.parseInt(x);
                        //System.out.println(selected_Tool);
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

        //Save buttons, new buttons ect, On north edge.
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
                }else if(file_Buttons[i].contains("Undo")){
                    //This is the load button, Give load functionality
                    button.setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                           undo();
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

        //Colour Buttons on south edge
        //Plus 2 for number of coloums due to the 2 extra buttons fro nofill and selector
        JPanel colour_panel = new JPanel(new GridLayout(2, num_Colour_Buttons+2));
        for (int i = 0; i < num_Colour_Buttons; i++) {
            JButton button = new JButton();

            button.setAction(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    int type = 0;
                    //0 is a fill command, 1 is a pen command
                    if(evt.getModifiers() == 18){
                        //18 is  a left control + left mouse click
                        //Fill
                        fill_Colour = button.getBackground();
                        fill = true;
                    }else{
                        //Pen
                        pen_Colour = button.getBackground();
                        type = 1;
                    }
                    Add_Colour(type);
                }
            });
            button.setBackground(colour_Buttons[i]);
            button.setSize(10,10);
            colour_panel.add(button);
        }

        //Load and add image to a button, used for the Nofill and the selector tools
        //Also part of panel above
        for(int i = 0;i<num_Colour_Special_Buttons;i++) {
            try {
                JButton button = new JButton();
                Image img = ImageIO.read(getClass().getResource(colour_Special_Buttons[i]));
                Image scaled_img = img.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);

                //Set action for buttons, makes each button get their specific functionality
                if(colour_Special_Buttons[i].contains("NoFill")){
                    //Initial starting colour, red == no fill, green == will fill
                    button.setBackground(Color.RED);
                    button.setName("Fill");
                    button.setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //Simple toggle of colours and fill states
                            if(fill){
                                fill = false;
                                Add_Colour(3);
                                button.setBackground(Color.RED);
                            }else{
                                fill = true;
                                button.setBackground(Color.GREEN);
                            }
                        }
                    });
                }else if(colour_Special_Buttons[i].contains("Rainbow")){
                    button.addActionListener(new Colour_Chooser());
                }

                button.setIcon(new ImageIcon(scaled_img));
                colour_panel.add(button);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

        colour_panel.setName("Colour_Panel");
        getContentPane().add(colour_panel,"South");
    }

    private class Colour_Chooser implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            //Sets colour then adds colour to list
            int type = 0;
            //0 is a fill command, 1 is a pen command
            if(evt.getModifiers() == 18){
                //18 is  a left control + left mouse click
                fill_Colour = JColorChooser.showDialog(null, "Choose a Color", pen_Colour);
            }else{
                pen_Colour = JColorChooser.showDialog(null, "Choose a Color", pen_Colour);
                type = 1;
            }
            Add_Colour(type);
        }
    }

    private void Add_Colour(int type){
        //Add colour to list of shapes, same arrayList as shapes as the order matters
        //and arrayLists keeps the order

        //Create array to store RGB values
        ArrayList<Double> RGB = new ArrayList<>();

        //Creates a new element to insert into the list of drawn shapes
        Drawn_Shapes shape;
        //dosent work for colour selector wheel
        if(type == 0){
            //0 is a fill command
            RGB.add(fill_Colour.getRed() + 0.0);
            RGB.add(fill_Colour.getGreen() + 0.0);
            RGB.add(fill_Colour.getBlue() + 0.0);
            shape = new Drawn_Shapes(shape_Type.FILL, RGB);
        }else if(type == 3){
            //3 is a no fill command
            RGB.add((double)'O');
            RGB.add((double)'F');
            RGB.add((double)'F');
            shape = new Drawn_Shapes(shape_Type.FILL, RGB);
        }else{
            RGB.add(pen_Colour.getRed() + 0.0);
            RGB.add(pen_Colour.getGreen() + 0.0);
            RGB.add(pen_Colour.getBlue() + 0.0);
            shape = new Drawn_Shapes(shape_Type.PEN, RGB);
        }

        drawn_Shapes.add(shape);
    }

    private void undo(){
        //Remove the last thing added to draw_shapes.
        //this will undo the last thing done
        if(drawn_Shapes.size() > 2){
            drawn_Shapes.remove(drawn_Shapes.size()-1);
            x_Current = -1;
            x_Previous = -1;
            y_Current = -1;
            y_Previous = -1;
            repaint();
        }else{
            //Throw exception, index out of range
        }
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
                StringBuilder line = new StringBuilder();
                if(shape.Type == shape_Type.POLYGON) {
                    //Polygon saving is slightly different from other shapes
                    double height = getContentPane().getComponent(3).getHeight();
                    double width = getContentPane().getComponent(3).getWidth();

                    line.append(shape.Type.toString());

                    //Minus 2 from size as to not inlcude the last point which just direct back to the starting position
                    for (int i = 0; i < shape.coordinates.size() - 2; i++) {
                        if (i % 2 == 0) {
                            line.append(" " + shape.coordinates.get(i) / width);
                        } else {
                            line.append(" " + shape.coordinates.get(i) / height);
                        }
                    }
                }else if(shape.Type == shape_Type.PEN || shape.Type == shape_Type.FILL){
                    //Colour Saving
                    Color colour_temp = new Color((int)round(shape.coordinates.get(0)),(int)round(shape.coordinates.get(1)),(int)round(shape.coordinates.get(2)));
                    Color oFF = new Color((int)'O',(int)'F',(int)'F');
                    String hex;
                    if(colour_temp.getRGB() == oFF.getRGB()){
                        //Save into the file as OFF insteda of an RGB value
                        hex = "OFF";
                    }else{
                        hex = "#"+Integer.toHexString(colour_temp.getRGB()).substring(2);
                    }

                    line.append(shape.Type + " " + hex);
                }else{
                    //All other shapes saving
                    line.append(shape.Type + " " + shape.coordinates.get(0) + " " + shape.coordinates.get(1) + " " +  shape.coordinates.get(2) + " " +  shape.coordinates.get(3));
                }
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
        new_Drawn_Shapes();
        selected_Tool = 2;

        //Load the file
        try {
            Scanner sc = new Scanner(chosenFile);

            //Read each line in the file & add to drawn_Shapes
            while (sc.hasNextLine()){
                //Reag each string seperated by space char
                String data[] = sc.nextLine().split(" ");

                switch (shape_Type.valueOf(data[0])) {
                    case PLOT:
                        add_Shape(shape_Type.valueOf(data[0]).ordinal(), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[1]), Double.parseDouble(data[2]));
                        break;
                    case POLYGON:
                        add_Polygon(data);
                        break;
                    case PEN:
                        int r = Integer.valueOf(data[1].substring(1,3),16);
                        int g = Integer.valueOf(data[1].substring(3,5),16);
                        int b = Integer.valueOf(data[1].substring(5,7),16);

                        pen_Colour = new Color(r,g,b);
                        Add_Colour(1);
                        break;
                    case FILL:
                        if(data[1].equals("OFF")){
                            Add_Colour(3);
                        }else {
                            r = Integer.valueOf(data[1].substring(1, 3), 16);
                            g = Integer.valueOf(data[1].substring(3, 5), 16);
                            b = Integer.valueOf(data[1].substring(5, 7), 16);

                            fill_Colour = new Color(r, g, b);
                            Add_Colour(0);
                        }
                        break;

                    default:
                        add_Shape(shape_Type.valueOf(data[0]).ordinal(), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3]), Double.parseDouble(data[4]));
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Repaint screen with new shapes
        repaint();
    }

    private void new_Drawn_Shapes(){
        //Sets the default values in every new instance of drawn_Shapes.
        drawn_Shapes = new ArrayList<>();

        //Default pen colour
        ArrayList<Double> RGB = new ArrayList<>();
        RGB.add(0.0);
        RGB.add(0.0);
        RGB.add(0.0);
        drawn_Shapes.add(new Drawn_Shapes(shape_Type.PEN, RGB));

        RGB = new ArrayList<>();
        RGB.add((double)'O');
        RGB.add((double)'F');
        RGB.add((double)'F');
        drawn_Shapes.add(new Drawn_Shapes(shape_Type.FILL, RGB));
    }

    private void add_Polygon(String data[]){
        double height = getContentPane().getComponent(3).getHeight();
        double width = getContentPane().getComponent(3).getWidth();

        //Add the coordinates of the polygon to a variable
        for(int i = 1;i<data.length;i++){
            //If odd will be an x value, else will be y value
            if(i%2 != 0){
                polygon.add(Double.parseDouble(data[i]) * width);
            }else{
                polygon.add(Double.parseDouble(data[i]) * height);
            }
        }

        //Reconnecting the last point to the original
        polygon.add(Double.parseDouble(data[1]) * width);
        polygon.add(Double.parseDouble(data[2]) * height);

        //Add the polygon read from the VEC file and add to the drawing variable
        Drawn_Shapes shape = new Drawn_Shapes(shape_Type.valueOf(data[0]), polygon);
        drawn_Shapes.add(shape);

        //Clear the polygon coordinate variable
        polygon = new ArrayList<>();
    }

    private void MouseClicked(MouseEvent event){
        //get the x and y coordinates when the click occours
        x_Previous = event.getX();
        y_Previous = event.getY();
        x_Current = x_Previous;
        y_Current = y_Previous;

        if(selected_Tool == shape_Type.POLYGON.ordinal()) {
            polygon_Completed = false;
            Double coords = x_Current + 0.0;
            polygon.add(coords);
            coords = y_Current + 0.0;
            polygon.add(coords);

            //Ends the polygon if original and latest position is within 10 pixels in any direction.
            if(polygon_Ending_Check() && polygon.size() > 4){
                mouse_Pressed = false;
                polygon_Completed = true;
                //Remove last 2 inputs
                polygon.remove(polygon.size()-2);
                polygon.remove(polygon.size()-1);

                //Add the last 2 indexes as the original coordinates
                polygon.add(polygon.get(0));
                polygon.add(polygon.get(1));

                Drawn_Shapes shape = new Drawn_Shapes(shape_Type.POLYGON, polygon);
                drawn_Shapes.add(shape);

                //Clear the polygon
                polygon = new ArrayList<>();
            }
        }
    }

    private boolean polygon_Ending_Check(){
        boolean same_Spot = false;
        //Checks if the click is within 10 pixels either way of original point, for x coordinates
        if (x_Current>polygon.get(0)-10 && x_Current<polygon.get(0)+10){
            same_Spot = true;
        }
        //Checks if the click is within 10 pixels either way of original point, for y coordinates
        //Extra check to see if the x-coordinate was on the point
        if (y_Current>polygon.get(1)-10 && y_Current<polygon.get(1)+10 && same_Spot){
            same_Spot = true;
        }
        return same_Spot;
    }

    private void MouseReleased(MouseEvent event){

        x_Current = event.getX();
        y_Current = event.getY();
        //System.out.println("mouse released");

        if(selected_Tool != shape_Type.POLYGON.ordinal()){
            //Add shape to previously drawn of shapes
            add_Shape(selected_Tool,x_Previous,y_Previous,x_Current,y_Current);
        }
        repaint();
    }

    private void add_Shape(int tool, double x1, double y1, double x2, double y2){
        //Size of drawing area, used convert coordinates to percentage of screen size
        double height = getContentPane().getComponent(3).getHeight();
        double width = getContentPane().getComponent(3).getWidth();

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

            //Get the input and action maps for drawing panel
            InputMap inputmap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionmap = this.getActionMap();

            //Add an input of left control and Z
            inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z ,KeyEvent.CTRL_DOWN_MASK ), "Undo" );

            //Action when left control and z is pressed
            actionmap.put("Undo", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    undo();
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            //These are used to convert the percentage into the current screen size coordinates
            double height = getContentPane().getComponent(3).getHeight();
            double width = getContentPane().getComponent(3).getWidth();
            int x1,x2,y1,y2;

            //Draws shapes that have already been drawn or loaded
            for (Drawn_Shapes shape:drawn_Shapes) {
                if(shape.Type == shape_Type.PEN) {
                    pen_Colour = new Color((int) round(shape.coordinates.get(0)), (int) round(shape.coordinates.get(1)), (int) round(shape.coordinates.get(2)));
                }else if(shape.Type == shape_Type.FILL){
                    if(shape.coordinates.get(0) == 79.0 && shape.coordinates.get(2) == 70.0 && shape.coordinates.get(2) == 70.0){
                        //Need a betetr way to check this. possibly make rgb colours null?
                        fill = false;
                    }else{
                        fill_Colour = new Color((int)round(shape.coordinates.get(0)),(int)round(shape.coordinates.get(1)),(int)round(shape.coordinates.get(2)));
                        fill = true;
                    }
                }else{
                    x1 = (int) round(shape.coordinates.get(0) * width);
                    y1 = (int) round(shape.coordinates.get(1) * height);
                    x2 = (int) round(shape.coordinates.get(2) * width);
                    y2 = (int) round(shape.coordinates.get(3) * height);

                    switch (shape.Type){
                        case LINE:
                            g.setColor(pen_Colour);
                            g.drawLine(x1,y1,x2,y2);
                            break;
                        case RECTANGLE:
                            //Draw outline
                            g.setColor(pen_Colour);
                            g.drawRect(Math.min(x1, x2),Math.min(y1, y2),Math.abs(x1 - x2),Math.abs(y1 - y2));
                            //Fill rectangle with colour
                            if(fill){
                                g.setColor(fill_Colour);
                                g.fillRect(Math.min(x1, x2)+1,Math.min(y1, y2)+1,Math.abs(x1 - x2)-1,Math.abs(y1 - y2)-1);
                            }
                            break;
                        case PLOT:
                            //Draw outline
                            g.setColor(pen_Colour);
                            g.drawOval(x1,y1,1,1);
                            break;
                        case ELLIPSE:
                            //Draw outline
                            g.setColor(pen_Colour);
                            g.drawOval(Math.min(x1, x2)+1,Math.min(y1, y2)+1,Math.abs(x1 - x2)-2,Math.abs(y1 - y2)-2);
                            if(fill){
                                g.setColor(fill_Colour);
                                g.fillOval(Math.min(x1, x2),Math.min(y1, y2),Math.abs(x1 - x2),Math.abs(y1 - y2));
                            }
                            break;
                        case POLYGON:
                            int size = shape.coordinates.size();
                            g.setColor(pen_Colour);
                            for(int i = 3;i<size;i+=2){
                                g.drawLine((int)round(shape.coordinates.get(i-3)),(int)round(shape.coordinates.get(i-2)), (int)round(shape.coordinates.get(i-1)),(int)round(shape.coordinates.get(i)));
                            }

                            //Fill Polygon
                            if(fill){
                                g.setColor(fill_Colour);
                                int[] x_points = new int[size/2];
                                int[] y_points = new int[size/2];
                                for(int i = 0;i<size;i++){
                                    if(i%2 == 0){
                                        //Even
                                        x_points[(int)round(floor(i/2))] = (int) round(shape.coordinates.get(i));
                                    }else{
                                        y_points[(int)round(floor(i/2))] = (int) round(shape.coordinates.get(i));
                                    }
                                }

                                g.fillPolygon(x_points,y_points,x_points.length);
                            }

                            break;
                        default:
                            System.out.println("Not a shape");
                            //put exception throw here.
                            break;
                    }
                }
            }

            //Change the colour back to the current pen colour
            g.setColor(pen_Colour);
            //Draw shape user is in the process of drawing
            switch (shape_Type.values()[selected_Tool]) {
                case LINE:
                    g.drawLine(x_Previous,y_Previous,x_Current,y_Current);
                    break;
                case RECTANGLE:
                    g.drawRect(Math.min(x_Previous, x_Current),Math.min(y_Previous, y_Current),Math.abs(x_Previous - x_Current),Math.abs(y_Previous - y_Current));
                    if(fill){
                        g.setColor(fill_Colour);
                        g.fillRect(Math.min(x_Previous, x_Current)+1,Math.min(y_Previous, y_Current)+1,Math.abs(x_Previous - x_Current)-1,Math.abs(y_Previous - y_Current)-1);
                    }
                    break;
                case PLOT:
                    g.drawOval(x_Previous,y_Previous,1,1);
                    break;
                case ELLIPSE:
                    g.drawOval(Math.min(x_Previous, x_Current),Math.min(y_Previous, y_Current),Math.abs(x_Previous - x_Current),Math.abs(y_Previous - y_Current));
                    if(fill){
                        g.setColor(fill_Colour);
                        g.fillOval(Math.min(x_Previous, x_Current)+1,Math.min(y_Previous, y_Current)+1,Math.abs(x_Previous - x_Current)-2,Math.abs(y_Previous - y_Current)-2);
                    }
                    break;
                case POLYGON:
                    g.drawLine(x_Previous,y_Previous,x_Current,y_Current);
                    for(int i = 3;i<polygon.size();i+=2){
                        g.drawLine((int)round(polygon.get(i-3)),(int)round(polygon.get(i-2)), (int)round(polygon.get(i-1)),(int)round(polygon.get(i)));
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(false);
        new GUI();
    }
}