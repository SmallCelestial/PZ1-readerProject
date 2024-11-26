import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        CSVReader reader = new CSVReader("admin-units.csv",",(?=([^\"]*\"[^\"]*\")*[^\"]*$)",true);
        for(int i=0; i<100; ++i){
            reader.next();
            for(int j=0; j<17; ++j){
                try{
                    if(j==2){
                        System.out.print(reader.get(j) + " ");
                    }
                    else if(j<5){
                        System.out.print(reader.getInt(j) + " ");
                    }
                    else {
                        System.out.print(reader.getDouble(j) + " ");
                    }
                }
                catch(NumberFormatException e){
                    System.out.print(reader.get(j) + " ");
                }

            }
            System.out.println();
        }
    }
}