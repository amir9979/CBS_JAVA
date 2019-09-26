package IO_Package;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class WriterTest {

    private Writer writer;
    private final String[] linesToWrite = IO_ManagerTest.linesToWrite;

    private final String directoryPath = IO_ManagerTest.testResources_path;
    private final String fileName = IO_ManagerTest.fileToWriteName;

    @Before
    public void before(){

        if (this.writer != null){
            this.writer.closeFile();
        }else {
            this.writer = new Writer();
        }


        // Check that file not exists
        IO_ManagerTest.deleteFileToWrite();


        /***       Test openFile with Valid values   ***/
        Enum_IO enum_io = writer.openFile(this.directoryPath, this.fileName);
        Assert.assertEquals(Enum_IO.OPENED,enum_io); // Opened successfully

    }

    @After
    public void after(){
        // Delete the file after each test
        Assert.assertTrue(deletedFile());
    }


    @Test
    public void openFile() {

        /***      Invalid values  ***/
        Enum_IO enum_io = this.writer.openFile(this.directoryPath, this.fileName);
        Assert.assertEquals(Enum_IO.CURRENT_FILE_STILL_OPEN,enum_io); // Trying to open an open file

        Writer badPathWriter = new Writer();
        String badFileDirectory = "fake folder";
        String badFileName = "not exists.txt";
        Enum_IO enum_io_notExists = badPathWriter.openFile(badFileDirectory, badFileName);
        Assert.assertEquals(Enum_IO.INVALID_PATH, enum_io_notExists); // Trying to open an invalid path

    }

    @Test
    public void writeText() {

        // Write line by line
        for (int i = 0; i < this.linesToWrite.length ; i++) {
            Assert.assertEquals(Enum_IO.WROTE_SUCCESSFULLY,writer.writeText(linesToWrite[i])); // Wrote successfully
        }
    }

    @Test
    public void closeFile() {

        /***       Valid values   ***/
        Enum_IO enum_io = this.writer.closeFile();
        Assert.assertEquals(Enum_IO.CLOSED, enum_io); // Closed successfully

        enum_io = this.writer.closeFile();
        Assert.assertEquals(Enum_IO.CLOSED, enum_io); // Trying to close a closed file


    }

    /* This method helps to remove the 'write_test.txt' */
    private boolean deletedFile(){

        //Close the file
        Enum_IO enum_io = this.writer.closeFile();
        if(! enum_io.equals(Enum_IO.CLOSED) ) {
            return false;
        }

        // Delete the file
        String filePath = IO_Manager.buildPath(new String[]{this.directoryPath, this.fileName});
        enum_io = IO_Manager.getInstance().deleteFile(new File(filePath));


        return enum_io.equals(Enum_IO.DELETED); // true if deleted successfully
    }
}