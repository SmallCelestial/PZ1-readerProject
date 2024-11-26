import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class CSVReaderTest {
    private CSVReader reader;

    @BeforeEach
    void setUp() throws IOException {
        reader = new CSVReader("titanic-part.csv",",(?=([^\"]*\"[^\"]*\")*[^\"]*$)",true);
    }


    @Test
    public void readCSVFile() throws IOException {
        while(reader.next()){
            int id = reader.getInt("PassengerId");
            String name = reader.get("Name");
            double fare = reader.getDouble("Fare");

            System.out.printf(Locale.US,"%d %s %f\n",id, name, fare);
        }
    }

    @Test
    void testGetByHeader() throws IOException {
        // given
        reader.next();

        // when
        String fare = reader.get("Fare");
        String cabin = reader.get("Cabin");

        // then
        assertEquals("7.25", fare);
        assertEquals("", cabin);
        assertThrows(InvalidHeaderNameException.class, ()->reader.get("Invalid header"));
    }

    @Test
    void testGetByIndex() throws IOException {
        // given
        reader.next();

        // when
        String fare = reader.get(9);
        String cabin = reader.get(10);

        // then
        assertEquals("7.25", fare);
        assertEquals("", cabin);
        assertThrows(InvalidIndexException.class, ()->reader.get(-1));
    }

    @Test
    void testGetIntByHeader() throws IOException {
        // given
        reader.next();

        // when
        int age = reader.getInt("Age");

        // then
        assertEquals(22, age);
        assertThrows(NumberFormatException.class, ()->reader.getInt("Fare"));
        assertThrows(InvalidHeaderNameException.class, ()->reader.getInt("Invalid header"));

    }

    @Test
    void testGetIntByIndex() throws IOException {
        // given
        reader.next();

        // when
        int age = reader.getInt(5);

        // then
        assertEquals(22, age);
        assertThrows(NumberFormatException.class, ()->reader.getInt(10));
        assertThrows(InvalidIndexException.class, ()->reader.getInt(-1));
    }

    @Test
    void testGetDoubleByHeader() throws IOException {
        // given
        reader.next();

        // when
        double fare = reader.getDouble("Fare");

        // then
        assertEquals(7.25, fare);
        assertThrows(NumberFormatException.class, ()->reader.getDouble("Cabin"));
        assertThrows(InvalidHeaderNameException.class, ()->reader.getDouble("Invalid header"));

    }

    @Test
    void testGetDoubleByIndex() throws IOException {
        // given
        reader.next();

        // when
        double fare = reader.getDouble(9);

        // then
        assertEquals(7.25, fare);
        assertThrows(NumberFormatException.class, ()->reader.getDouble(10));
        assertThrows(InvalidIndexException.class, ()->reader.getDouble(-1));
    }

    @Test
    void testGetLongByHeader() throws IOException {
        // given
        reader.next();

        // when
        Long age = reader.getLong("Age");

        // then
        assertEquals(22L, age);
        assertThrows(NumberFormatException.class, ()->reader.getLong("Cabin"));
        assertThrows(InvalidHeaderNameException.class, ()->reader.getLong("Invalid header"));
    }

    @Test
    void testGetLongByIndex() throws IOException {
        // given
        reader.next();

        // when
        Long age = reader.getLong(5);

        // then
        assertEquals(22L, age);
        assertThrows(NumberFormatException.class, ()->reader.getLong(10));
        assertThrows(InvalidIndexException.class, ()->reader.getLong(-1));
    }

    @Test
    void testIfReaderCanReadFromString() throws IOException {
        // given
        String text = "a,b,c\n123.4,567.8,91011.12";
        String[] expectedFirstLine = new String[]{"123.4", "567.8", "91011.12"};
        reader = new CSVReader(new StringReader(text),",",true);

        // when
        reader.next();

        // then
        assertEquals(expectedFirstLine[0], reader.get("a"));
        assertEquals(expectedFirstLine[1], reader.get("b"));
        assertEquals(expectedFirstLine[2], reader.get("c"));
    }

    @Test
    void getTime() throws IOException {
        //given
        reader = new CSVReader("dates.csv",",",true);
        LocalTime expectedTime = LocalTime.parse("14:35", DateTimeFormatter.ofPattern("HH:mm"));

        // when
        reader.next();

        // then
        assertEquals(expectedTime, reader.getTime("Time", "HH:mm"));
        assertEquals(expectedTime, reader.getTime(1, "HH:mm"));
    }

    @Test
    void getDate() throws IOException {
        //given
        reader = new CSVReader("dates.csv",",",true);
        LocalDate expectedDate = LocalDate.parse("2023-11-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // when
        reader.next();

        // then
        assertEquals(expectedDate, reader.getDate("Date", "yyyy-MM-dd"));
        assertEquals(expectedDate, reader.getDate(0, "yyyy-MM-dd"));
    }


}