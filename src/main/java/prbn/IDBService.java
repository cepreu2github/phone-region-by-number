package prbn;

/**
 * here we incapsulate all database interactions
 */
public interface IDBService {
    /**
     * @return datetime of last DB update
     */
    String getUpdateDatetime();

    /**
     * @param column which hash to return
     * @return hash from specified column
     */
    String getHash(String column);

    /**
     * @param column which file hash
     * @param hash new hash
     */
    void updateHash(String column, String hash);

    /**
     * @param pathToFile where CSV file placed
     * @return new rows count
     */
    int fillTableFromCSV(String pathToFile);

    /**
     * @param firstDigit 3x, 4x, 9x, etc...
     * @param pathToFile  where CSV file placed
     * @return rows count [inserted, updated, deleted]
     */
    int[] updateTableFromCSV(String pathToFile, int firstDigit);

    /**
     * @param number input
     * @return region
     */
    String checkNumber(String number);
}
