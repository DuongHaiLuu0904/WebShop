package com.ptit.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {
    
    private static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");
    
    /**
     * Xóa dấu tiếng Việt và chuyển về chữ thường
     */
    public static String removeAccents(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        // Normalize text và loại bỏ dấu
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        String withoutDiacritics = DIACRITICS_AND_FRIENDS.matcher(normalized).replaceAll("");
        
        // Xử lý đặc biệt cho chữ đ/Đ
        withoutDiacritics = withoutDiacritics.replace("đ", "d").replace("Đ", "D");
        
        return withoutDiacritics.toLowerCase();
    }
    
    /**
     * So sánh 2 chuỗi không phân biệt dấu
     */
    public static boolean containsIgnoreAccents(String text, String keyword) {
        if (text == null || keyword == null) {
            return false;
        }
        return removeAccents(text).contains(removeAccents(keyword));
    }
}
