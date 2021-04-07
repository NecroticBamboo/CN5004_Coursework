import java.util.Date;
import java.util.List;

public interface IServiceRecord {

    String getUserName();

    String getUserMobile();

    Date getRecordTime();

    List<ServiceType> getRecordServices();

    void updateServices(List<ServiceType> newServices);

    String getRecordDescription();

    void updateRecordDescription(String newDescription);

}
