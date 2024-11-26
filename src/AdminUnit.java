public class AdminUnit {
    String name;
    int adminLevel;
    double population;
    double area;
    double density;
    AdminUnit parent;
    BoundingBox bbox;

    public AdminUnit() {}

    public AdminUnit(String name, int adminLevel, double population, double area, double density, AdminUnit parent, BoundingBox bbox) {
        this.name = name;
        this.adminLevel = adminLevel;
        this.population = population;
        this.area = area;
        this.density = density;
        this.parent = parent;
        this.bbox = bbox;
    }

    public AdminUnit(String name, double area, int adminLevel, double population, double density, BoundingBox bbox) {
        this(name, adminLevel, population, area, density, new AdminUnit(), bbox);
    }

    @Override
    public String toString() {
        return "AdminUnit{" +
                "name='" + name + '\'' +
                ", adminLevel=" + adminLevel +
                ", population=" + population +
                ", area=" + area +
                ", density=" + density +
                '}';
    }
}