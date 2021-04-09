import java.util.Date;
import java.util.List;

/**
 * Interface for primary data record. Holds information about a call for emergency services.
 */
public interface IServiceRecord {

    /**
     * Gets caller name
     *
     * @return returns caller name
     */
    String getCallerName();

    /**
     * Gets user mobile
     *
     * @return returns user mobile
     */
    String getPhoneNumber();

    /**
     * Gets the time when the record was made
     *
     * @return returns record time
     */
    Date getRecordTime();

    /**
     * Gets services
     *
     * @return returns services
     */
    List<ServiceType> getRequestedServices();

    /**
     * Updates services array
     *
     * @param newServices new services
     */
    void updateServices(List<ServiceType> newServices);

    /**
     * Gets record description
     *
     * @return returns record description
     */
    String getRecordDescription();

    /**
     * Updates record description
     *
     * @param newDescription new description
     */
    void updateRecordDescription(String newDescription);

}
