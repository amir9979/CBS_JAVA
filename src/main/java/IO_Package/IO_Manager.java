package IO_Package;

import java.io.File;
import java.util.HashSet;

public class IO_Manager {
    // testme

    private HashSet<String> openedPaths;
    public static final String workingDirectory = System.getProperty("user.dir") + "\\src\\main";

    private static IO_Manager ourInstance = new IO_Manager();

    public static IO_Manager getInstance() {
        return ourInstance;
    }

    private IO_Manager() {
        // Todo - check if HashSet does 'contains' properly on Strings
        this.openedPaths = new HashSet<String>();
    }


    public boolean addOpenPath(String toAdd){

        return this.openedPaths.add(toAdd);
    }

    public boolean removeOpenPath(String toRemove){

        if( ! this.openedPaths.contains(toRemove)){
            return true;
        }
        return this.openedPaths.remove(toRemove);
    }


    public Reader getReader(String filePath){

        if ( isOpen(filePath)) {
            return null;
        }


        Reader reader = new Reader();
        Enum_IO enum_io = reader.openFile(filePath);
        if(enum_io.equals(Enum_IO.OPENED)){
            this.openedPaths.add(filePath);
            return reader;
        }

        return null;

    }


    public Writer getWriter(String folderPath, String fileName){

        String filePath = IO_Manager.buildPath(new String[]{folderPath, fileName});

        if ( isOpen(filePath)) {
            return null;
        }

        Writer writer = new Writer();
        Enum_IO enum_io = writer.openFile(folderPath, fileName);

        if(enum_io.equals(Enum_IO.OPENED)){
            this.openedPaths.add(filePath);
            return writer;
        }

        return null;

    }

    public static boolean pathExists(File file){
        return file.exists();
    }

    public static boolean isDirectory(File directory){
        return directory.isDirectory();
    }

    public Enum_IO deleteFile(File toDelete){


        if ( pathExists(toDelete)) {
            // fixme - toDelete file exists but won't delete ( sometimes does )
            if (toDelete.delete()){
                this.removeOpenPath(toDelete.getPath());
                return Enum_IO.DELETED;
            }
        }else {
            return Enum_IO.INVALID_PATH;
        }

        return Enum_IO.ERROR;
    }

    public boolean isOpen(String filePath){
        return this.openedPaths.contains(filePath);
    }

    public static String buildPath(String[] input){

        if( input == null || input.length == 0){
            return null;
        }

        // concat input in format: input[0] + "\" + input[1]
        String result = input[0];
        for (int i = 1; i < input.length ; i++) {
            result += "\\" + input[i];
        }

        return result;
    }



}
