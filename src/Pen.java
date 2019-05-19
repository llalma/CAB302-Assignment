import java.awt.*;

public class Pen extends Shape{

    Color colour;
    //Constructos
    public Pen(){
        super(shape_Type.PEN,new Color(0,0,0));
        this.colour = new Color(0,0,0);
    }

    public Pen(Color colour){
        super(shape_Type.PEN,colour);
        this.colour = colour;
    }

    @Override
    public void draw(Graphics g, double width, double height) {
        //No draw functionality
    }

    @Override
    public void fill(Graphics g, double width, double height) {
        //No fill functionality
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

        line.append(shape_Type.PEN + " " + hex);
        return line.toString();
    }

    public shape_Type get_Shape(){
        return shape_Type.PEN;
    }

    public Color get_Colour(){
        return colour;
    }
}
