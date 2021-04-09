/**
 * Service record information. Holds a link to actual storage.
 * Do not use getFileName outside of EmergencyService class.
 */
public class ServiceRecordInfo {

    private final String fileName;
    private final IServiceRecord record;

    public ServiceRecordInfo(String fileNameIn, IServiceRecord recordIn) {
        fileName = fileNameIn;
        record = recordIn;
    }

    /**
     * Do not use outside of EmergencyService
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Get service record
     *
     * @return
     */
    public IServiceRecord getRecord() {
        return record;
    }

}
