import java.util.Objects;

import static java.lang.Math.*;

public class BoundingBox {
    private double xmin;
    private double ymin;
    private double xmax;
    private double ymax;


    public double getXmin() {
        return xmin;
    }

    public double getYmin() {
        return ymin;
    }

    public double getXmax() {
        return xmax;
    }

    public double getYmax() {
        return ymax;
    }

    public BoundingBox(){
        this(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public BoundingBox(double xmin, double ymin, double xmax, double ymax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    /**
     * Powiększa BB tak, aby zawierał punkt (x,y)
     * Jeżeli był wcześniej pusty - wówczas ma zawierać wyłącznie ten punkt
     * @param x - współrzędna x
     * @param y - współrzędna y
     */
    void addPoint(double x, double y){
        if (isEmpty()){
            updateBoundingBox(x, y, x, y);
        }
        else {
            if (x < xmin){
                xmin = x;
            }
            if(x > xmax){
                xmax = x;
            }
            if (y < ymin){
                ymin = y;
            }
            if(y > ymax){
                ymax = y;
            }
        }
    }

    /**
     * Sprawdza, czy BB zawiera punkt (x,y)
     * @param x
     * @param y
     * @return
     */
    boolean contains(double x, double y){
        if (isEmpty()){
            return false;
        }
        return xmin <= x && xmax >= x && ymin <= y && ymax >= y;
    }

    /**
     * Sprawdza czy dany BB zawiera bb
     * @param bb
     * @return
     */
    boolean contains(BoundingBox bb){
        if (bb.isEmpty()){
            return true;
        }
        return !isEmpty() && xmin <= bb.getXmin() && xmax >= bb.getXmax() && ymin <= bb.getYmin() && ymax >= bb.getYmax();
    }

    /**
     * Sprawdza, czy dany BB przecina się z bb
     * @param bb
     * @return
     */
    boolean intersects(BoundingBox bb){
        if(isEmpty() && bb.isEmpty()){
            return false;
        }
        return !(xmin > bb.xmax) && !(bb.xmin > xmax) && !(ymin > bb.ymax) && !(bb.ymin > ymax);
    }

    /**
     * Powiększa rozmiary tak, aby zawierał bb oraz poprzednią wersję this
     * Jeżeli był pusty - po wykonaniu operacji ma być równy bb
     * @param bb
     * @return
     */
    BoundingBox add(BoundingBox bb){
        if (isEmpty()){
            updateBoundingBox(bb.getXmin(), bb.getYmin(), bb.getXmax(), bb.getYmax());
        }
        else {
            xmin = Math.min(xmin, bb.getXmin());
            xmax = Math.max(xmax, bb.getXmax());
            ymin = Math.min(ymin, bb.getYmin());
            ymax = Math.max(ymax, bb.getYmax());
        }
        return this;
    }
    /**
     * Sprawdza czy BB jest pusty
     * @return
     */
    boolean isEmpty(){
        return Double.isNaN(xmin) || Double.isNaN(xmax) || Double.isNaN(ymin) || Double.isNaN(ymax);
    }

    /**
     * Sprawdza czy
     * 1) typem o jest BoundingBox
     * 2) this jest równy bb
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BoundingBox that = (BoundingBox) o;
        if(isEmpty() && that.isEmpty()){
            return true;
        }
        return Double.compare(xmin, that.xmin) == 0 && Double.compare(ymin, that.ymin) == 0 && Double.compare(xmax, that.xmax) == 0 && Double.compare(ymax, that.ymax) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xmin, ymin, xmax, ymax);
    }

    /**
     * Oblicza i zwraca współrzędną x środka
     * @return if !isEmpty() współrzędna x środka else wyrzuca wyjątek
     * (sam dobierz typ)
     */
    double getCenterX(){
        if (isEmpty()){
            throw new EmptyBoundingBoxException();
        }
        return (xmax + xmin) / 2;
    }
    /**
     * Oblicza i zwraca współrzędną y środka
     * @return if !isEmpty() współrzędna y środka else wyrzuca wyjątek
     * (sam dobierz typ)
     */
    double getCenterY(){
        if (isEmpty()){
            throw new EmptyBoundingBoxException();
        }
        return (ymax + ymin) / 2;
    }

    /**
     * Oblicza odległość pomiędzy środkami this bounding box oraz bbx
     * @param bbx prostokąt, do którego liczona jest odległość
     * @return if !isEmpty odległość, else wyrzuca wyjątek lub zwraca maksymalną możliwą wartość double
     * Ze względu na to, że są to współrzędne geograficzne, zamiast odległości użyj wzoru haversine
     * (ang. haversine formula)
     *
     * Gotowy kod można znaleźć w Internecie...
     */
    double distanceTo(BoundingBox bbx){
        if (isEmpty() || bbx.isEmpty()){
            throw new EmptyBoundingBoxException();
        }
        double lat1 = toRadians(this.getCenterY());
        double lon1 = toRadians(this.getCenterX());

        double lat2 = toRadians(bbx.getCenterY());
        double lon2 = toRadians(bbx.getCenterX());

        final double R = 6371.0;

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) *
                        sin(dLon / 2) * sin(dLon / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));

        return R * c;
    }

    private void updateBoundingBox(double xmin, double ymin, double xmax, double ymax){
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "xmin=" + xmin +
                ", ymin=" + ymin +
                ", xmax=" + xmax +
                ", ymax=" + ymax +
                '}';
    }
}