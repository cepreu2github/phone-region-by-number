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
}
