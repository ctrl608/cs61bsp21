package gitlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
//import java.io.IOException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

//import gitlet.Commit.*;
import static gitlet.Main.*;

import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
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
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File STAGE = join(GITLET_DIR, "stage");


    public static class Stage implements Serializable {
        private TreeMap<String, String> additions;
        private TreeSet<String> removals;

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

        public boolean containAdd(String f) {
            return additions.containsKey(f);
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


    public static void init() {
        GITLET_DIR.mkdir();
        COMMITS.mkdir();
        BRANCHES.mkdir();
        BLOBS.mkdir();
        stage = new Stage();

        HEADCommit = Commit.initialCommit();
        Utils.writeObject(join(COMMITS, HEADCommit.toHash()), HEADCommit);
        createNewBranch("master");
        HEAD = "master";
        save();
    }

    public static void save() {
        Utils.writeObject(STAGE, stage);
        Utils.writeContents(HEAD_FILE, HEAD);
//        Utils.writeObject(join(COMMITS,HEADCommit.toHash()),HEADCommit);

    }


    public static void load() {
        //恢复上次关闭状态
        HEAD = Utils.readContentsAsString(HEAD_FILE);
        HEADCommit = branchLatestCommit(HEAD);
        stage = Utils.readObject(Repository.STAGE, Repository.Stage.class);
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
        if (stagingHash == null && !stage.containAdd(f)) {
            //文件需要stage
            //no counterpart in stage
            stage.stageOne(f, currentHash);
            stage.removals.remove(f);
            added = true;
        } else if (!Objects.equals(stagingHash, currentHash)) {
            //相应文件存在,但是内容冲突,覆盖
            //conflict
            stage.replace(f, currentHash);
            //如果如果stage里面有这个文件的add,说明之前必然有上面的过程,不用再去掉这个文件的remove
            added = true;
        }
        //如果完全相同,那么上面都不是,不动
        //check if the file is previously tracked in the latest commit


        //排除兜兜转转又回到起点的情况
        if (HEADCommit.contain(f) && currentHash.equals(HEADCommit.getTracked(f))) {

            stage.removeAdd(f);
            added = false;
        }
        save();
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
        HEADCommit = Commit.newCommit(msg);
        //save it
        writeContents(join(BRANCHES, HEAD), HEADCommit.toHash());
        HEADCommit.saveCommit();
        save();
    }

    public static void mergeCommit(String mergeBranch) {
        if (stage.isEmpty()) {
            throw error("No changes added to the commit.");
        }
        //create new commit
        HEADCommit = Commit.newMergeCommit(mergeBranch);
        //save it
        writeContents(join(BRANCHES, HEAD), HEADCommit.toHash());
        HEADCommit.saveCommit();
        save();
    }

    public static boolean removeAdd(String f) {
        return stage.removeAdd(f);
    }

    public static String nameToHash(String name) {
        File file = Utils.join(Repository.CWD, name);
        byte[] blob = Utils.readContents(file);
        return Utils.sha1((Object) blob);
    }

    public static void rm(String name) {
        boolean inAdd = removeAdd(name);
        //try to remove corresponding addition
        if (!inAdd) {
            if (HEADCommit.contain(name)) {
                //if tracked, take it into the removals
                File current = join(CWD, name);
                current.delete();
                stage.removals.add(name);
            } else {
                //if untracked, error
                throw Utils.error("No reason to remove the file.");
            }
        }
        save();
    }


    /// create a new branch pointer to the current commit
    public static void createNewBranch(String name) {
        File branch = join(BRANCHES, name);
        if (branch.exists()) {
            throw error("A branch with that name already exists.");
        }
        writeContents(branch, HEADCommit.toHash());
    }

    /// find the latest commit of a given branch (what the branch points at)
    public static Commit branchLatestCommit(String name) {
        File branch = Utils.join(Repository.BRANCHES, name);
        if (!branch.exists()) {
            throw error("No such branch exists.");
        }
        String commitHash = Utils.readContentsAsString(branch);
        return Commit.idToCommit(commitHash);
    }

    /// globally print the logs
    public static void globalLog() {
        for (String hash : Objects.requireNonNull(plainFilenamesIn(COMMITS))) {
            File file = join(COMMITS, hash);
            Commit commit = Utils.readObject(file, Commit.class);
            commit.print();
        }
    }

    /**
     * find the commits that contains the message
     *
     */
    public static void find(String finding) {
        boolean found = false;
        for (String hash : Objects.requireNonNull(plainFilenamesIn(COMMITS))) {
            File file = join(COMMITS, hash);
            Commit commit = Utils.readObject(file, Commit.class);
            String message = commit.getMessage();
            if (message.equals(finding)) {
                System.out.println(commit.toHash());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        for (String branchName : Objects.requireNonNull(plainFilenamesIn(BRANCHES))) {
            if (branchName.equals(HEAD)) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String file : stage.additions.keySet()) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String file : stage.removals) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");


    }

    public static void rmBranch(String branchName) {

        if (!Objects.requireNonNull(plainFilenamesIn(BRANCHES)).contains(branchName)) {
            throw error("A branch with that name does not exist.");
        }
        if (Objects.equals(branchName, HEAD)) {
            throw error("Cannot remove the current branch.");
        }
        File branchFile = join(BRANCHES, branchName);
        branchFile.delete();
    }

    private static void unsafeCheckoutOne(Commit commit, String fileName) {
        String hash = commit.getTracked(fileName);
        byte[] blob = readContents(join(BLOBS, hash));
        File file = join(CWD, fileName);
        writeContents(file, (Object) blob);
    }

    public static void checkoutOne(String commitId, String fileName) {
        commitId = findHash(commitId);
        Commit commit = Commit.idToCommit(commitId);
        if (commit == null) {
            throw error("No commit with that id exists.");
        }
        if (!commit.contain(fileName)) {
            throw error("File does not exist in that commit.");
        }
        unsafeCheckoutOne(commit, fileName);
    }

    public static void checkUntracked(Commit aimCommit) {
        for (String prevFile : aimCommit.trackedFileNameList()) {
            String prevHash = aimCommit.getTracked(prevFile);
            String currHash = Utils.fileToHash(prevFile);
            //过去追踪现在不追,现在有文件并且和过去不同,那么撞了
            if (!HEADCommit.contain(prevFile) && currHash != null && !currHash.equals(prevHash)) {
                throw error("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
            }
        }
    }

    public static void checkoutBranch(String branch) {
        if (!Utils.plainFilenamesIn(BRANCHES).contains(branch)) {
            throw error("No such branch exists.");
        }
        if (Objects.equals(branch, HEAD)) {
            throw error("No need to checkout the current branch.");
        }

        reset(branchLatestCommit(branch).toHash(), false);
        stage = new Stage();
        HEAD = branch;
        HEADCommit = branchLatestCommit(branch);
        save();
    }

    private static String findHash(String abbr) {
        int count = 0;
        String real = null;
        for (String hash : Objects.requireNonNull(plainFilenamesIn(COMMITS))) {
            if (Objects.equals(hash, abbr)) {
                return abbr;
            }
            if (hash.startsWith(abbr)) {
                count += 1;
                real = hash;
            }

        }
        if (count == 1) {
            return real;
        } else if (count == 0) {
            return null;
        } else {
            throw error("hash cracked! " + abbr + " is insufficient");
        }

    }

    public static void reset(String hash, boolean moveBranch) {
        hash = findHash(hash);
        Commit commit = Commit.idToCommit(hash);
        if (commit == null) {
            throw error("No commit with that id exists.");
        }

        checkUntracked(commit);
        ///wont overwrite,安全


        /// 删除当前追踪的所有文件

        for (String fileName : HEADCommit.trackedFileNameList()) {
            File file = join(CWD, fileName);
            file.delete();
        }
        /// 写入之前追踪的所有文件
        for (String prevName : commit.trackedFileNameList()) {
            String prevHash = commit.getTracked(prevName);
            File file = join(CWD, prevName);
            File blobFile = join(BLOBS, prevHash);

            //copy from BLOBS
            byte[] blob = readContents(blobFile);
            writeContents(file, (Object) blob);
        }

        /// clear stage
        stage = new Stage();


        File currentBranchFile = join(BRANCHES, HEAD);
        if (moveBranch) {
            Utils.writeContents(currentBranchFile, hash);
        }
        HEADCommit = commit;
        save();


    }

    private static boolean equalTrackedContent(Commit a, Commit b) {
        if (a.trackedFileNameList().size() != b.trackedFileNameList().size()) {
            return false;
        }
        boolean isEqual = true;
        for (String fileName : a.trackedFileNameList()) {
            if (!a.getTracked(fileName).equals(b.getTracked(fileName))) {
                isEqual = false;
            }
        }
        return isEqual;
    }

    private static Commit splitCommit(String a, String b) {
        //对a做BFS(其他方法也行),找到所有祖先
        Set<String> avisited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(a);
        avisited.add(a);
        while (!queue.isEmpty()) {
            Commit currCommit = Commit.idToCommit(queue.poll());
            Commit parent = currCommit.parent();
            Commit anotherParent = currCommit.anotherParent();
            String parentId = parent == null ? null : parent.toHash();
            String anotherParentId = anotherParent == null ? null : anotherParent.toHash();
            if (parentId != null && !avisited.contains(parentId)) {
                avisited.add(parentId);
                queue.add(parentId);
            }
            if (anotherParentId != null && !avisited.contains(anotherParentId)) {
                avisited.add(anotherParentId);
                queue.add(anotherParentId);
            }
        }

        ///对B做BFS,找到最近的A的祖先
        queue = new LinkedList<>();
        Set<String> bvisited = new HashSet<>();
        queue.add(b);
        bvisited.add(b);
        while (!queue.isEmpty()) {
            Commit currCommit = Commit.idToCommit(queue.poll());
            if (avisited.contains(currCommit.toHash())) {
                return currCommit;
            }
            Commit parent = currCommit.parent();
            Commit anotherParent = currCommit.anotherParent();
            String parentId = parent == null ? null : parent.toHash();
            String anotherParentId = anotherParent == null ? null : anotherParent.toHash();
            if (parentId != null && !bvisited.contains(parentId)) {
                bvisited.add(parentId);
                queue.add(parentId);
            }
            if (anotherParentId != null && !bvisited.contains(anotherParentId)) {
                bvisited.add(anotherParentId);
                queue.add(anotherParentId);
            }
        }
        throw error("debug:cannot find splitCommit!");

    }

    private static String mergeConflict(String fileName,
                                        String currentBlobHash, String givenBlobHash) {

        byte[] current = (currentBlobHash == null)
                ? new byte[0] : readContents(join(BLOBS, currentBlobHash));
        byte[] given = (givenBlobHash == null)
                ? new byte[0] : readContents(join(BLOBS, givenBlobHash));
        byte[] left = "<<<<<<< HEAD\n".getBytes(StandardCharsets.UTF_8);
        byte[] middle = "=======\n".getBytes(StandardCharsets.UTF_8);
        byte[] right = ">>>>>>>\n".getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String newHash;
        try {
            out.write(left);
            out.write(current);
            out.write(middle);
            out.write(given);
            out.write(right);
            byte[] result = out.toByteArray();
            newHash = Utils.sha1((Object) result);
            writeContents(join(BLOBS, newHash), result);
            writeContents(join(CWD, fileName), result);
            stage.stageOne(fileName, newHash);
            //等价于直接stage.stageOne(filename,newHash);
        } catch (IOException e) {
            throw error("debug: error: in merging confilict" + e.getMessage());
        }
        save();
        return newHash;
    }

    public static void mergeBasicCheck(String givenBranch) {
        if (!stage.isEmpty()) {
            throw error("You have uncommitted changes.");
        }
        if (!plainFilenamesIn(BRANCHES).contains(givenBranch)) {
            throw error("A branch with that name does not exist.");
        }
        if (givenBranch.equals(HEAD)) {
            throw error("Cannot merge a branch with itself.");
        }
    }


    public static void merge(String givenBranch) {
        mergeBasicCheck(givenBranch);

        Commit givenCommit = branchLatestCommit(givenBranch);

        checkUntracked(givenCommit);


        /// 边界情况
        Commit splitCommit = splitCommit(HEADCommit.toHash(), givenCommit.toHash());
        if (splitCommit.equals(givenCommit)) {
            throw error("Given branch is an ancestor of the current branch.");
        }
        if (splitCommit.equals(HEADCommit)) {
            checkoutBranch(givenBranch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        /** 主体*/
        Set<String> splitList = new HashSet<>(splitCommit.trackedFileNameList());
        Set<String> headlist = new HashSet<>(HEADCommit.trackedFileNameList());
        Set<String> givenlist = new HashSet<>(givenCommit.trackedFileNameList());
        // 并集
        Set<String> allTracked = new HashSet<>(splitList);
        allTracked.addAll(headlist);
        allTracked.addAll(givenlist);
        boolean conflicted = false;
        for (String fileName : allTracked) {
            String splitHash = splitCommit.getTracked(fileName);
            String headHash = HEADCommit.getTracked(fileName);
            String givenHash = givenCommit.getTracked(fileName);
            boolean headChanged = !Objects.equals(splitHash, headHash);
            boolean givenChanged = !Objects.equals(splitHash, givenHash);
            /// 1
            if (splitHash != null && givenChanged && !headChanged) {
                if (givenHash == null) {
                    rm(fileName);
                } else {
                    if (!writeFromBlob(fileName, givenHash)) {
                        throw error("debug: blob missing " + givenHash);
                    }
                    add(fileName);
                }
                continue;
            }
            /// 2
            if (!givenChanged && headChanged) {
                //stay as they are
                continue;
            }
            /// 3
            if (Objects.equals(givenHash, headHash)) {
                continue;
            }
            /// 467 Redundant
            /// 5: not in split, not in head, but in given -> checkout and stage
            if (splitHash == null && headHash == null && givenHash != null) {
                if (!writeFromBlob(fileName, givenHash)) {
                    throw error("debug: blob missing " + givenHash);
                }
                add(fileName);
                continue;
            }
            /// 8 always true
            if (givenChanged && headChanged && !Objects.equals(givenHash, headHash)) {
                mergeConflict(fileName, headHash, givenHash);
                conflicted = true;
            }
        }
        mergeCommit(givenBranch);
        if (conflicted) {
            System.out.println("Encountered a merge conflict.");
        }
        save();
    }
}
