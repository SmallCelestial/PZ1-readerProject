import java.util.List;

public class AdminUnit {
    public String name;
    int adminLevel;
    int population;
    double area;
    double density;
    List<AdminUnit> children;
    AdminUnit parent;
    BoundingBox bbox;

    public void setChildren(List<AdminUnit> children) {
        this.children = children;
    }
    public void setParent(AdminUnit parent) {
        this.parent = parent;
    }

    public AdminUnit() {
    }

    public AdminUnit(String name, int adminLevel, int population, double area, double density, AdminUnit parent, BoundingBox bbox) {
        this.name = name;
        this.adminLevel = adminLevel;
        this.population = population;
        this.area = area;
        this.density = density;
        this.parent = parent;
        this.bbox = bbox;
    }

    public AdminUnit(String name, double area, int adminLevel, int population, double density, BoundingBox bbox) {
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
                ", bbox=" + bbox +
                '}';
    }

    void fixMissingValues() {
        if (density == 0 && population == 0) {
            density = parent.findEstimatedDensity();
            population = (int)(area * density);
        }
    }

    double findEstimatedDensity() {
        double density = this.density;
        while (density == 0 && parent != null) {
            density = parent.findEstimatedDensity();
        }
        return density;
    }
}