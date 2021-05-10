package twopc.common;

import lombok.Getter;

@Getter
public enum Stage {
    INIT(0),
    VOTE_REQUEST(1),
    VOTE_COMMIT(2),
    VOTE_ABORT(3),
    GLOBAL_COMMIT(4),
    GLOBAL_ABORT(5),
    ABORT(6),
    COMMIT_SUCCESS(7);
    private int code;

    public int getCode() {
        return code;
    }

    Stage(int code) {
        this.code = code;
    }
}
