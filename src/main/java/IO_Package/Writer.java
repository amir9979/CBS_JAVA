package IO_Package;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer{
    // imp - not ready to use

    private File file;
    private BufferedWriter buffer;

    public Writer(){
    }


    public Enum_IO openFile(String folderPath, String fileName){

        if( this.buffer != null ){
            return Enum_IO.ALREADY_OPENED;
        }

        // Imp - check if filePath is valid
        String[] joinPaths = {folderPath, fileName};
        String filePath = IO_Manager.buildPath(joinPaths);

        if ( !IO_Manager.pathExists(folderPath) ||
                IO_Manager.pathExists(filePath) ){
            return Enum_IO.INVALID_PATH;
        }

        this.file = new File(filePath);

        // Try to create Buffer
        try {
            this.buffer = new BufferedWriter(new FileWriter(file));
            return Enum_IO.OPENED;
        }catch (IOException exception){
            exception.printStackTrace();
        }

        return Enum_IO.ERROR;
    }


    public Enum_IO writeText(String textToWrite){

        if ( this.buffer != null ){

            try{
                this.buffer.write(textToWrite);

                return Enum_IO.WROTE_SUCCESSFULLY;

            }catch ( IOException exception){
                exception.printStackTrace();
            }
        }
        return Enum_IO.ERROR;
    }


    public Enum_IO closeFile() {

        try{
            if( this.buffer == null){
                this.file = null;
                return Enum_IO.CLOSED;
            }

            this.buffer.close();
            this.buffer = null;
            this.file = null;

            return Enum_IO.CLOSED;

        }catch (IOException exception){
            exception.printStackTrace();

        }

        return Enum_IO.ERROR;
    }



}
