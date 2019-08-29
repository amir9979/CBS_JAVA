package IO_Package;

import java.util.HashSet;

public class IO_Manager {
    // imp - not ready to use

    private HashSet<String> openedPaths;


    private static IO_Manager ourInstance = new IO_Manager();

    public static IO_Manager getInstance() {
        return ourInstance;
    }

    private IO_Manager() {
        // Todo - check if hash set does
        //  'contains' properly on Strings
        this.openedPaths = new HashSet<String>();
    }


    public Reader getReader(String filePath){

        // imp - If path is currently opened
        if ( isOpen(filePath)) {
            return null;
        }


        Reader reader = new Reader();
        Enum_IO enum_io = reader.openFile(filePath);
        if(enum_io.equals(Enum_IO.OPENED)){
            openedPaths.add(filePath);
            return reader;
        }

        return null;

    }


    public Writer getWriter(String folderPath, String fileName){


        String[] joinPaths = {folderPath, fileName};
        String filePath = IO_Manager.buildPath(joinPaths);

        if ( isOpen(filePath)) {
            return null;
        }

        Writer writer = new Writer();
        Enum_IO enum_io = writer.openFile(folderPath, fileName);

        if(enum_io.equals(Enum_IO.OPENED)){
            openedPaths.add(filePath);
            return writer;
        }

        return null;

    }

    public static boolean pathExists(String filePath){
        // imp - Check if path exists (files and folders)

        return true;
    }

    public boolean isOpen(String filePath){
        return this.openedPaths.contains(filePath);
    }

    public static String buildPath(String[] input){

        // imp - concat input like: input[0] + "\" + input[1]

        return "";
    }

}
