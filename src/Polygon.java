import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.floor;
import static java.lang.Math.round;

public class Polygon extends Shape{

    private ArrayList<Double> coordinates =  new ArrayList<>();

    /**
     * Constructs the Polygon shape.
     *
     * @param coords - ArrayList containing doubles which are the x and y coordinates of the shape. In that order
     */
    public Polygon(ArrayList<Double> coords){
        super(shape_Type.POLYGON,coords);
        for(int i = 0;i<coords.size();i++){
            coordinates.add(coords.get(i));
        }
    }

    /**
     * Draw the outline of the shape with the current selected pen colour
     *
     * @param g - Graphics of place to draw,
     * @param width - Current width of drawing area
     * @param height - Current height of drawing area
     */
    public void draw(Graphics g,double width, double height){
        for(int i = 3;i<coordinates.size();i+=2){
            g.drawLine((int)round(coordinates.get(i-3)*width),(int)round(coordinates.get(i-2)*height), (int)round(coordinates.get(i-1)*width),(int)round(coordinates.get(i)*height));
        }
    }

    /**
     * Fill the shape with the current selected fill colour
     *
     * @param g - Graphics of place to draw,
     * @param width - Current width of drawing area
     * @param height - Current height of drawing area
     */
    public void fill(Graphics g,double width, double height){
        int size = coordinates.size();
        int[] x_points = new int[size/2];
        int[] y_points = new int[size/2];
        for(int i = 0;i<size;i++){
            if(i%2 == 0){
                //Even
                x_points[(int)round(floor(i/2))] =  (int)round(coordinates.get(i) * width);
            }else{
                y_points[(int)round(floor(i/2))] =  (int)round(coordinates.get(i) * height);
            }
        }

        g.fillPolygon(x_points,y_points,x_points.length);
    }

    /**
     * Returns the string in the correct format for the VEC file, used for saving.
     *
     * @return - String that will be saved in the VEC file
     */
    public String save_Text(){
        StringBuilder line = new StringBuilder();

        line.append(shape_Type.POLYGON.toString());

        //Minus 2 from size as to not include the last point which just direct back to the starting position
        for(int i = 0;i<coordinates.size()-2;i++){
            line.append(" " + coordinates.get(i));
        }

        return line.toString();
    }

}
