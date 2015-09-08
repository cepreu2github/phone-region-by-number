package prbn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
            return "НИКОГДА";
        return date;
    }

    @Override
    public String getHash(String column) {
        String sql = "SELECT `"+ column +"` FROM `config` WHERE id = 1";
        String hash = (String)jdbcTemplate.queryForObject(sql, null, String.class);
        return hash;
    }

    @Override
    public void updateHash(String column, String hash) {
        jdbcTemplate.update("UPDATE `config` SET `" + column + "` = ?, `lastupdated` = NOW() WHERE `id` = 1", hash);
    }

    private void fillTableFromCSV(String tableName, String pathToFile){
        jdbcTemplate.execute("SET GLOBAL local_infile = 1;\n");
        jdbcTemplate.execute("SET character_set_database=cp1251;\n");
        jdbcTemplate.execute("LOAD DATA LOCAL INFILE '" + pathToFile + "' \n" +
                "INTO TABLE `" + tableName + "` \n" +
                "FIELDS TERMINATED BY ';' \n" +
                "LINES TERMINATED BY '\\n'\n" +
                "IGNORE 1 ROWS (`prefix`, `start`, `end`, @dummy, @dummy, `region`);");
        jdbcTemplate.execute("SET character_set_database=default;\n");
        jdbcTemplate.execute("DELETE FROM `" + tableName + "` WHERE `region`='';");
    }

    @Override
    public int fillTableFromCSV(String pathToFile) {
        fillTableFromCSV("numbers", pathToFile);
        Integer count = (Integer)jdbcTemplate.queryForObject("SELECT COUNT(*) FROM numbers;\n",
                null, Integer.class);
        return count;
    }

    @Override
    public int[] updateTableFromCSV(String pathToFile, int firstDigit) {
        jdbcTemplate.execute("CREATE TEMPORARY TABLE IF NOT EXISTS `temp_updates` (" +
                "`prefix` INT NOT NULL, `start` INT NOT NULL, `end` INT NOT NULL,\n" +
                "    `region` VARCHAR(250) NOT NULL, PRIMARY KEY (`prefix`, `start`, `end`));");
        fillTableFromCSV("temp_updates", pathToFile);
        int updated = jdbcTemplate.update("UPDATE `numbers` AS n INNER JOIN `temp_updates` AS t \n" +
                "\tON n.region != t.region AND\n" +
                "\tn.prefix = t.prefix AND\n" +
                "\tn.start = t.start AND\n" +
                "\tn.end = t.end SET n.prefix = t.prefix, n.start = t.start,\n" +
                "\tn.end = t.end, n.region = t.region; -- updated");
        int inserted = jdbcTemplate.update("INSERT INTO `numbers` SELECT t.* " +
                "FROM `temp_updates` AS t LEFT OUTER JOIN `numbers` AS n \n" +
                        "\tON n.prefix = t.prefix AND\n" +
                        "\tn.start = t.start AND\n" +
                        "\tn.end = t.end WHERE n.prefix IS NULL; -- inserted");
        int deleted = jdbcTemplate.update("DELETE n FROM `numbers` AS n " +
                        "LEFT OUTER JOIN `temp_updates` AS t \n" +
                        "\tON n.prefix = t.prefix AND\n" +
                        "\tn.start = t.start AND\n" +
                        "\tn.end = t.end WHERE t.prefix IS NULL \n" +
                        "\tAND LEFT(n.prefix, 1) = ?; -- deleted",
                new Object[]{firstDigit});
        jdbcTemplate.execute("DROP TABLE `temp_updates`;");
        return new int[]{inserted, updated, deleted};
    }

    @Override
    public String checkNumber(String number) {
        String region = null;
        String sql = "SELECT `region` FROM `numbers` WHERE `prefix` = LEFT(?, 3) " +
                "AND `start` <= RIGHT(?, 7) AND `end` >= RIGHT(?, 7)";
        try{
            region = (String)jdbcTemplate.queryForObject(sql, new String[]{number, number, number}, String.class);
        } catch (EmptyResultDataAccessException e){
            region = "НЕИЗВЕСТНЫЙ РЕГИОН";
        }
        return region;
    }
}
