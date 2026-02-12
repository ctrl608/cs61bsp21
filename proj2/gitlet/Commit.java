package gitlet;


import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import gitlet.Repository.*;

import static gitlet.Main.*;

import static gitlet.Utils.*;


/**
 * Represents a gitlet commit object.
 * <p>
 * does at a high level.
 *
 *
 *
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private static final String INITIAL_TIMESTAMP =
            "Thu Jan 1 00:00:00 1970 +0000";

    private final String message;
    private String timestamp;
    private final long timestampRaw;
    private String parentId;
    private String anotherParentId;
    //private  final String hash;
    private final TreeMap<String, String> trackedFiles;
    //文件路径->hash


    private Commit(String msg, long timestampRaw, String parentId, String anotherParentId) {
        message = msg;
        this.timestampRaw = timestampRaw;
        this.timestamp = getCurrentTimestamp(timestampRaw);
        this.parentId = parentId;
        this.anotherParentId = anotherParentId;
        trackedFiles = generateTrackedList(parentId);
    }

    public static Commit initialCommit() {
        return new Commit("initial commit", 0, null, null);
    }

    public static Commit newCommit(String msg) {
        return new Commit(msg, getCurrentTimestampRaw(), HEADCommit.toHash(), null);
    }

    public void print() {
        System.out.println("===");
        System.out.println("commit " + this.toHash());
        if (anotherParent() != null) {
            System.out.printf(
                    "Merge: %s %s\n", parentId.substring(0, 7), anotherParentId.substring(0, 7));
        }
        System.out.println("Date: " + timestamp);
        System.out.println(message);
        System.out.println();
    }

    public String getMessage() {
        return message;
    }

    private static long getCurrentTimestampRaw() {
        return System.currentTimeMillis();
    }

    private static String getCurrentTimestamp(long ms) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(ms));
    }

    private static TreeMap<String, String> generateTrackedList(String parentId) {
        Commit fromCommit = idToCommit(parentId);
        TreeMap<String, String> newTracked;
        if (parentId == null) {
            newTracked = new TreeMap<>();
        } else {
            newTracked = new TreeMap<>(fromCommit.trackedFiles);
        }
        return Repository.applyModify(newTracked);
    }

    public boolean contain(String file) {
        return trackedFiles.containsKey(file);
    }

    public String getTracked(String file) {
        return trackedFiles.get(file);
    }

    public static Commit idToCommit(String commitHash) {
        if (commitHash == null) {
            return null;
        }
        File commitFile = join(Repository.COMMITS, commitHash);
        if (!commitFile.exists()) {
            return null;
        }
        return Utils.readObject(commitFile, Commit.class);
    }

    public void saveCommit() {
        Utils.writeObject(join(Repository.COMMITS, this.toHash()), this);
    }

    public String toHash() {
        return Utils.sha1(message + timestampRaw + parentId, serialize(trackedFiles));
    }

    public Commit parent() {
        return idToCommit(parentId);
    }

    public Commit anotherParent() {
        return idToCommit(anotherParentId);
    }

    public void printTrackedFiles() {
        for (String file : trackedFiles.keySet()) {
            System.out.println(file + " " + trackedFiles.get(file));
        }
    }

    public Set<String> trackedFileNameList() {
        return this.trackedFiles.keySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Commit)) {
            return false;
        }
        Commit other = (Commit) o;
        return this.toHash().equals(other.toHash());
    }

    @Override
    public int hashCode() {
        return this.toHash().hashCode();
    }

    public static Commit newMergeCommit(String branch) {
        String msg = "Merged " + branch + " into " + HEAD + ".";
        String anotherParentId = Repository.branchLatestCommit(branch).toHash();
        return new Commit(msg, getCurrentTimestampRaw(), HEADCommit.toHash(), anotherParentId);
    }

}
