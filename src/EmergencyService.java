import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**\
 * Implementation for IEmergencyService. This class responsible for IServiceRecord storage (store, modify and retrieve).
 * It uses simple files as a backing storage.
 */
public class EmergencyService implements IEmergencyService{

    private final String folderName;

    /**
     * Constructor for EmergencyService
     * If folder doesn't exist, create one
     * @param folderNameIn a folder where records are stored
     * @throws IOException
     */
    public EmergencyService(String folderNameIn) throws IOException{
        folderName=folderNameIn;
        if(!Files.exists(Paths.get(folderName))){
            Files.createDirectories(Paths.get(folderName));
        }
    }

    /**
     * Adds record to the folder
     * @param record Record that needs to be stored in the folder
     * @throws IOException
     */
    @Override
    public ServiceRecordInfo addRecord(IServiceRecord record) throws IOException{

        String formattedDate= new SimpleDateFormat("yyyy-MM-dd").format(record.getRecordTime());
        String folder=folderName+"/"+formattedDate;

        if(!Files.exists(Paths.get(folder))){
            Files.createDirectories(Paths.get(folder));
        }

        UUID uuid = UUID.randomUUID();
        String fileName=folder+"/"+uuid.toString()+".obf";
        write(record,fileName);

        return new ServiceRecordInfo(fileName,record);
    }

    private static void write(IServiceRecord record,String fileName) throws IOException{
        File file = new File(fileName);
        file.createNewFile();
        try(FileOutputStream recordsFile=new FileOutputStream(file);
            ObjectOutputStream recordStream=new ObjectOutputStream(recordsFile)){

            recordStream.writeObject(record);

        }catch(IOException e){
//            System.out.println("There was a problem writing the file");
            throw e;
        }

    }

    /**
     * Check each file for search criteria (caller supplied lambda).
     * @param criteria
     * @return Records matching criteria
     */
    private List<ServiceRecordInfo> search(ISearchCriteria criteria) {
        List<ServiceRecordInfo> recordsList=new ArrayList<>();
        File dir = new File(folderName);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                File[] fileNames=child.listFiles();
                if(fileNames==null){
                    continue;
                }
                for(File data: fileNames){
                    // Do something with child
                    try(FileInputStream recordsFile=new FileInputStream(data);
                        ObjectInputStream recordStream=new ObjectInputStream(recordsFile)){

                        IServiceRecord temp = (IServiceRecord) recordStream.readObject();
                        if(criteria.match(temp)){
                            ServiceRecordInfo serviceRecordInfo =new ServiceRecordInfo(data.getAbsolutePath(),temp);
                            recordsList.add(serviceRecordInfo);
                        }

                    }catch(IOException | ClassNotFoundException e){
                        //this will ignore all invalid files.
                        e.printStackTrace();
                    }
                }
            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
            return recordsList;
        }
        return recordsList;
    }

    /**
     * A search by service method
     * @param service input value
     * @return Returns a List of records and records file names with the specified service
     */
    @Override
    public List<ServiceRecordInfo> getByService(ServiceType service) {
        return search((x)->x.getRequestedServices().contains(service));
    }

    /**
     * A search by mobile method
     * @param mobile input value
     * @return Returns a List of records and records file names with the specified mobile number
     */
    @Override
    public List<ServiceRecordInfo> getByMobile(String mobile) {
        return search((x)->x.getPhoneNumber().equals(mobile));
    }

    /**
     * Returns all records in the database
     * @return Returns a List of records and records file names
     */
    @Override
    public List<ServiceRecordInfo> getAllRecords() {
        return search((x)->true);
    }

    /**
     * Updates a record
     * @param record an updated version of the record
     * @throws IOException
     */
    @Override
    public void updateRecord(ServiceRecordInfo record) throws IOException{
        write(record.getRecord(),record.getFileName());
    }

    /**
     * Deletes a record
     * @param record A record that needs to be deleted
     * @throws UserInputException
     */
    @Override
    public void deleteRecord(ServiceRecordInfo record) throws UserInputException {
        File temp= new File(record.getFileName());
        if(!temp.delete()){
            throw new UserInputException("Cant delete the file.");
        }
    }
}
