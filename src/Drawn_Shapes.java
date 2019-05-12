import java.util.ArrayList;

public class Drawn_Shapes{
    shape_Type Type;
    ArrayList<Double> coordinates;

    Drawn_Shapes(shape_Type Type, ArrayList<Double> coordinates){
        this.Type = Type;
        this.coordinates = coordinates;
    }
}