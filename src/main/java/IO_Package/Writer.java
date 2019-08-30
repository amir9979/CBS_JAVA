package IO_Package;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer{
    // Testme - passed the tests

    private File file;
    private BufferedWriter buffer;

    public Writer(){
    }


    public Enum_IO openFile(String folderPath, String fileName){

        if( this.buffer != null ){
            return Enum_IO.ALREADY_OPENED;
        }


        this.file = new File(folderPath, fileName);
        File directory = new File(folderPath);

        if ( IO_Manager.pathExists(this.file) ||
                !IO_Manager.pathExists(directory) ){
            return Enum_IO.INVALID_PATH;
        }


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
                this.buffer.flush();

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
