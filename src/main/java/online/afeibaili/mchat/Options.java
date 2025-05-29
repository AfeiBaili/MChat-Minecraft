package online.afeibaili.mchat;

public enum Options {
    PRINT_ADVANCEMENT("取得进度消息", false),
    PRINT_LEFT_GAME("玩家离开消息", false),
    PRINT_JOINED_GAME("玩家进入消息", false),
    PRINT_DEATH("死亡消息", false),
    PRINT_OTHER("其他消息", false),
    PRINT_CHAT("聊天消息", true),
    ;
    String description;
    boolean value;

    Options() {

    }

    Options(String description, boolean value) {
        this.description = description;
        this.value = value;
    }

    @Override
    public String toString() {
        return '[' + "描述=" + description + ", 值=" + value + ']';
    }
}