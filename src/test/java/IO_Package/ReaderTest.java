package IO_Package;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReaderTest {

    private Reader reader;
    private final String fileRelativePath = "resources/test reader file.txt";
    private final String[] linesToRead = {  "This file has 3 linesToRead" ,
                                            "This is the second line",
                                            "This is the last line"};
    private final String filePath = IO_Manager.buildPath(new String[]{ IO_Manager.workingDirectory, this.fileRelativePath});


    @Before
    public void before(){

        this.reader = new Reader();

        /***       Test openFile with Valid values   ***/
        Enum_IO enum_io = reader.openFile(this.filePath);
        Assert.assertEquals(Enum_IO.OPENED,enum_io);

    }

    @Test
    public void getNextLine() {

        /***       Valid values   ***/
        for (int i = 0; i < linesToRead.length ; i++) {
            Assert.assertEquals(linesToRead[i],this.reader.getNextLine());
        }


    }

    @Test
    public void openFile() {


        /***      Invalid values  ***/
        Enum_IO enum_io = this.reader.openFile(this.filePath);
        Assert.assertEquals(Enum_IO.ALREADY_OPENED,enum_io);


        Reader badPathReader = new Reader();
        String badFilePath = "not exists.txt";
        Enum_IO enum_io_notExists = badPathReader.openFile(badFilePath);
        Assert.assertEquals(Enum_IO.INVALID_PATH, enum_io_notExists);


    }
    @Test
    public void closeFile() {


        /***       Valid values   ***/
        Enum_IO enum_io = this.reader.closeFile();
        Assert.assertEquals(Enum_IO.CLOSED, enum_io);

        enum_io = this.reader.closeFile();
        Assert.assertEquals(Enum_IO.CLOSED, enum_io);


    }
}