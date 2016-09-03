package brains.mock.firebasetest.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean validateEmail(String emailStr) {
        String regularExpression = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        Pattern pattern = Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(emailStr);

        return matcher.matches();
    }

    public static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && validateEmail(email);
    }

    public static String formatNumberString(int number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return String.valueOf(number);
        }
    }

    public static String refactorPhone(String phone) {
        char[] charArray = phone.toCharArray();
        ArrayList<Character> phoneRef = new ArrayList<>();
        for (char symbol : charArray) {
            if (symbol != '-' && symbol != ')' && symbol != '(' && symbol != ' ') {
                phoneRef.add(symbol);
            }
        }
        //return phone like --> +380934567891
        return buildPhone(phoneRef);
    }

    private static String buildPhone(ArrayList<Character> list) {
        StringBuilder builder = new StringBuilder(list.size());
        for (Character ch : list) {
            builder.append(ch);
        }
        return builder.toString();
    }
}
