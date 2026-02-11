package gitlet;

import net.sf.saxon.expr.Component;
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
    public void testMainCommit() {
        String[] command = {"commit", "removed"};
        Main.main(command);
    }

    @Test
    public void testMainLog() {
        String[] command = {"log"};
        Main.main(command);
    }

    @Test
    public void testMainGlobalLog() {
        String[] command = {"global-log"};
        Main.main(command);
    }

    @Test
    public void testMainRm() {
        String[] command = {"rm", "a.txt"};
        Main.main(command);
    }

    @Test
    public void testMainStatus() {
        String[] command = {"status"};
        Main.main(command);
    }

    @Test
    public void basic() {
        delRepo();
        Main.main(new String[]{"init"});
        Main.main(new String[]{"add", "a.txt"});
        Main.main(new String[]{"commit", "add a"});
        Main.main(new String[]{"rm", "a.txt"});
        Main.main(new String[]{"commit", "rm a"});
    }

    @Test
    public void anything() {
       Repository.load();
        showLink(Main.HEADCommit);
    }
    public static void show(Commit commit) {
        System.out.println("commit:"+commit.toHash());
        System.out.println(commit.parent()==null?null: "parentId:"+commit.parent().toHash());
        System.out.println("message:"+commit.getMessage());
        System.out.println("======");
        commit.printTrackedFiles();
        System.out.println("======");
        System.out.println();
    }
    public static void showLink(Commit commit){
        while(commit!=null){
            show(commit);
            commit=commit.parent();
        }
    }
    public static void showLink(String branchName){
        showLink(Repository.branchLatestCommit(branchName));
    }
    @Test
    public void run(){

        delRepo();
        Main.main(new String[]{"init"});
        Main.main(new String[]{"add", "a.txt"});
        Main.main(new String[]{"commit", "added a"});
        Main.main(new String[]{"branch","addingB"});

        Main.main(new String[]{"rm", "a.txt"});
        Main.main(new String[]{"commit", "rmed a"});

    }

}
