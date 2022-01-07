package com.servir.frostmapper2.utils;


/**
 * This is a useful callback mechanism so we can abstract our AsyncTasks out into separate, re-usable
 * and testable classes yet still retain a hook back into the calling activity. Basically, it'll make classes
 * cleaner and easier to unit test.
 *
 * //@param <String>
 */

public interface AsyncTaskCompleteListener{

    /**
     * Invoked when the AsyncTask has completed its execution.
     * @param result The resulting object from the AsyncTask.
     */

    public void AsyncTaskCompleteListener(String result, String sender, String TableName, String FieldName);

}


