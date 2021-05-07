package twopc.coordinator.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Stage {
    INIT(0),
    VOTE_REQUEST(1),
    VOTE_COMMIT(2),
    VOTE_ABORT(3),
    GLOBAL_COMMIT(4),
    GLOBAL_ABORT(5),
    DECISION_REQUEST(6);
    private int code;
}
