import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ServiceRecord implements IServiceRecord, Serializable {

    private final String userName;
    private final String userMobile;
    private final Date time;
    private List<ServiceType> services;
    private String description;

    public ServiceRecord(String userNameIn, String userMobileIn, Date timeIn, List<ServiceType> servicesIn, String descriptionIn){
        userName=userNameIn;
        userMobile=userMobileIn;
        time=timeIn;
        services=servicesIn;
        description=descriptionIn;
    }



    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getUserMobile() {
        return userMobile;
    }

    @Override
    public Date getRecordTime() {
        return time;
    }

    @Override
    public List<ServiceType> getRecordServices() {
        return services;
    }

    @Override
    public void updateServices(List<ServiceType> newServices) {
        services=newServices;
    }

    @Override
    public String getRecordDescription() {
        return description;
    }

    @Override
    public void updateRecordDescription(String newDescription) {
        description=newDescription;
    }

}
