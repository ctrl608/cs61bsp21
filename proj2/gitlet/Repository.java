package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

//import gitlet.Commit.*;
import static gitlet.Main.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    //folders
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS = join(GITLET_DIR, "commits");
    public static final File BRANCHES = join(GITLET_DIR, "branches");
    public static final File BLOBS = join(GITLET_DIR, "blobs");
    //files
    public static final File HEADID_FILE = join(GITLET_DIR, "HEADid");
    public static final File STAGE = join(GITLET_DIR, "stage");


    public static class Stage implements Serializable {
        public TreeMap<String, String> additions;
        public TreeSet<String> removals;

        Stage() {
            additions = new TreeMap<>();
            removals = new TreeSet<>();
        }

        public String findAdds(String f) {
            return additions.get(f);
        }

        public void stageOne(String f, String hash) {
            additions.put(f, hash);
        }

        public void unstageOne() {
        }

        public void replace(String f, String hash) {
            additions.replace(f, hash);
        }

        public boolean removeAdd(String f) {
            if (!additions.containsKey(f)) {
                return false;
            }
            additions.remove(f);
            return true;
        }

        public boolean isEmpty() {
            return additions.isEmpty() && removals.isEmpty();
        }

    }


    /* TODO: fill in the rest of this class. */
    public static void init() {
        GITLET_DIR.mkdir();
        COMMITS.mkdir();
        BRANCHES.mkdir();
        BLOBS.mkdir();
        try {
            STAGE.createNewFile();
        } catch (IOException e) {
            ;
        }
        HEAD = Commit.initialCommit();
        HEAD.saveCommit();
        HEADid = HEAD.toHash();
        save();
    }

    public static void delRecursive(File f) {
        /**recursively delete the file or folder,very danger
         */
        if (!f.exists()) {
            return;
        }
        if (f.isFile()) {
            f.delete();
        } else {
            for (File child : f.listFiles()) {
                delRecursive(child);
            }
            f.delete();
        }
    }

    public static boolean add(String f) {
        boolean added = false;

        //check if the file exists
        File file = join(CWD, f);
        if (!file.exists()) {
            throw Utils.error("File does not exist.");
        }
        //get the sha1 id of the file
        byte[] blob = Utils.readContents(file);

        //copy the content into the corresponding blob
        String currentHash = Utils.sha1((Object) blob);

        File blobFile = join(BLOBS, currentHash);
        if (!blobFile.exists()) {
            Utils.writeContents(blobFile, (Object) blob);
        }
        //保证了blob存在,但是文件是否需要stage还要分析
        //check if the file is staged
        String stagingHash = stage.findAdds(f);
        if (stagingHash == null) {
            //文件需要stage
            //no counterpart in stage
            stage.stageOne(f, currentHash);
            stage.removals.remove(currentHash);
            added=true;
        } else if (!Objects.equals(stagingHash, currentHash)) {
            //相应文件存在,但是内容冲突,覆盖
            //conflict
            stage.replace(f, currentHash);
            //如果如果stage里面有这个文件的add,说明之前必然有上面的过程,不用再去掉这个文件的remove
            added= true;
        }
        //如果完全相同,那么上面都不是,不动
        //check if the file is previously tracked in the latest commit


        //排除兜兜转转又回到起点的情况
        if (HEAD.existTracked(f, currentHash)) {
            stage.removeAdd(f);
            added=false;
        }
        return added;
    }

    public static TreeMap<String, String> applyModify(TreeMap<String, String> raw) {
        for (String remove : stage.removals) {
            raw.remove(remove);
        }
        for (String key : stage.additions.keySet()) {
            raw.put(key, stage.additions.get(key));
        }
        stage = new Stage();
        return raw;
    }

    public static void commit(String msg) {
        if (stage.isEmpty()) {
            throw error("No changes added to the commit.");
        }
        //create new commit
        HEAD = Commit.newCommit(msg);
        //save it
        HEAD.saveCommit();
        save();
    }

    public static void save() {
        HEADid = HEAD.toHash();
        Utils.writeObject(STAGE, stage);
        Utils.writeObject(HEADID_FILE, HEADid);
    }

    public static boolean removeAdd(String f) {
        return stage.removeAdd(f);
    }

    public static String nameToHash(String name) {
        File file = Utils.join(Repository.CWD, name);
        byte[] blob = Utils.readContents(file);
        return Utils.sha1((Object) blob);
    }
    public static void rm(String f){
        boolean inAdd = removeAdd(f);
        if (!inAdd) {
            String hash =nameToHash(f);
            if (HEAD.existTracked(f,hash)) {
                //if tracked, take it into the removals
                stage.removals.add(hash);
            } else {
                //if untracked, error
                throw Utils.error("No reason to remove the file.");
            }
        }
        save();
    }
    public static void showHEAD(){
        load();
        HEAD.listTrackedFiles();
    }
}
