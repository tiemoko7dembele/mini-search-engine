package com.dassaultsystemes.searchengine;

public enum Mode {
    CONSOLE, BATCH;

    public static boolean isValid(String modeToValid)
    {
        for(Mode mode:values())
            if (mode.name().equals(modeToValid))
                return true;
        return false;
    }
}
