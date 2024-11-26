import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CSVReader {
    private final BufferedReader reader;
    private final String delimiter;
    private boolean hasHeader;
    private final List<String> columnLabels = new ArrayList<>();
    private final Map<String,Integer> columnLabelsToInt = new HashMap<>();
    private String[]current;

    /**
     *
     * @param filename - nazwa pliku
     * @param delimiter - separator pól
     * @param hasHeader - czy plik ma wiersz nagłówkowy
     */

    public CSVReader(String filename,String delimiter,boolean hasHeader) throws IOException {
        reader = new BufferedReader(new FileReader(filename));
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if(hasHeader)parseHeader();
    }

    public CSVReader(String filename,String delimiter) throws IOException {
        this(filename, delimiter, true);
    }

    public CSVReader(String filename) throws IOException {
        this(filename, ",", true);
    }

    CSVReader(Reader reader, String delimiter, boolean hasHeader) throws IOException {
        this.reader = new BufferedReader(reader);
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if(hasHeader)parseHeader();
    }

    void parseHeader() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return;
        }
        String[] header = line.split(delimiter);

        for (int i = 0; i < header.length; i++) {
            columnLabels.add(header[i]);
            columnLabelsToInt.put(header[i],i);
        }
    }

    boolean next() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return false;
        }

        this.current = line.split(delimiter);
        return true;
    }

    List<String> getColumnLabels(){
        return columnLabels;
    }

    int getRecordLength(){
        return current.length;
    }

    boolean isMissing(int columnIndex){
        return current[columnIndex]==null;
    }

    boolean isMissing(String columnLabel){
        return isMissing(columnLabelsToInt.get(columnLabel));
    }

    String get(int columnIndex){
        if(columnIndex < 0 || columnIndex >= current.length){
            throw new InvalidIndexException("Column index out of bounds");
        }
        if (isMissing(columnIndex)){
            return "";
        }
        return current[columnIndex];
    }

    String get(String columnLabel) {
        Integer header_number = columnLabelsToInt.get(columnLabel);
        if (header_number == null) {
            throw new InvalidHeaderNameException("There is no header \"%s\" ".formatted(columnLabel));
        }
        return get(header_number);
    }

    int getInt(int columnIndex){
        return Integer.parseInt(get(columnIndex));
    }

    int getInt(String columnLabel){
        Integer header_number = columnLabelsToInt.get(columnLabel);
        if (header_number == null) {
            throw new InvalidHeaderNameException("There is no header \"%s\" ".formatted(columnLabel));
        }
        return getInt(header_number);

    }

    double getDouble(int columnIndex){
        return Double.parseDouble(get(columnIndex));
    }

    double getDouble(String columnLabel) {
        Integer header_number = columnLabelsToInt.get(columnLabel);
        if (header_number == null) {
            throw new InvalidHeaderNameException("There is no header \"%s\" ".formatted(columnLabel));
        }
        return getDouble(header_number);
    }

    long getLong(int columnIndex){
        return Long.parseLong(get(columnIndex));
    }

    long getLong(String columnLabel) {
        Integer header_number = columnLabelsToInt.get(columnLabel);
        if (header_number == null) {
            throw new InvalidHeaderNameException("There is no header \"%s\" ".formatted(columnLabel));
        }
        return getLong(header_number);
    }

    LocalTime getTime(int columnIndex, String format){
        String time = get(columnIndex);
        return LocalTime.parse(time, DateTimeFormatter.ofPattern(format));
    }

    LocalTime getTime(String columName, String format){
        String time = get(columName);
        return LocalTime.parse(time, DateTimeFormatter.ofPattern(format));
    }

    LocalDate getDate(int columnIndex, String format){
        String date = get(columnIndex);
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
    }

    LocalDate getDate(String columnName, String format){
        String date = get(columnName);
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
    }

}