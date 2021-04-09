/**
 * Lambda interface for records searching
 */
public interface ISearchCriteria {

    /**
     * Filter function.
     *
     * @param record
     * @return true if record matches the criteria
     */
    boolean match(IServiceRecord record);
}
