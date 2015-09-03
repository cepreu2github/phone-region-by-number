package prbn;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class MainController {
    //GET - вернет время последнего обновления базы, POST - выполнит обновление
    @RequestMapping("/update/date")
    public String getUpdateDatetime() {
        return "31.12.2015";
    }
    //POST - отправит номера в CSV и вернет результат обработки
    @RequestMapping("/numbers")
    public String getRegionsForNumbers()
    {
        return "31.12.2015";
    }
    //POST - проверка одного номера
    @RequestMapping("/number")
    public String getRegionForNumber()
    {
        return "31.12.2015";
    }


}
