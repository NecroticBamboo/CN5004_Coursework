import java.io.IOException;
import java.util.List;

public interface IEmergencyService {
    /**
     * Creates a persisted record and returns ServiceRecordInfo
     * @param record what needs to be recorded
     * @return returns ServiceRecordInfo
     */
    ServiceRecordInfo addRecord(IServiceRecord record) throws IOException;

    /**
     * Searches and returns all records with the specified service type
     * @param service the specified service
     * @return returns list of records
     */
    List<ServiceRecordInfo> getByService(ServiceType service) ;

    /**
     * Searches and returns all records which match the specified mobile number
     * @param mobile the specified mobile number
     * @return returns list of records
     */
    List<ServiceRecordInfo> getByMobile(String mobile);

    /**
     * Returns all records
     * @return returns list of records
     */
    List<ServiceRecordInfo> getAllRecords();

    /**
     * Updates the specified record
     * @param record records that needs to be updated
     */
    void updateRecord(ServiceRecordInfo record) throws IOException;

    /**
     * Deletes the specified record
     * @param record record that needs to be deleted
     */
    void deleteRecord(ServiceRecordInfo record) throws UserInputException;

}
