package prbn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DBService implements IDBService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        String sql = "USE phones";
        jdbcTemplate.execute(sql);
    }

    @Override
    public String getUpdateDatetime() {
        String sql = "SELECT `lastupdated` FROM `config` WHERE id = 1";
        String date = (String)jdbcTemplate.queryForObject(sql, null, String.class);
        if (date == null)
            return "\"НИКОГДА\"";
        return date;
    }

    @Override
    public String getHash(String column) {
        String sql = "SELECT `"+ column +"` FROM `config` WHERE id = 1";
        String hash = (String)jdbcTemplate.queryForObject(sql, null, String.class);
        return hash;
    }

    @Override
    public int fillTableFromCSV(String pathToFile) {
        jdbcTemplate.execute("SET GLOBAL local_infile = 1;\n");
        jdbcTemplate.execute("SET character_set_database=cp1251;\n");
        jdbcTemplate.execute("LOAD DATA LOCAL INFILE '" + pathToFile + "' \n" +
                "INTO TABLE numbers \n" +
                "FIELDS TERMINATED BY ';' \n" +
                "LINES TERMINATED BY '\\n'\n" +
                "IGNORE 1 ROWS (`prefix`, `start`, `end`, @dummy, @dummy, `region`);");
        jdbcTemplate.execute("SET character_set_database=default;\n");
        jdbcTemplate.execute("DELETE FROM numbers WHERE `region`='';");
        Integer count = (Integer)jdbcTemplate.queryForObject("SELECT COUNT(*) FROM numbers;\n",
                null, Integer.class);
        return count;
    }
}
