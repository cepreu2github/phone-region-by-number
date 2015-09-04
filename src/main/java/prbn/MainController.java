package prbn;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
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

    /**
     * @return datetime of last database update
     */
    @RequestMapping(value = "/date", method = RequestMethod.GET)
    public String getUpdateDatetime() {
        return dbService.getUpdateDatetime();
    }

    /**
     * update database
     *
     * @return statistics about update process (lines inserted, updated, deleted) and current datetime from DB
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String performUpdate() throws IOException {
        for (int i=0; i<4; i++){
            String hash = dbService.getHash(columns[i]);
            URL url = new URL(urls[i]);
            URLConnection connection = url.openConnection();
            Map<String, List<String>> map = connection.getHeaderFields();
            String newHash = map.get("ETag").get(0);
            if (hash == null || !hash.equals(newHash)) {
                try (InputStream input = url.openConnection().getInputStream()) {

                }
            }
        }
        // для каждого из 4-х файлов
            // считываем из БД хеш
            // получаем etag
            // если старый хеш отсутствует или несовпадает с полученным
                // пишем CSV в файл на диске
            // если старый хеш отсутсвует
                // грузим данные в таблицу из CSV
            // если хеш не совпадает с полученным
                // пользуемся этим рецептом - http://stackoverflow.com/questions/21495600/import-csv-to-update-rows-in-table/21495728#21495728
            // обновляем хеш в БД
        // TODO: вычислять обновленные, удаленные, добавленные можно по этому рецепту - http://stackoverflow.com/a/27902396
        return "{\"date\":\"20.10.2015 20:45\", \"inserted\":3, \"updated\": 1, \"deleted\": 2}";
    }

    /**
     * @param file CSV with phone numbers
     * @return list of regions for array of phone numbers
     * @throws IOException
     * @throws BadPhoneNumberException in case of incorrect number in list
     */
    @RequestMapping(value = "/numbers", method = RequestMethod.POST)
    public String getRegionsForNumbers(@RequestParam("file") MultipartFile file) throws IOException, BadPhoneNumberException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!file.isEmpty()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String firstLine = in.readLine();
            if (firstLine.length() >= 8)
                return "[{\"number\":\"81234356782\", \"region\":\"Тмутаракань\"}, {\"number\":\"84674356712\", \"region\":\"Залесье\"}]";
        }
        throw  new BadPhoneNumberException("\"Нераспознан формат списка номеров. Используйте CSV.\"");
    }

    /**
     * @param number phone can be formatted in different ways: "8 (143) 248 12 54", "+7234-1235612", etc. All
     *               acceptable.
     * @return region for one phone number
     * @throws BadPhoneNumberException if it is not phone number
     */
    @RequestMapping(value = "/number", method = RequestMethod.GET)
    public String getRegionForNumber(@RequestParam String number) throws BadPhoneNumberException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (number.length()> 4)
            return "\"Междуречье\"";
        else
            throw new BadPhoneNumberException("\"некорректный номер телефона\"");
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
