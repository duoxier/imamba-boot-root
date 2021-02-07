package com.imamba.boot.persist.hbase.rowkey;

public enum Termination {
    AUTO,
    MUST,
    SHOULD_NOT;

    private Termination() {
    }
}
