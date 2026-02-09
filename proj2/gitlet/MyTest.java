package gitlet;

import org.junit.Test;


public class MyTest {

    @Test
    public void delRepo() {
        Repository.delRecursive(Repository.GITLET_DIR);
    }

    @Test
    public void testMainInit() {
        String[] command = {"init"};
        Main.main(command);
    }

    @Test
    public void testMainAdd() {
        String[] command = {"add", "a.txt"};
        Main.main(command);

    }

    @Test
    public void basic() {
        delRepo();
        testMainInit();
        testMainAdd();
        testMainCommit();
        testMainLog();
    }

    @Test
    public void testMainCommit() {
        String[] command = {"commit", "test"};
        Main.main(command);
    }

    @Test
    public void testMainLog() {
        String[] command = {"log"};
        Main.main(command);
    }

    @Test
    public void testMainRm() {
        String[] command = {"rm", "a.txt"};
        Main.main(command);
    }

    @Test
    public void rmAndCommit() {
        Repository.showHEAD();
        testMainRm();
        Main.main(new String[]{"commit", "remove"});
        testMainLog();
        Repository.showHEAD();
    }

}
