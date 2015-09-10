package prbn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PhoneRegionByNumberApplication.class)
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class DBServiceTests {
    @Autowired
    IDBService dbService;

    private static final String emptyCsvContent = "АВС/ DEF;От;До;Емкость;Оператор;Регион\n";

    private static final String testCsvContent = "АВС/ DEF;От;До;Емкость;Оператор;Регион\n" +
            "301     ;       0 ;       999 ;       1000   ;       Открытое акционерное общество \"АСВТ\"    ;       Коврижки\n" +
            "302     ;       0 ;       999 ;       1000   ;       Открытое акционерное общество междугородной и международной электрической связи \"Ростелеком\"    ;       Черевички\n" +
            "303     ;       0 ;       999 ;       1000   ;       Открытое акционерное общество междугородной и международной электрической связи \"Ростелеком\"    ;       Черевички\n" +
            "301     ;       1000 ;       1999 ;       1000   ;       Открытое акционерное общество междугородной и международной электрической связи \"Ростелеком\"    ;       Моркошка\n" +
            "301     ;       2000 ;       9999 ;       8000    ;       Открытое акционерное общество междугородной и международной электрической связи \"Ростелеком\"    ;       Моркошка\n";

    private static final String modTestCsvContent = "АВС/ DEF;От;До;Емкость;Оператор;Регион\n" +
            "301     ;       0 ;       999 ;       1000   ;       Открытое акционерное общество \"АСВТ\"    ;       Коврижки\n" +
            "302     ;       0 ;       999 ;       1000   ;       Открытое акционерное общество междугородной и международной электрической связи \"Ростелеком\"    ;       Черевички\n" +
            "301     ;       1000 ;       1999 ;       1000   ;       Открытое акционерное общество междугородной и международной электрической связи \"Ростелеком\"    ;       Торопышка\n" +
            "301     ;       2000 ;       9999 ;       8000    ;       Открытое акционерное общество междугородной и международной электрической связи \"Ростелеком\"    ;       Моркошка\n" +
            "310     ;       2000 ;       9999 ;       8000    ;       Открытое акционерное общество междугородной и международной электрической связи \"Ростелеком\"    ;       Моркошка\n";


    @Test
    public void hashTests() {
        String oldDate = dbService.getUpdateDatetime();
        dbService.updateHash(MainController.columns[0], "newHash");
        assertEquals("newHash", dbService.getHash(MainController.columns[0]));
        assertNotEquals(dbService.getUpdateDatetime(), oldDate);
    }

    private Path createFileFromContent(String content) throws IOException {
        final Path tempPath = Files.createTempFile("dbupdate", ".csv");
        Files.delete(tempPath);
        Files.copy(new ByteArrayInputStream(content.getBytes("cp1251")), tempPath);
        return tempPath;
    }

    @Test
    public void mainTests() throws IOException {
        // remove old content
        Path emptyFile = createFileFromContent(emptyCsvContent);
        dbService.updateTableFromCSV(emptyFile.toString(), MainController.digits[0]);
        // create our content
        Path testFile = createFileFromContent(testCsvContent);
        dbService.fillTableFromCSV(testFile.toString());
        // modify
        Path modFile = createFileFromContent(modTestCsvContent);
        int[] linesCount = dbService.updateTableFromCSV(modFile.toString(), MainController.digits[0]);
        Files.delete(emptyFile);
        Files.delete(testFile);
        Files.delete(modFile);
        // check for correct remove and fill
        assertEquals(1, linesCount[0]);
        assertEquals(1, linesCount[1]);
        assertEquals(1, linesCount[2]);
        // check for number extraction
        assertEquals(dbService.checkNumber("3010003843").trim(), "Моркошка");
        assertEquals(dbService.checkNumber("3014003843"), "НЕИЗВЕСТНЫЙ РЕГИОН");
        assertEquals(dbService.checkNumber("3054003843"), "НЕИЗВЕСТНЫЙ РЕГИОН");
    }


}
