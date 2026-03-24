package com.iyo.ohhaeng.app.command;

public enum CommandType {
    // 조회
    MY_INFO,   // /내정보
    RANKING,   // /랭킹

    // 자원/성장
    HUNT,      // /사냥
    ENHANCE,   // /강화
    REROLL,    // /리롤

    // 전투
    DUEL,      // /대결 @상대
    RAID,      // /레이드

    // 설정
    RENAME,    // /이름 [닉네임]

    // 도움말
    SKILL_INFO, // /스킬

    // 파싱 실패
    UNKNOWN
}
