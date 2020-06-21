package com.wings.okpermission.javav2.request;

/**
 * @author AI
 */
public class RequestChain {

    private BaseTask headTask;

    private BaseTask tailTask;

    public void addTaskToChain(BaseTask task) {
        if (headTask == null) {
            headTask = task;
        }
        // add task to the tail
        if (tailTask != null) {
            tailTask.next = task;
        }
        tailTask = task;
    }

    public void runTask() {
        headTask.request();
    }

}
