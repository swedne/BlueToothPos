package com.swed.pos.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TextUtil {

    public static String dismissCardNumber(String paramString) {
//        String localObject;
//        if (isEmpty(paramString)) {
//            localObject = "";
//        }
        int j;
//        do {
//            return localObject;
        j = paramString.length() - 4;
//            localObject = paramString;
//        } while (j <= 6);
        StringBuffer localObject = new StringBuffer(paramString.length());
        int i = 0;
        if (i < paramString.length()) {
            if ((i < 6) || (i >= j)) {
                localObject.append(paramString.charAt(i));
            }
            for (; ; ) {
                i += 1;
                localObject.append("*");
                break;
            }
        }
        return localObject.toString();
    }

    public static String getNotEmptyStr(String paramString) {
        String str = paramString;
        if (paramString == null) {
            str = "";
        }
        return str;
    }

    public static boolean isEmpty(String paramString) {
        return (paramString == null) || (paramString.length() == 0) || (paramString.equalsIgnoreCase("null")) || (paramString.isEmpty()) || (paramString.equals(""));
    }

    public static boolean isUrl(String paramString) {
        return Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$").matcher(paramString).matches();
    }

    public static String keepTwoDecimal(double paramDouble) {
        return new DecimalFormat("######0.00").format(paramDouble);
    }

    public static String listToString(List<String> paramList, String paramString) {
        StringBuffer localStringBuffer = new StringBuffer();
        int i = 0;
        while (i < paramList.size()) {
            localStringBuffer.append((String) paramList.get(i));
            if (i != paramList.size() - 1) {
                localStringBuffer.append(paramString);
            }
            i += 1;
        }
        return localStringBuffer.toString();
    }

    public static void out(String paramString) {
    }

    public static void setDeleteAction(final EditText paramEditText, View paramView) {
        paramEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean) {
                if (paramAnonymousBoolean) {
                    paramEditText.setVisibility(View.VISIBLE);
                    paramEditText.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View paramAnonymous2View) {
                            paramEditText.setText("");
                        }
                    });
                    return;
                }
                paramEditText.setVisibility(View.INVISIBLE);
            }
        });
    }

    public static void setPricePoint(final EditText paramEditText) {
        paramEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable paramAnonymousEditable) {
            }

            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
            }

            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
                CharSequence localCharSequence = paramAnonymousCharSequence;
                if (paramAnonymousCharSequence.toString().contains(".")) {
                    localCharSequence = paramAnonymousCharSequence;
                    if (paramAnonymousCharSequence.length() - 1 - paramAnonymousCharSequence.toString().indexOf(".") > 2) {
                        localCharSequence = paramAnonymousCharSequence.toString().subSequence(0, paramAnonymousCharSequence.toString().indexOf(".") + 3);
                        paramEditText.setText(localCharSequence);
                        paramEditText.setSelection(localCharSequence.length());
                    }
                }
                paramAnonymousCharSequence = localCharSequence;
                if (localCharSequence.toString().trim().substring(0).equals(".")) {
                    paramAnonymousCharSequence = "0" + localCharSequence;
                    paramEditText.setText(paramAnonymousCharSequence);
                    paramEditText.setSelection(2);
                }
                if ((paramAnonymousCharSequence.toString().startsWith("0")) && (paramAnonymousCharSequence.toString().trim().length() > 1) && (!paramAnonymousCharSequence.toString().substring(1, 2).equals("."))) {
                    paramEditText.setText(paramAnonymousCharSequence.subSequence(0, 1));
                    paramEditText.setSelection(1);
                }
            }
        });
    }

    public static List<String> splitText(String paramString1, String paramString2) {
        ArrayList localArrayList = new ArrayList();
        if (!isEmpty(paramString1)) {
            if (paramString1.contains(paramString2)) {
                String[] split = paramString1.split(paramString2);
                int j = split.length;
                int i = 0;
                while (i < j) {
                    paramString2 = split[i];
                    if (!isEmpty(paramString2)) {
                        localArrayList.add(paramString2);
                    }
                    i += 1;
                }
            }
            localArrayList.add(paramString1);
        }
        return localArrayList;
    }
}
