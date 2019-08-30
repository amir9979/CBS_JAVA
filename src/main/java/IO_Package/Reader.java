package IO_Package;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader {
    // Testme - ready to be tested

    private File file;
    private Scanner scanner;
    private IO_Manager io_manager;

    public Reader(){
        this.io_manager = IO_Manager.getInstance();
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

        this.file = new File(filePath);

        if ( !IO_Manager.pathExists(this.file) ){
            return Enum_IO.INVALID_PATH;
        }


        // Try to create Scanner
        try {
            this.scanner = new Scanner(this.file);

           // if ( this.io_manager.addOpenPath(this.file.getPath()) )
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

        if( this.file == null ){
            return Enum_IO.CLOSED;
        }

        if (IO_Manager.getInstance().removeOpenPath(this.file.getPath()) ){
            this.file = null;
            return Enum_IO.CLOSED;
        }

        return Enum_IO.ERROR;

    }
}
