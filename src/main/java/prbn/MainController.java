package prbn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
public class MainController {
    @Autowired
    IDBService dbService;

    private static final String[] urls = {
            "http://rossvyaz.ru/docs/articles/Kody_ABC-3kh.csv",
            "http://rossvyaz.ru/docs/articles/Kody_ABC-4kh.csv",
            "http://rossvyaz.ru/docs/articles/Kody_ABC-8kh.csv",
            "http://rossvyaz.ru/docs/articles/Kody_DEF-9kh.csv"
    };
    private static final String[] columns = {"ABC-3_key", "ABC-4_key", "ABC-8_key", "DEF-9_key"};
    private static final int[] digits = {3, 4, 8, 9};

    /**
     * @return datetime of last database update
     */
    @RequestMapping(value = "/date", method = RequestMethod.GET)
    public String getUpdateDatetime() {
        return "\"" + dbService.getUpdateDatetime() + "\"";
    }

    /**
     * update database
     *
     * @return statistics about update process (lines inserted, updated, deleted) and current datetime from DB
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String performUpdate() throws IOException {
        int countNew = 0;
        int countMod = 0;
        int countDel = 0;
        // for every of 4 files
        for (int i=0; i<4; i++){
            // get hash from DB
            String hash = dbService.getHash(columns[i]);
            // get etag from remote server
            URL url = new URL(urls[i]);
            URLConnection connection = url.openConnection();
            Map<String, List<String>> map = connection.getHeaderFields();
            String newHash = map.get("ETag").get(0);
            // if old hash not exist or not equal to new
            if (hash == null || !hash.equals(newHash)) {
                // write CSV to temporary file
                final Path tempPath = Files.createTempFile("dbupdate", ".csv");
                Files.delete(tempPath);
                try (InputStream input = url.openConnection().getInputStream()) {
                    Files.copy(input, tempPath);
                }
                // if old hash does not exist
                if (hash == null) {
                    // load data from CSV to DB table
                    countNew = dbService.fillTableFromCSV(tempPath.toString());
                // if hash not equal to new
                } else if (!hash.equals(newHash)){
                    // update data in DB using this recipe:
                    int[] result = dbService.updateTableFromCSV(tempPath.toString(), digits[i]);
                    countNew += result[0];
                    countMod += result[1];
                    countDel += result[2];
                }
                Files.delete(tempPath);
                // write new hash and datetime to DB
                dbService.updateHash(columns[i], newHash);
            }
        }
        String newDate = dbService.getUpdateDatetime();
        return "{\"date\":\"" + newDate + "\", \"inserted\":" + Integer.toString(countNew) +
                ", \"updated\":" + Integer.toString(countMod) + ", \"deleted\": " + Integer.toString(countDel) + "}";
    }

    /**
     * @param file CSV with phone numbers
     * @return list of regions for array of phone numbers
     * @throws IOException
     * @throws BadPhoneNumberException in case of incorrect number in list
     */
    @RequestMapping(value = "/numbers", method = RequestMethod.POST)
    public String getRegionsForNumbers(@RequestParam("file") MultipartFile file) throws IOException, BadPhoneNumberException {
        if (!file.isEmpty()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(file.getInputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            String line = null;
            while((line = in.readLine())!= null){
                sb.append("{\"number\":\"");
                sb.append(line);
                sb.append("\", \"region\":\"");
                sb.append(dbService.checkNumber(validateNumber(line)).trim());
                sb.append("\"},");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append("]");
            return sb.toString();
            //return "[{\"number\":\"81234356782\", \"region\":\"Тмутаракань\"}, {\"number\":\"84674356712\", \"region\":\"Залесье\"}]";
        }
        throw  new BadPhoneNumberException("\"Нераспознан формат списка номеров. Используйте CSV.\"");
    }

    private String validateNumber(String number) throws BadPhoneNumberException {
        if (number == null)
            throw new BadPhoneNumberException("\"некорректный номер телефона\"");
        number = number.replaceAll("\\D+","");
        if (number.length() == 0)
            throw new BadPhoneNumberException("\"некорректный номер телефона\"");
        if (number.charAt(0) == '8' || number.charAt(0) == '7')
            number = number.substring(1);
        if (number.length() != 10)
            throw new BadPhoneNumberException("\"некорректный номер телефона\"");
        return number;
    }

    /**
     * @param number phone can be formatted in different ways: "8 (143) 248 12 54", "+7234-1235612", etc. All
     *               acceptable.
     * @return region for one phone number
     * @throws BadPhoneNumberException if it is not phone number
     */
    @RequestMapping(value = "/number", method = RequestMethod.GET)
    public String getRegionForNumber(@RequestParam String number) throws BadPhoneNumberException {
        return "\"" + dbService.checkNumber(validateNumber(number)).trim() + "\"";
    }

    @ExceptionHandler(BadPhoneNumberException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleException(BadPhoneNumberException e) {
        return e.getMessage();
    }

    private class BadPhoneNumberException extends Exception {
        public BadPhoneNumberException(String s) {
            super(s);
        }
    }
}
