package IO_Package;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader {
    // imp - not ready to use

    protected File file;
    protected Scanner scanner;

    public Reader(){
    }


    /*  Returns the next line in file
        If There is no nextLine - returns null  */
    public String getNextLine(){


        if( this.scanner != null && this.scanner.hasNextLine() ){
            return this.scanner.nextLine();
        }

        return null;
    }


    public Enum_IO openFile(String filePath){

        if ( this.scanner != null ){
            return Enum_IO.ALREADY_OPENED;
        }


        // Imp - check if filePath exists
        if ( !IO_Manager.pathExists(filePath) ){
            return Enum_IO.INVALID_PATH;
        }

        this.file = new File(filePath);

        // Try to create Scanner
        try {
            this.scanner = new Scanner(this.file);
            return Enum_IO.OPENED;
        } catch (FileNotFoundException exception){
            exception.printStackTrace();
        }

        return Enum_IO.ERROR;


    }


    public Enum_IO closeFile() {

        if( this.scanner != null ){
            this.scanner.close();
            this.scanner = null;
        }

        this.file = null;
        return Enum_IO.CLOSED;

    }
}