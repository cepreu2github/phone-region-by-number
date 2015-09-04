package prbn;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class MainController {
    //GET - вернет время последнего обновления базы
    @RequestMapping("/date")
    public String getUpdateDatetime() {
        return "31.12.2015";
    }

    // POST - выполнит обновление
    @RequestMapping("/update")
    public String performUpdate() {
        return "31.12.2015";
    }

    //GET - отправит номера в CSV и вернет результат обработки
    @RequestMapping("/numbers")
    public String getRegionsForNumbers() {
        return "[{\"number\":\"81234356782\", \"region\":\"Тмутаракань\"}, {\"number\":\"84674356712\", \"region\":\"Залесье\"}]";
    }

    //GET - проверка одного номера
    @RequestMapping("/number")
    public String getRegionForNumber() {
        return "31.12.2015";
    }


}
