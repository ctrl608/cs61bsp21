package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import gitlet.Repository.*;

import static gitlet.Main.*;

import static gitlet.Utils.*;


/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
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


    private Commit(String msg, long timestampRaw, String parentId) {
        message = msg;
        this.timestampRaw = timestampRaw;
        this.timestamp = getCurrentTimestamp(timestampRaw);
        this.parentId = parentId;
        trackedFiles = generateTrackedList(parentId);
    }

    public static Commit initialCommit() {
        return new Commit("initial commit", 0, null);
    }

    public static Commit newCommit(String msg) {
        return new Commit(msg, getCurrentTimestampRaw(), HEADid);
    }

    public void printCommit() {
        System.out.println("===");
        System.out.println("commit " + this.toHash());
        //TODO: merged commit
        System.out.println("Date: " + timestamp);
        System.out.println(message);
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
        TreeMap<String, String> newTracked;
        if (parentId == null) {
            newTracked = new TreeMap<>();
        } else {
            newTracked = new TreeMap<>(HEAD.trackedFiles);
        }
        newTracked = Repository.applyModify(newTracked);
        return newTracked;
    }

    public boolean existTracked(String file, String hash) {
        return Objects.equals(trackedFiles.get(file), hash);
    }

    public static Commit idToCommit(String commitHash) {
        if (commitHash == null) {
            return null;
        }
        File commitFile = join(Repository.COMMITS, commitHash);
        return Utils.readObject(commitFile, Commit.class);
    }

    public void saveCommit() {
        Utils.writeObject(join(Repository.COMMITS, this.toHash()), this);
    }

    public String toHash() {
        return Utils.sha1(message+timestampRaw+parentId);
    }

    public Commit parent() {
        return idToCommit(parentId);
    }
    public void listTrackedFiles(){
        for(String file: trackedFiles.keySet()){
            System.out.println(file+" "+trackedFiles.get(file));
        }
    }
    private static void merge() {
    }

    /* TODO: fill in the rest of this class. */
}
