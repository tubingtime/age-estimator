package com.company;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;


/*
Estimates age given a name, state and sex by using name data from Social Security.
Picks the year that the most people were born with that name and sex.
ie. 200 Men born in 1990 named Jeff vs. 100 Men born in 2000 named Jeff
since this year is 2021, jeff is 31.
 */
public class AgeEstimator<T> { // StateNode<List<People>>
    List<T> states; //flex list, either arraylist or linkedlist
    private static final String COMMA_DELIMETER = ",";

    public AgeEstimator(Properties properties) throws Exception {
        states = getListType(properties); //TODO: make new list in accordance with file size?
        String directory = properties.getProperty("Directory");
        if (directory == null)
            throw new Exception("ListType Parameter not found!");
        File sniffer = new File(directory); //sniffs out all the file names
        FilenameFilter txts = new FilenameFilter() { //only select files that end in .TXT
            @Override
            public boolean accept(File dir, String name) {
                return (name.endsWith(".TXT"));
            }
        };
        String[] pathnames = sniffer.list(txts);
        if (pathnames == null){
            throw new Exception("The provided directory is invalid."+"\nDirectory="+sniffer.getPath());
        }
        for (String pathname : pathnames) {
            pathname = pathname.replaceAll(".TXT",""); //remove .txt to get state code
            List<People> newList = (List<People>) getListType(properties);
            StateNode<List<People>> newNode = new StateNode<>(pathname, newList);
            states.add((T) newNode);
        }
        if(states.size() == 0)
            throw new Exception("The provided directory has no .TXT files!"+"\nDirectory="+sniffer.getPath());

        for (int state = 0; state < states.size(); state++) { // for all states provided
            //get the state object
            StateNode<List<People>> curState = (StateNode<List<People>>) states.get(state);
            FileReader filereader = new FileReader(properties.getProperty("Directory")+ curState.stateCode + ".TXT");
            BufferedReader buffReader = new BufferedReader(filereader);
            String line;

            // we are going to read data line by line
            // and add each person to list
            while ((line = buffReader.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMETER);
                int sex = 0;// 0 = male, 1 = female
                if(values[1].equals("F"))
                    sex = 1;
                //add person to the list
                curState.people.add(new People(values[3],sex,Integer.parseInt(values[2]),Integer.parseInt(values[4])));
                // :D
            }
            buffReader.close();
        }

    }

    public int[] estimateAge(String name, String stateCode, String pSex) throws Exception {
        int sex = 0;// 0 = male, 1 = female
        if(pSex.equals("F"))
            sex = 1;
        for (int state = 0; state < states.size(); state++) { // for all states provided
            //get the state object
            StateNode<List<People>> curState = (StateNode<List<People>>) states.get(state);
            if (curState.stateCode.equalsIgnoreCase(stateCode)){
                int curMax = 0; //max people with name
                int [] likelyAge = {-1, -1, -1}; //{ LikeleyAge, MinAge, MaxAge }
                // loop through people
                List<People> pList = curState.people;
                for (int person = 0; person < pList.size(); person++) {
                    People curP = pList.get(person);
                    if(curP.sex == sex && curP.name.equals(name)){ // if the sex & name matches
                        if(curP.count > curMax) {
                            curMax = curP.count;
                            likelyAge[0] = curP.birthday;
                            if(likelyAge[1] != -1) {
                                likelyAge[1] = -1; // so we know later to not use the range
                            }
                        }
                        else if (curP.count == curMax){ // if equal then we need to specify an age RANGE
                            if(likelyAge[1] == -1) { //first time special case
                                if(curP.birthday < likelyAge[0]) {
                                    likelyAge[1] = curP.birthday;
                                    likelyAge[2] = likelyAge[0];
                                } else {
                                    likelyAge[2] = curP.birthday;
                                    likelyAge[1] = likelyAge[0];
                                }
                                likelyAge[0] = -1; //so we know later to use the range
                            }
                            else if(curP.birthday < likelyAge[1]) // if curP was born earlier than current oldest
                                likelyAge[1] = curP.birthday;
                            else if (curP.birthday > likelyAge[2]) // else if curP was born later than current youngest
                                likelyAge[2] = curP.birthday;

                            }
                        }
                    }
                return likelyAge;
                }
            }
        int[] failed = {-1};
        return failed;
    }


    public List<T> getListType(Properties properties) throws Exception {
        String ListType = properties.getProperty("ListType");
        if (ListType == null)
            throw new Exception("ListType Parameter not found!");
        else if (ListType.equalsIgnoreCase("arraylist")) {
            return new ArrayList<>();
        }
        else if (ListType.equalsIgnoreCase("linkedlist")) {
            return new LinkedList<>();
        }
        else
            throw new Exception("Invalid List Type: "+ ListType);
    }

    public boolean isInvalidState(String code) throws Exception {
        for (int state = 0; state < states.size(); state++) { // for all states provided
            //get the state object
            StateNode<List<People>> curState = (StateNode<List<People>>) states.get(state);
            if (curState.stateCode.equalsIgnoreCase(code))
                return false;
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            InputStream stream = new FileInputStream("/home/thomas/IdeaProjects/WhatsMyAge/resources/props.properties");
            Properties props = new Properties();
            props.load(stream);
            stream.close();
            System.out.println("Adding Entries...");
            long totalTime = 0;
            long startTime = System.currentTimeMillis();
            AgeEstimator<StateNode<List<People>>> ageE = new AgeEstimator<>(props);
            totalTime += System.currentTimeMillis() - startTime;
            System.out.println("Total adding time = " + totalTime + "ms.");

            int currentYear = 2021;
            System.out.println("\n--== Welcome to the Age Estimator ==--");
            Scanner console = new Scanner(System.in);
            while (true){
                String gender, name, state;
                System.out.println("First name of the person (or EXIT to quit): ");
                String s = console.nextLine();
                if(s.equalsIgnoreCase("EXIT")) {
                    System.out.println("Exiting...");
                    break;
                }
                name = s;
                boolean invalid = false;
                do {
                    if (!invalid)
                        System.out.println("Gender (M/F): ");
                    else
                        System.out.println("Invalid entry. Enter M or F: ");
                    s = console.nextLine();
                    s = s.toUpperCase();
                    invalid = true;
                } while (!s.equalsIgnoreCase("F") && !s.equalsIgnoreCase("M"));
                gender = s;
                invalid = false;
                do {
                    if (!invalid)
                        System.out.println("State of birth (two letter state code): ");
                    else
                        System.out.println("No matching .TXT with given state code. Enter a two letter state code: ");
                    s = console.nextLine();
                    s = s.toUpperCase();
                    invalid = true;
                } while (ageE.isInvalidState(s));
                state = s;
                int[] birthYear = (ageE.estimateAge(name, state, gender));
                if (birthYear[0] == birthYear[1])
                    System.out.println("No person with name '"+name+"' found.");
                else if (birthYear[1] != -1){ //if we have a range
                    System.out.println(name + ", born in " + state + " is most likely to be anywhere from\n" +
                            (currentYear - birthYear[2]) +" - "+ (currentYear - birthYear[1])+" years old.");
                }
                else {
                    int age = currentYear - birthYear[0];
                    System.out.println(name + ", born in " + state + " is most likely around " + age + " years old.");
                }

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }
}
