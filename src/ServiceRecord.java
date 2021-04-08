import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Primary data record. Holds information about a call for emergency services.
 * Implements IServiceRecord
 */
public class ServiceRecord implements IServiceRecord, Serializable {

    private final String callerName;
    private final String phoneNumber;
    private final Date time;
    private List<ServiceType> services;
    private String description;

    /**
     * Constructor
     * @param callerNameIn
     * @param proneNumberIn
     * @param timeIn
     * @param servicesIn
     * @param descriptionIn
     */
    public ServiceRecord(String callerNameIn, String proneNumberIn, Date timeIn, List<ServiceType> servicesIn, String descriptionIn){
        callerName =callerNameIn;
        phoneNumber =proneNumberIn;
        time=timeIn;
        services=servicesIn;
        description=descriptionIn;
    }

    @Override
    public String getCallerName() {
        return callerName;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public Date getRecordTime() {
        return time;
    }

    @Override
    public List<ServiceType> getRequestedServices() {
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
