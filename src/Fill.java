import java.awt.*;

import static java.lang.Math.round;

public class Fill extends Shape{

   Color colour;
    //Constructors
    public Fill(){
        super(shape_Type.FILL,new Color(255,255,255));
        this.colour = null;
    }

    public Fill(Color colour){
        super(shape_Type.FILL,colour);
        this.colour = colour;
    }

    @Override
    public void draw(Graphics g, double width, double height) {
       // No draw functionality
    }

    @Override
    public void fill(Graphics g, double width, double height) {
       //No Fill functionality
    }

    public String save_Text(){
        //Colour Saving
        StringBuilder line = new StringBuilder();
        String hex;

        if(colour == null){
            hex = "OFF";
        }else{
            hex = "#"+Integer.toHexString(colour.getRGB()).substring(2);
        }

        line.append(shape_Type.FILL + " " + hex);
        return line.toString();
    }

    public shape_Type get_Shape(){
        return shape_Type.FILL;
    }

    public Color get_Colour(){
        return colour;
    }
}
