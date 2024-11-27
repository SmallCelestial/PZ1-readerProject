import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

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


    private BoundingBox getBoundingBoxFromReader(CSVReader reader){
        double xmin = 0;
        double xmax = 0;
        double ymin = 0;
        double ymax = 0;

        try{
            xmin = Collections.min(List.of(reader.getDouble("x1"), reader.getDouble("x2"),
                    reader.getDouble("x3"), reader.getDouble("x4"), reader.getDouble("x5"))
            );
        }
        catch (NumberFormatException ignored){
        }

        try{
            xmax = Collections.max(List.of(reader.getDouble("x1"), reader.getDouble("x2"),
                    reader.getDouble("x3"), reader.getDouble("x4"), reader.getDouble("x5"))
            );
        }
        catch (NumberFormatException ignored){
        }

        try{
            ymin = Collections.min(List.of(reader.getDouble("y1"), reader.getDouble("y2"),
                    reader.getDouble("y3"), reader.getDouble("y4"), reader.getDouble("y5"))
            );
        }
        catch (NumberFormatException ignored){
        }

        try{
            ymax = Collections.max(List.of(reader.getDouble("y1"), reader.getDouble("y2"),
                    reader.getDouble("y3"), reader.getDouble("y4"), reader.getDouble("y5"))
            );
        }
        catch (NumberFormatException ignored){
        }

        return new BoundingBox(xmin, ymin, xmax, ymax);
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

    private void fixMissingValues(){
        for(AdminUnit unit : units){
            unit.fixMissingValues();
        }
    }

}

