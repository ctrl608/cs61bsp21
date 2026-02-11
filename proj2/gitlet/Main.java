package gitlet;

//import java.io.IOException;

import java.util.Objects;

import static gitlet.Repository.*;

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
    public static String HEAD;
    //example:"master"
    public static Commit HEADCommit;
    public static Repository.Stage stage;


    public static void main(String[] args) {
        Utils.checkArgsAtLeast(args, 1,"Please enter a command.");
        String firstArg = args[0];
        if (!Repository.GITLET_DIR.exists() && !firstArg.equals("init")) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        //try to load from repos
        if (Repository.GITLET_DIR.exists()) {
            load();
        }
        try {

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
                    Repository.add(args[1]);

                    //save current situation
                    Repository.save();
                    break;
                case "commit":
                    Utils.checkArgs(args, 2, "Please enter a commit message.");
                    if (args[1].isEmpty()) {
                        throw Utils.error("Please enter a commit message.");
                    }
                    Repository.commit(args[1]);
                    break;
                case "rm":
                    Utils.checkArgs(args, 2, "Incorrect operands.");
                    String name = args[1];
                    Repository.rm(name);
                    break;
                case "log":
                    Utils.checkArgs(args, 1, "Incorrect operands.");
                    Commit curr = HEADCommit;
                    while (curr != null) {
                        curr.print();
                        curr = curr.parent();
                    }
                    break;
                case "global-log":
                    Utils.checkArgs(args, 1, "Incorrect operands.");
                    Repository.globalLog();
                    break;
                case "find":
                    Utils.checkArgs(args, 2, "Incorrect operands.");
                    Repository.find(args[1]);
                    break;
                case "status":
                    Utils.checkArgs(args, 1, "Incorrect operands.");
                    Repository.status();
                    break;
                case "checkout":

                    /**java gitlet.Main checkout -- [file name]
                     *java gitlet.Main checkout [commit id] -- [file name]
                     *java gitlet.Main checkout [branch name]
                     * */

                    if (args.length == 2) {
                        Repository.checkoutBranch(args[1]);
                    } else if (args.length == 3 && Objects.equals(args[1], "--")) {
                        Repository.checkoutOne(HEADCommit.toHash(), args[2]);
                    } else if (args.length == 4 && Objects.equals(args[2], "--")) {
                        Repository.checkoutOne(args[1], args[3]);
                    } else {
                        throw Utils.error("Incorrect operands.");
                    }
                    break;
                case "branch":
                    Utils.checkArgs(args, 2, "Incorrect operands.");
                    createNewBranch(args[1]);
                    break;
                case "rm-branch":
                    Utils.checkArgs(args, 2, "Incorrect operands.");
                    Repository.rmBranch(args[1]);
                    break;
                case "reset":
                    Utils.checkArgs(args, 2, "Incorrect operands.");
                    Repository.reset(args[1], true);
                    break;
                case "merge":
                    Utils.checkArgs(args,2,"Incorrect operands.");
                    Repository.merge(args[1]);
                    break;
                default:
                    throw Utils.error("No command with that name exists.");
            }

        } catch (GitletException e) {
            System.out.println(e.getMessage());
        }
        save();
    }

}
