package IO_Package;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class WriterTest {

    private Writer writer;
    private final String directoryRelativePath = "resources";
    private final String[] linesToWrite = { "Try to write\n" ,
                                            "Wrote the second line\n",
                                            "Wrote the last line"};

    private final String directoryPath = IO_Manager.buildPath(new String[]{ IO_Manager.workingDirectory, this.directoryRelativePath});
    private final String fileName = "write test.txt";

    @Before
    public void before(){
        this.writer = new Writer();

        /***       Test openFile with Valid values   ***/
        Enum_IO enum_io = writer.openFile(this.directoryPath, this.fileName);
        Assert.assertEquals(Enum_IO.OPENED,enum_io);

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
        Assert.assertEquals(Enum_IO.ALREADY_OPENED,enum_io);

        Writer badPathWriter = new Writer();
        String badFileDirectory = "fake folder";
        String badFileName = "not exists.txt";
        Enum_IO enum_io_notExists = badPathWriter.openFile(badFileDirectory, badFileName);
        Assert.assertEquals(Enum_IO.INVALID_PATH, enum_io_notExists);

    }

    @Test
    public void writeText() {

        // Write line by line
        for (int i = 0; i < this.linesToWrite.length ; i++) {
            Assert.assertEquals(Enum_IO.WROTE_SUCCESSFULLY,writer.writeText(linesToWrite[i]));
        }

    }

    @Test
    public void closeFile() {

        /***       Valid values   ***/
        Enum_IO enum_io = this.writer.closeFile();
        Assert.assertEquals(Enum_IO.CLOSED, enum_io);

        enum_io = this.writer.closeFile();
        Assert.assertEquals(Enum_IO.CLOSED, enum_io);



    }

    private boolean deletedFile(){

        //Close the file
        if(! this.writer.closeFile().equals(Enum_IO.CLOSED) ) {
            return false;
        }

        // Delete the file
        String filePath = IO_Manager.buildPath(new String[]{this.directoryPath, this.fileName});
        Enum_IO enum_io = IO_Manager.deleteFile(new File(filePath));

        return enum_io.equals(Enum_IO.DELETED);
    }
}