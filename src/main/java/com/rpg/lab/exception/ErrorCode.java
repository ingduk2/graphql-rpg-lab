package com.rpg.lab.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND("요청한 대상을 찾을 수 없습니다."),
    BAD_REQUEST("잘못된 요청입니다."),
    INTERNAL_ERROR("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    PREREQUISITE_NOT_COMPLETED("선행 퀘스트를 먼저 완료해야 합니다."),
    INSUFFICIENT_GOLD("골드가 부족합니다."),
    ITEM_ALREADY_OWNED("이미 보유한 장비입니다."),
    HP_ALREADY_FULL("이미 체력이 가득 찼습니다."),
    INVALID_ITEM_OPERATION("이 아이템에는 사용할 수 없는 동작입니다."),
    MAX_ENHANCE_LEVEL("이미 최대 강화 단계입니다."),
    ;

    private final String userMessage;

    public Map<String, Object> toExtensions() {
        return Map.of(
                "errorCode", this.name(),
                "userMessage", userMessage
        );
    }
}
