package IO_Package;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class IO_ManagerTest {
    // imp - write missing tests
    // Blocking - deleteFile Method: toDelete file exists but won't delete ( sometimes does )

    IO_Manager io_manager = IO_Manager.getInstance();


    static final String test_resources_path = IO_Manager.buildPath(new String[]{ IO_Manager.workingDirectory,"test_resources"});


    /***    ReaderTest   ***/

    static final String fileToReadName = "test_reader_file.txt";
    static final String fileToReadPath = IO_Manager.buildPath(new String[]{ test_resources_path, fileToReadName});
    static final String[] linesToRead = {  "This file has 3 lines" ,
                                            "This is the second line",
                                            "This is the last line"};


    /***    WriterTest   ***/
    static final String fileToWriteName = "write_test.txt";
    static final String fileToWritePath = IO_Manager.buildPath(new String[]{ test_resources_path, fileToWriteName});

    static final String[] linesToWrite = {  "Try to write\n" ,
                                            "Wrote the second line\n",
                                            "Wrote the last line"};





    @Before
    public void before(){
        deleteFileToWrite();
    }

    @After
    public void after(){
        deleteFileToWrite();
    }

    static void deleteFileToWrite(){
        // Check that file not exists
        File file = new File(fileToWriteName);
        if ( IO_Manager.pathExists(file) ){
            file.delete();
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

        /***       Valid values   ***/
        Writer writer = io_manager.getWriter(test_resources_path,fileToWriteName);
        Assert.assertNotNull(writer);


        /***       Invalid values   ***/
        writer = io_manager.getWriter(test_resources_path,fileToWriteName); // path still open
        Assert.assertNull(writer);

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