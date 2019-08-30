package IO_Package;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReaderTest {

    private Reader reader;
    private final String[] linesToRead = IO_ManagerTest.linesToRead;
    private final String filePath = IO_ManagerTest.fileToReadPath;


    @Before
    public void before(){

        this.reader = new Reader();

        /***       Test openFile with Valid values   ***/
        Enum_IO enum_io = reader.openFile(this.filePath);
        Assert.assertEquals(Enum_IO.OPENED,enum_io);

    }


    @After
    public void after(){
        IO_Manager.getInstance().removeOpenPath(this.filePath);
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