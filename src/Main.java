import java.io.IOException;
import java.util.Locale;

public class Main {

    static void testReader() throws IOException {
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

    static void testAdminUnitList() throws IOException {
        AdminUnitList adminUnitList = new AdminUnitList();
        adminUnitList.read("admin-units.csv");
        AdminUnit place = null;
//        AdminUnitList withPattern = adminUnitList.selectByName("sucha", false);
//        withPattern.list(System.out);
        for(AdminUnit unit: adminUnitList.units){
            if (unit.name.equals("Kraków")){
                place = unit;
                break;
            }
        }
        System.out.println(place);

        double t1 = System.nanoTime()/1e6;
        AdminUnitList neighboursOfPlace = adminUnitList.getNeighbors(place, 15);
        double t2 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"t2-t1=%f\n",t2-t1);

        for (AdminUnit neighbour: neighboursOfPlace.units){
            System.out.println(neighbour);
        }

    }

    public static void main(String[] args) throws IOException {
        testAdminUnitList();
    }
}