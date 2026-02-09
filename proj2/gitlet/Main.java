package gitlet;

//import java.io.IOException;

import java.io.File;
import java.util.Objects;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */


public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */

    //objects
    public static Commit HEAD;
    public static String HEADid;
    public static Repository.Stage stage;
    public static String currentBranch;


    public static void main(String[] args) {
        // TODO: what if args is empty?
        Utils.checkArgsAtLeast(args, 1);
        String firstArg = args[0];
        //try to load from repos
        if (Repository.GITLET_DIR.exists()) {
            load();
        }
        switch (firstArg) {
            case "init":
                Utils.checkArgs(args, 1, "Incorrect operands.");
                if (Repository.GITLET_DIR.exists()) {
                    throw Utils.error("A Gitlet version-control system already exists in the current directory.");
                }
                Repository.init();
                break;
            case "add":
                Utils.checkArgs(args, 2, "Incorrect operands.");
                for (int i = 1; i < args.length; ++i) {
                    Repository.add(args[i]);
                }

                //save current situation
                Repository.save();
                break;
            // TODO: FILL THE REST IN
            case "commit":
                try {
                    Utils.checkArgs(args, 2, "Incorrect operands.");
                    Repository.commit(args[1]);
                } catch (GitletException e) {
                    System.out.println("Please enter a commit message.");
                    String msg = new java.util.Scanner(System.in).nextLine();
                    Repository.commit(msg);
                }
                break;
            case "rm":
                Utils.checkArgs(args, 2, "Incorrect operands.");
                String f = args[1];
                Repository.rm(f);
                break;
            case "log":
                Utils.checkArgs(args, 1, "");
                Commit curr = HEAD;
                while (curr != null) {
                    curr.printCommit();
                    curr = curr.parent();
                }
                break;
            case "global-log":
                break;
            case "find":
                break;
            case "status":
                System.out.println("=== Branches ===");
                for(File branch: Objects.requireNonNull(Repository.BRANCHES.listFiles())) {
                    String name=branch.getName();
                    if(name==currentBranch){
                        System.out.printf("*");
                    }
                    System.out.println(name);
                }

                System.out.println("=== Staged Files ===");
                for(String file : stage.additions.keySet()){
                    System.out.println(file);
                }

                System.out.println("=== Removed Files ===");
                for(String file: stage.removals){
                    System.out.println(file);
                }

//                System.out.println("=== Modifications Not Staged For Commit ===");
//                System.out.println("=== Untracked Files ===");

                break;
            case "checkout":
                break;
            case "branch":
                break;
            case "rm-branch":
                break;
            case "reset":
                break;
            case "merge":
                break;
            default:
                throw Utils.error("No command with that name exists.");
        }
    }

    public static void load() {
        //恢复上次关闭状态
        HEADid = Utils.readObject(Repository.HEADID_FILE, String.class);
        HEAD = Commit.idToCommit(HEADid);
        stage = Utils.readObject(Repository.STAGE, Repository.Stage.class);
    }
}
