public class KeyValue {

    private final String fileName;
    private final IServiceRecord record;

    public KeyValue(String fileNameIn, IServiceRecord recordIn){
        fileName=fileNameIn;
        record=recordIn;
    }

    public String getFileName(){
        return fileName;
    }

    public IServiceRecord getRecord(){
        return record;
    }

}
