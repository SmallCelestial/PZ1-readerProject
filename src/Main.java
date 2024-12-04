import java.io.IOException;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Predicate;

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

    static AdminUnitList readPlaces() throws IOException {
        AdminUnitList adminUnitList = new AdminUnitList();
        adminUnitList.read("admin-units.csv");
        return adminUnitList;
    }

    static void testAdminUnitList() throws IOException {
        AdminUnitList adminUnitList = readPlaces();
        AdminUnitList withPattern = adminUnitList.selectByName("sucha", false);
        withPattern.list(System.out);

    }

    static void readStartsWithZOrderByArea() throws IOException {
        AdminUnitList list = readPlaces();
        var out = System.out;
        list.filter(a->a.name.startsWith("Ż")).sortInPlaceByArea().list(out);
        System.out.println(out);
    }

    static void readStartsWithKOrderByArea() throws IOException {
        AdminUnitList list = readPlaces();
        var out = System.out;
        list.filter(a->a.name.startsWith("K")).sortInPlaceByName().list(out);
    }

    static void districtsWithParentAsMalopolskie() throws IOException {
        AdminUnitList list = readPlaces();
        var out = System.out;
        list.filter(a->(a.adminLevel == 6 && a.parent.name.equals("województwo małopolskie"))).list(out);
    }

    static void exmapleWithAndOr() throws IOException {
        AdminUnitList list = readPlaces();
        Predicate<AdminUnit> hasLargePopulation = unit -> unit.population < 500_000;
        Predicate<AdminUnit> startsWithW = unit -> unit.name.startsWith("W");
        Predicate<AdminUnit> endsWithK = unit -> unit.name.endsWith("k");

        Predicate<AdminUnit> complexCriteria = hasLargePopulation.and(startsWithW.or(endsWithK));

        var out = System.out;
        list.filter(complexCriteria).list(out);
    }

    static void testGettingNeigbours() throws IOException {
        AdminUnitList adminUnitList = readPlaces();
        AdminUnit place = null;
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

    static void testGettingNeigboursHierarchical() throws IOException {
        AdminUnitList adminUnitList = readPlaces();
        AdminUnit place = null;
        for(AdminUnit unit: adminUnitList.units){
            if (unit.name.equals("Kraków")){
                place = unit;
                break;
            }
        }
        System.out.println(place);

        double t1 = System.nanoTime()/1e6;
        AdminUnitList neighboursOfPlace = adminUnitList.getNeighborsHierarchical(place, 15);
        double t2 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"t2-t1=%f\n",t2-t1);

        for (AdminUnit neighbour: neighboursOfPlace.units){
            System.out.println(neighbour);
        }
    }

    static void testQuery0(AdminUnitList list) throws IOException {
        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(list)
                .where(a->a.area>1000)
                .or(a->a.name.startsWith("Sz"))
                .sort((a,b)->Double.compare(a.area,b.area))
                .limit(100);
        query.execute().list(System.out);
    }

    static void testQuery1(AdminUnitList list) throws IOException {
        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(list)
                .where(a -> a.population > 100_000)
                .and(a -> a.name.endsWith("ów"))
                .sort(Comparator.comparing(a -> a.name))
                .limit(50);
        query.execute().list(System.out);
    }

    static void testQuery2(AdminUnitList list) throws IOException {
        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(list)
                .where(a -> a.area < 500)
                .or(a -> a.name.startsWith("K"))
                .sort((a, b) -> Double.compare(b.population, a.population))
                .offset(10)
                .limit(30);
        query.execute().list(System.out);
    }

    static void testQuery3(AdminUnitList list) throws IOException {
        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(list)
                .where(a -> a.population >= 100_000 && a.population <= 500_000)
                .and(a -> a.name.contains("a"))
                .sort((a, b) -> Double.compare(a.area, b.area))
                .limit(20);
        query.execute().list(System.out);
    }


    public static void main(String[] args) throws IOException {
        StringBuilder line_break = new StringBuilder("\n");
        line_break.append("*".repeat(30));
        line_break.append("\n");
//        testAdminUnitList();
//        readStartsWithKOrderByArea();
//        districtsWithParentAsMalopolskie();
//        exmapleWithAndOr();
//
//        AdminUnitList list = readPlaces();
//        testQuery0(list);
//
//        System.out.println(line_break);
//        testQuery1(list);
//
//        System.out.println(line_break);
//        testQuery2(list);
//
//        System.out.println(line_break);
//        testQuery3(list);

        testGettingNeigbours();
        System.out.println(line_break);
        testGettingNeigboursHierarchical();
    }
}