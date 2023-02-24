package com.sdu.kob.common;

public enum GameStatus {
    PLAYING("playing","正在对弈"),
    WAITING("waiting", "等待"),
    FINISHED("finished", "对局结束");

    private String name;
    private String value;

    GameStatus(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
