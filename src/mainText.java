import java.io.IOException;
import java.util.*;

public class mainText {

    private static IEmergencyService emergencyService;
    private static final int pageSize = 5;

    static {
        try {
            emergencyService = new EmergencyService("Records");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String[] mainMenuOptions = {
            "Add new record",
            "Get all records",
            "Get record by service",
            "Get record by mobile",
            "Exit"
    };

    private static final String[] findMenuOptions = {
            "Update record",
            "Remove record",
            "Next five records",
            "Previous five records",
            "Back"
    };

    private static final String[] availableServices = {
            "Fire",
            "Ambulance",
            "Police"
    };

    private static final String[] updateOptions = {
            "Add services",
            "Update Description",
            "Back"
    };

    public static void main(String[] args) {
        // main menu loop
        int choice = -1;
        do {
            try {
                choice = menuIteration();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        } while (choice != mainMenuOptions.length);
    }

    /**
     * Iterates over the main menu options.
     *
     * @return returns user choice.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static int menuIteration() throws IOException, ClassNotFoundException {
        int choice;
        List<ServiceRecordInfo> list;
        System.out.println("u1821659");
        choice = displayMenu(mainMenuOptions, "Main menu");

        System.out.println(choice);
        switch (choice) {
            case 1:
                addNewRecord();
                break;
            case 2:
                list = emergencyService.getAllRecords();
                showRecordMenu(list);
                break;
            case 3:
                list = emergencyService.getByService(askUserForServiceType());
                showRecordMenu(list);
                break;
            case 4:
                list = emergencyService.getByMobile(getValidMobile("Enter mobile: "));
                showRecordMenu(list);
                break;
            case 5:
                break;
            default:
                System.out.println("Please re-enter your choice");
                break;
        }
        return choice;
    }

    /**
     * Asks user to select service
     *
     * @return converts user input and returns it
     */
    private static ServiceType askUserForServiceType() {
        int servicesChoice = displayMenu(availableServices, "Choose service: ", true); //error here
        return convertToServiceType(servicesChoice);
    }

    /**
     * Asks user to enter a integer value.
     * Checks if the int variable is between min and max
     * Asks to rewrite if wrong int is entered
     *
     * @param min      minimum of int value
     * @param max      maximum of int value
     * @param question prints this at the beginning
     * @return returns user value
     */
    private static int getValidInt(int min, int max, String question) {
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.print(question);
            try {
                choice = sc.nextInt();
                if (choice >= min && choice <= max)
                    return choice;
            } catch (Exception e) {
                // we need to read invalid input here. otherwise next call to nextXXX() will fail as well
                sc.nextLine();
            }
            System.out.println("Enter number between " + min + " and " + max);
        } while (true);
    }

    /**
     * Asks user to enter a mobile.
     * Checks if the variable matches the regex
     * Asks to rewrite if wrong value is entered
     *
     * @param question the question that is displayed at the start
     * @return returns number
     */
    private static String getValidMobile(String question) {
        Scanner sc = new Scanner(System.in);
        String number;
        do {
            System.out.println(question);
            number = sc.nextLine();
            if (number.matches("\\+?[0-9][0-9\\-]+") && number.length() < 25) {
                return number;
            }
            System.out.println("Your phone number is wrong!");
        } while (true);
    }

    /**
     * Displays menu
     *
     * @param menuOptions what items to display in the menu
     * @param menuHeader  what is the header of the menu
     * @return returns user choice and prevents user from entering smt wrong
     */

    private static int displayMenu(String[] menuOptions, String menuHeader) {
        return displayMenu(menuOptions, menuHeader, true);
    }

    /**
     * Displays menu
     *
     * @param menuOptions   what items to display in the menu
     * @param menuHeader    what is the header of the menu
     * @param validateInput flag that decides the maximum
     * @return returns user choice and prevents user from entering smt wrong
     */
    private static int displayMenu(String[] menuOptions, String menuHeader, boolean validateInput) {
        int choice;
        do {
            System.out.println();
            System.out.println(menuHeader);
            for (int i = 0; i < menuOptions.length; i++) {
                System.out.println((i + 1) + ") " + menuOptions[i]);
            }

            System.out.print("Enter your choice: ");
            choice = getValidInt(1, validateInput ? menuOptions.length : Integer.MAX_VALUE, "");

            if (choice <= 0) {
                System.out.println("Invalid choice. Please, re-enter.");
            }

        } while (choice <= 0);
        return choice;
    }

    /**
     * Adds new record to the system
     *
     * @throws IOException
     */
    private static void addNewRecord() throws IOException {

        Scanner sc = new Scanner(System.in);
        System.out.println("What is the caller name?");
        String name = sc.nextLine();

        String phoneNumber = getValidMobile("What is callers' mobile number?");
        Date date = new Date();

        //method for adding services
        List<ServiceType> services;
        do {
            try {
                services = selectServices();
                break;
            } catch (UserInputException e) {
                // skip
            }
        } while (true);

        System.out.println("What is the complaint: ");
        String description = sc.nextLine();

        IServiceRecord newRecord = new ServiceRecord(name, phoneNumber, date, services, description);
        emergencyService.addRecord(newRecord);
    }

    /**
     * Asks user to select available services
     *
     * @return ArrayList of services
     */
    private static List<ServiceType> selectServices() {
        Set<ServiceType> services = new HashSet<>();

        int servicesChoice = displayMenu(availableServices, "What services do the caller require?", false);
        for (; servicesChoice != 0; servicesChoice /= 10) {

            ServiceType serviceType = convertToServiceType(servicesChoice % 10);
            services.add(serviceType);
        }

        return new ArrayList<>(services);
    }

    /**
     * Converts an integer value into ServiceType
     *
     * @param serviceTypeN variable that needs to be converted
     * @return returns the converted variable
     */
    private static ServiceType convertToServiceType(int serviceTypeN) {
        ServiceType serviceType;
        switch (serviceTypeN) {
            case 1:
                serviceType = ServiceType.Fire;
                break;
            case 2:
                serviceType = ServiceType.Ambulance;
                break;
            case 3:
                serviceType = ServiceType.Police;
                break;
            default:
                throw new UserInputException("Invalid service number.");
        }
        return serviceType;
    }

    /**
     * Method that displays the Find menu and asks user to enter its choice
     *
     * @param list List with records
     * @throws IOException
     */
    private static void showRecordMenu(List<ServiceRecordInfo> list) throws IOException {
        int choice;
        int fromWhere = 0;

        do {
            displayRecords(list, fromWhere);

            if (list.isEmpty()) {
                return;
            }

            choice = displayMenu(findMenuOptions, "What would you like to do?");
            switch (choice) {
                case 1:
                    //update
                    ServiceRecordInfo selected = selectRecord(list, fromWhere);
                    if (selected != null) {
                        if (showUpdateRecordMenu(selected)) {
                            emergencyService.updateRecord(selected);
                            System.out.println("File updated");
                        }
                    }
                    break;
                case 2:
                    //remove
                    ServiceRecordInfo toDelete = selectRecord(list, fromWhere);
                    if (toDelete != null) {
                        emergencyService.deleteRecord(toDelete);
                        list.remove(toDelete);
                        System.out.println("file deleted");
                    }
                    break;
                case 3:
                    fromWhere = fromWhere + pageSize;
                    if (fromWhere > list.size()) {
                        fromWhere = list.size() - pageSize;
                    }
                    if (fromWhere < 0) {
                        fromWhere = 0;
                    }
                    break;
                case 4:
                    fromWhere = fromWhere - pageSize;
                    if (fromWhere < 0) {
                        fromWhere = 0;
                    }
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Please re-enter your choice");
                    break;
            }
        } while (choice != findMenuOptions.length);
    }

    /**
     * Asks user to select record
     *
     * @param list List of records
     * @return returns a single record
     */
    private static ServiceRecordInfo selectRecord(List<ServiceRecordInfo> list, int fromWhere) {
        if (fromWhere >= list.size()) {
            return null;
        }
        int choice = getValidInt(1, Math.min(list.size() - fromWhere, pageSize), "Select record ");
        return list.get(fromWhere + choice - 1);
    }

    /**
     * Menu that asks user select what to update
     *
     * @param record a record that needs to be updated
     * @return returns updated record
     */
    private static boolean showUpdateRecordMenu(ServiceRecordInfo record) {
        boolean changed = false;
        int choice;
        Scanner sc = new Scanner(System.in);
        do {
            choice = displayMenu(updateOptions, "What would you like to update?");
            switch (choice) {
                case 1:
                    //add service
                    record.getRecord().updateServices(selectServices());
                    displayRecord(record.getRecord());
                    changed = true;
                    break;
                case 2:
                    //update description
                    System.out.println("Updated description: ");
                    record.getRecord().updateRecordDescription(sc.nextLine());
                    displayRecord(record.getRecord());
                    changed = true;
                    break;
                case 3:
                    return changed;
                default:
                    System.out.println("Something went wrong!");
                    break;
            }
        } while (true);
    }

    /**
     * Displays five records from the list
     *
     * @param list      List of records
     * @param fromWhere starting point
     */
    private static void displayRecords(List<ServiceRecordInfo> list, int fromWhere) {
        if (list == null || list.size() == 0 ) {
            System.out.print("\n========== No records found ==========\n\n");
        } else if (fromWhere < 0 || fromWhere >= list.size()) {
            System.out.println("\n========== Nothing to show ==========\n\n");
        } else {
            System.out.println();
            for (int count = 0; count < 5; count++) {
                int i = count + fromWhere;
                if (i >= list.size()) {
                    break;
                }
                System.out.print((count + 1) + ") ");
                displayRecord(list.get(i).getRecord());
                System.out.println("---------------");
            }
        }
    }

    /**
     * Method that displays record
     *
     * @param record the records that needs to be displayed.
     */
    private static void displayRecord(IServiceRecord record) {
        System.out.println("Caller name:   " + record.getCallerName());
        System.out.println("Caller mobile: " + record.getPhoneNumber());
        System.out.println("Date:          " + record.getRecordTime());
        System.out.println("Services:      " + record.getRequestedServices());
        System.out.println("Description:   " + record.getRecordDescription());
    }

}
