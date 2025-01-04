package com.adl.et.telco.testautomation.global;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalVars {
    public static final String BASE_DIR = System.getProperty("user.dir");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
}
