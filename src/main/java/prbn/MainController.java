package prbn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
public class MainController {
    @Autowired
    IDBService dbService;

    /**
     * datetime of last database update
     *
     * @return
     */
    @RequestMapping(value = "/date", method = RequestMethod.GET)
    public String getUpdateDatetime() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "\"31.12.2014 13:08\"";
    }

    /**
     * update database
     *
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String performUpdate() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "{\"date\":\"20.10.2015 20:45\", \"inserted\":3, \"updated\": 1, \"deleted\": 2}";
    }

    /**
     * determine region for array of phone numbers
     *
     * @param file CSV with phone numbers
     * @return
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
     * get region for one phone number
     *
     * @param number
     * @return
     * @throws BadPhoneNumberException
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
