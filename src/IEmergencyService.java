import java.io.IOException;
import java.util.List;

public interface IEmergencyService {
    KeyValue addRecord(IServiceRecord record) throws IOException;

    List<KeyValue> getByService(ServiceType service) ;

    List<KeyValue> getByMobile(String mobile);

    List<KeyValue> getAllRecords();

    void updateRecord(KeyValue record) throws IOException;

    void deleteRecord(KeyValue record) throws UserInputException;

}
