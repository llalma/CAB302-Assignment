import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.round;

public class Line extends Shape{

    private double x1,x2,y1,y2;

    /**
     * Constructs the Line shape.
     *
     * @param coords - ArrayList containing doubles which are the x and y coordinates of the shape. In that order
     */
    public Line(ArrayList<Double> coords){
        super(shape_Type.LINE,coords);
        x1 = coords.get(0);
        y1 = coords.get(1);
        x2 = coords.get(2);
        y2 = coords.get(3);
    }

    /**
     * Draw the outline of the shape with the current selected pen colour
     *
     * @param g - Graphics of place to draw,
     * @param width - Current width of drawing area
     * @param height - Current height of drawing area
     */
    public void draw(Graphics g,double width, double height){
        int x1_scaled = (int)round(x1 * width);
        int y1_scaled = (int)round(y1 * height);
        int x2_scaled = (int)round(x2 * width);
        int y2_scaled = (int)round(y2 * height);

        g.drawLine(x1_scaled,y1_scaled,x2_scaled,y2_scaled);
    }

    /**
     * No fill functionality for a line.
     *
     * @param g - Graphics of place to draw,
     * @param width - Current width of drawing area
     * @param height - Current height of drawing area
     */
    public void fill(Graphics g,double width, double height){
        //No Fill functionality
    }

    /**
     * Returns the string in the correct format for the VEC file, used for saving.
     *
     * @return - String that will be saved in the VEC file
     */
    public String save_Text(){
        return shape_Type.LINE + " " + x1 + " " + y1 + " " +  x2 + " " +  y2;
    }

}
