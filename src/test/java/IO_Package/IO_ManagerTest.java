package IO_Package;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class IO_ManagerTest {
    // Todo - delete 'file to write' after test
    // imp - write missing tests

    IO_Manager io_manager = IO_Manager.getInstance();


    static final String testResources_path = IO_Manager.testResources_Directory;


    /***    ReaderTest   ***/

    static final String fileToReadName = "IO_Package\\test_reader_file.txt";
    static final String fileToReadPath = IO_Manager.buildPath(new String[]{testResources_path, fileToReadName});
    static final String[] linesToRead = {   "This file has 3 lines" ,
            "This is the second line",
            "This is the last line"};


    /***    WriterTest   ***/
    static final String fileToWriteName = "write_test.txt";
    static final String fileToWritePath = IO_Manager.buildPath(new String[]{testResources_path, fileToWriteName});

    static final String[] linesToWrite = {  "Try to write\n" ,
            "Wrote the second line\n",
            "Wrote the last line"};





    @Before
    public void before(){

    }

    @After
    public void after(){

    }

    public static void deleteFileToWrite(){
        boolean deleted = false;
        // Check that file not exists
        File file = new File(fileToWritePath);
        if ( IO_Manager.pathExists(file) ){
            deleted = file.delete();
        }
    }


    @Test
    public void getReader() {

        /***       Valid values   ***/
        Reader reader = io_manager.getReader(fileToReadPath);
        Assert.assertNotNull(reader);
        reader.closeFile();
        reader = io_manager.getReader(fileToReadPath);
        Assert.assertNotNull(reader);


        /***       Invalid values   ***/
        reader = io_manager.getReader(fileToReadPath);
        Assert.assertNull(reader);


    }

    @Test
    public void getWriter() {

        File file = new File(testResources_path,fileToWriteName);
        if ( IO_Manager.pathExists(file) ){
            file.delete();
        }

        /***       Valid values   ***/
        Writer writer = io_manager.getWriter(testResources_path,fileToWriteName);
        Assert.assertNotNull(writer);


        /***       Invalid values   ***/
        writer = io_manager.getWriter(testResources_path,fileToWriteName); // path still open
        Assert.assertNull(writer);

        deleteFileToWrite();

    }

    @Test
    public void pathExists() {
        // imp
    }

    @Test
    public void isDirectory() {
        // imp
    }

    @Test
    public void deleteFile() {
        // imp
    }

    @Test
    public void isOpen() {

        /***       Invalid values   ***/
        Assert.assertFalse(this.io_manager.isOpen("fake_file.txt"));


        /***       Valid values   ***/
        String openPath = "open_path.txt";
        this.io_manager.addOpenPath(openPath);
        Assert.assertTrue(this.io_manager.isOpen(openPath));
        this.io_manager.removeOpenPath(openPath);


    }

    @Test
    public void buildPath() {
        // BuildPath format: folder\fileName.txt
        String path = IO_Manager.buildPath(new String[]{"folder","file_name"});
        Assert.assertEquals("folder\\file_name", path);

    }
}