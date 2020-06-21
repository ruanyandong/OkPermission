package com.wings.okpermission.javav2.request;

import java.util.List;

/**
 * @author AI
 */
public interface ChainTask {

    /**
     * Get the ExplainScope for showing RequestReasonDialog.
     * @return Instance of ExplainScope.
     */
    ExplainScope getExplainScope();

    /**
     * Get the ForwardScope for showing ForwardToSettingsDialog.
     * @return Instance of ForwardScope.
     */
    ForwardScope getForwardScope();

    /**
     * Do the request logic.
     */
    void request();

    /**
     * Request permissions again when user denied.
     * @param permissions
     *          Permissions to request again.
     */
    void requestAgain(List<String> permissions);

    /**
     * Finish this task and notify the request result.
     */
    void finish();
}