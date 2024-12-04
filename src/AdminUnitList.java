import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Predicate;

public class AdminUnitList {
    List<AdminUnit> units;

    public AdminUnitList() {
        this(new ArrayList<>());
    }

    public AdminUnitList(List<AdminUnit> units) {
        this.units = units;
    }

    /**
     * Czyta rekordy pliku i dodaje do listy
     * @param filename nazwa pliku
     */

    public void read(String filename) throws IOException {
        CSVReader reader = new CSVReader(filename);
        Map<Long, AdminUnit> adminUnitsById = new HashMap<>();
        Map<AdminUnit, Long> idOfAdminUnit = new HashMap<>();
        Map<AdminUnit, Long> idOfParentIdByReference = new HashMap<>();
        Map<Long,List<AdminUnit>> parentIdToChildren = new HashMap<>();

        while (reader.next()){
            AdminUnit adminUnit = getAdminUnitFromReader(reader);
            long parentId;
            try{
                parentId = reader.getLong("parent");
            }
            catch (NumberFormatException e){
                parentId = 0L;
            }
            Long id = reader.getLong("id");
            adminUnitsById.put(id, adminUnit);
            idOfAdminUnit.put(adminUnit, id);
            idOfParentIdByReference.put(adminUnit, parentId);
            units.add(adminUnit);

            if (!parentIdToChildren.containsKey(parentId)){
                parentIdToChildren.put(parentId, new ArrayList<>());
                parentIdToChildren.get(parentId).add(adminUnit);
            }
            else {
                parentIdToChildren.get(parentId).add(adminUnit);
            }

        }

        for (AdminUnit unit : units) {
            var parentId = idOfParentIdByReference.get(unit);
            var parent = adminUnitsById.get(parentId);
            unit.setParent(parent);

            unit.setChildren(parentIdToChildren.get(idOfAdminUnit.get(unit)));
        }
        fixMissingValues();

    }

    /**
     * Zwraca listę jednostek sąsiadujących z jendostką unit na tym samym poziomie hierarchii admin_level.
     * Czyli sąsiadami wojweództw są województwa, powiatów - powiaty, gmin - gminy, miejscowości - inne miejscowości
     * @param unit - jednostka, której sąsiedzi mają być wyznaczeni
     * @param maxdistance - parametr stosowany wyłącznie dla miejscowości, maksymalny promień odległości od środka unit,
     *                    w którym mają sie znaleźć punkty środkowe BoundingBox sąsiadów
     * @return lista wypełniona sąsiadami
     */
    AdminUnitList getNeighbors(AdminUnit unit, double maxdistance){
        AdminUnitList neighbors = new AdminUnitList();
        int adminLevel = unit.adminLevel;
        for (AdminUnit adminUnit : units) {
            if (adminUnit.adminLevel != adminLevel || !unit.bbox.intersects(adminUnit.bbox)){
                continue;
            }
            if (adminLevel != 8){
                neighbors.units.add(adminUnit);
            }

            else if (unit.bbox.distanceTo(adminUnit.bbox) <= maxdistance){
                neighbors.units.add(adminUnit);
            }
        }
        return neighbors;
    }

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInPlaceByName(){
        class NameComparator implements Comparator<AdminUnit> {
            @Override
            public int compare(AdminUnit o1, AdminUnit o2) {
                return o1.name.compareTo(o2.name);
            }
        }

        Comparator<AdminUnit> comparator = new NameComparator();

        units.sort(comparator);
        return this;
    }

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInPlaceByArea(){
        units.sort(new Comparator<AdminUnit>() {
            @Override
            public int compare(AdminUnit o1, AdminUnit o2) {
                return Double.compare(o1.area, o2.area);
            }
        });
        return this;
    }

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInPlaceByPopulation(){
        units.sort((o1, o2) -> Integer.compare(o1.population, o2.population));
        return this;
    }

    AdminUnitList sortInPlace(Comparator<AdminUnit> cmp){
        units.sort(cmp);
        return this;
    }

    AdminUnitList sort(Comparator<AdminUnit> cmp) {
        AdminUnitList sortedUnits = new AdminUnitList(new ArrayList<>(units));
        sortedUnits.sortInPlace(cmp);
        return sortedUnits;
    }

    /**
     *
     * @param pred referencja do interfejsu Predicate
     * @return nową listę, na której pozostawiono tylko te jednostki,
     * dla których metoda test() zwraca true
     */
    AdminUnitList filter(Predicate<AdminUnit> pred){
        List<AdminUnit> filtered = new ArrayList<>();
        for (AdminUnit unit : units) {
            if (pred.test(unit)) {
                filtered.add(unit);
            }
        }
        return new AdminUnitList(filtered);
    }


    /**
     * Zwraca co najwyżej limit elementów spełniających pred
     * @param pred - predykat
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    AdminUnitList filter(Predicate<AdminUnit> pred, int limit){
        List<AdminUnit> filtered = new ArrayList<>();
        int counter = 0;
        for (AdminUnit unit : units) {
            if (counter == limit) {
                break;
            }
            if (pred.test(unit)) {
                filtered.add(unit);
                counter++;
            }
        }
        return new AdminUnitList(filtered);
    }

    /**
     * Zwraca co najwyżej limit elementów spełniających pred począwszy od offset
     * Offest jest obliczany po przefiltrowaniu
     * @param pred - predykat
     * @param - od którego elementu
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    AdminUnitList filter(Predicate<AdminUnit> pred, int offset, int limit){
        AdminUnitList result = new AdminUnitList();
        var filtered = result.units;
        int counter = 0;
        for (AdminUnit unit : units) {
            if (counter == limit) {
                break;
            }
            if (pred.test(unit)) {
                counter++;
                if (counter > offset) {
                    filtered.add(unit);
                }
            }
        }
        return result;
    }

    /**
     * Wypisuje zawartość korzystając z AdminUnit.toString()
     * @param out
     */
    void list(PrintStream out){
        for (AdminUnit unit : units){
            out.println(unit.toString());
        }
    }

    /**
     * Wypisuje co najwyżej limit elementów począwszy od elementu o indeksie offset
     * @param out - strumień wyjsciowy
     * @param offset - od którego elementu rozpocząć wypisywanie
     * @param limit - ile (maksymalnie) elementów wypisać
     */
    void list(PrintStream out,int offset, int limit ){
        for(int actualIndex = offset, printedCount = 0; printedCount < limit && actualIndex < units.size(); offset++, printedCount++){
            out.println(units.get(actualIndex));
        }
    }

    /**
     * Zwraca nową listę zawierającą te obiekty AdminUnit, których nazwa pasuje do wzorca
     * @param pattern - wzorzec dla nazwy
     * @param regex - jeśli regex=true, użyj finkcji String matches(); jeśli false użyj funkcji contains()
     * @return podzbiór elementów, których nazwy spełniają kryterium wyboru
     */
    AdminUnitList selectByName(String pattern, boolean regex){
        AdminUnitList ret = new AdminUnitList();
        for (AdminUnit unit : units){
            if (regex){
                if (unit.name.matches(pattern)){
                    ret.units.add(unit);
                }
            }
            else{
                if (unit.name.contains(pattern)){
                    ret.units.add(unit);
                }

            }
        }
        return ret;
    }


    private BoundingBox getBoundingBoxFromReader(CSVReader reader){
        BoundingBox boundingBox = new BoundingBox();
        try{
            boundingBox.addPoint(reader.getDouble("x1"), reader.getDouble("y1"));
            boundingBox.addPoint(reader.getDouble("x2"), reader.getDouble("y2"));
            boundingBox.addPoint(reader.getDouble("x3"), reader.getDouble("y3"));
            boundingBox.addPoint(reader.getDouble("x4"), reader.getDouble("y4"));
        }catch (NumberFormatException ignored){}

        return boundingBox;
    }

    private AdminUnit getAdminUnitFromReader(CSVReader reader){
        String name;
        int adminLevel;
        int population;
        double area;
        double density;
        BoundingBox boundingBox;

        name = reader.get("name");
        try {
            adminLevel = reader.getInt("admin_level");
        }
        catch (NumberFormatException e){
            adminLevel = 0;
        }

        try {
            population = reader.getInt("population");
        }
        catch (NumberFormatException e){
            population = 0;
        }

        try {
            area = reader.getDouble("area");
        }
        catch (NumberFormatException e){
            area = 0;
        }

        try {
            density = reader.getDouble("density");
        }
        catch (NumberFormatException e){
            density = 0;
        }

        boundingBox = getBoundingBoxFromReader(reader);

        return new AdminUnit(name, area, adminLevel, population, density, boundingBox);

    }

    private void fixMissingValues(){
        for(AdminUnit unit : units){
            unit.fixMissingValues();
        }
    }

}

