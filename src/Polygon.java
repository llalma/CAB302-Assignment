import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.floor;
import static java.lang.Math.round;

public class Polygon extends Shape{

    private ArrayList<Double> coordinates =  new ArrayList<>();
    //Constructor
    public Polygon(ArrayList<Double> coords){
        super(shape_Type.POLYGON,coords);
        for(int i = 0;i<coords.size();i++){
            coordinates.add(coords.get(i));
        }
    }

    public void draw(Graphics g,double width, double height){
        for(int i = 3;i<coordinates.size();i+=2){
            g.drawLine((int)round(coordinates.get(i-3)*width),(int)round(coordinates.get(i-2)*height), (int)round(coordinates.get(i-1)*width),(int)round(coordinates.get(i)*height));
        }
    }

    public void fill(Graphics g,double width, double height){
        int size = coordinates.size();
        int[] x_points = new int[size/2];
        int[] y_points = new int[size/2];
        for(int i = 0;i<size;i++){
            if(i%2 == 0){
                //Even
                x_points[(int)round(floor(i/2))] =  (int)round(coordinates.get(i));
            }else{
                y_points[(int)round(floor(i/2))] =  (int)round(coordinates.get(i));
            }
        }

        g.fillPolygon(x_points,y_points,x_points.length);
    }

    public String save_Text(){
        StringBuilder line = new StringBuilder();

        line.append(shape_Type.POLYGON.toString());

        //Minus 2 from size as to not include the last point which just direct back to the starting position
        for(int i = 0;i<coordinates.size()-2;i++){
            line.append(" " + coordinates.get(i));
        }

        return line.toString();
    }

    public shape_Type get_Shape(){
        return shape_Type.POLYGON;
    }
}
