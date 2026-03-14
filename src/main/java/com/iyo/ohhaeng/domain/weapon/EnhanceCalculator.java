package com.iyo.ohhaeng.domain.weapon;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class EnhanceCalculator {

    /** [성공%, 유지%, 하락%] — 행 합계 = 100, 인덱스 = 현재 강화 레벨 */
    private static final int[][] ENHANCE_RATES = {
        {90, 10,  0},  // L0
        {85, 12,  3},  // L1
        {80, 15,  5},  // L2
        {75, 17,  8},  // L3
        {70, 18, 12},  // L4
        {65, 20, 15},  // L5
        {55, 22, 23},  // L6
        {45, 25, 30},  // L7
        {35, 28, 37},  // L8
        {25, 30, 45},  // L9
        {22, 30, 48},  // L10
        {20, 28, 52},  // L11
        {18, 27, 55},  // L12
        {15, 25, 60},  // L13
        {12, 23, 65},  // L14
        {10, 20, 70},  // L15
        { 9, 18, 73},  // L16
        { 8, 17, 75},  // L17
        { 7, 15, 78},  // L18
        { 6, 14, 80},  // L19
        { 5, 13, 82},  // L20
        { 5, 12, 83},  // L21
        { 4, 11, 85},  // L22
        { 4, 10, 86},  // L23
        { 3, 10, 87},  // L24
        { 3,  7, 90},  // L25 ← 극악 구간
        { 2,  5, 93},  // L26
        { 1,  4, 95},  // L27
        { 1,  2, 97},  // L28
        { 1,  1, 98},  // L29
    };

    public EnhanceOutcome roll(int enhanceLevel) {
        int[] r = ENHANCE_RATES[enhanceLevel];
        int roll = ThreadLocalRandom.current().nextInt(100);
        if (roll < r[0])        return EnhanceOutcome.SUCCESS;
        if (roll < r[0] + r[1]) return EnhanceOutcome.HOLD;
        return EnhanceOutcome.DOWNGRADE;
    }
}
