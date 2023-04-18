package qiandao;

public class Filter {
        String coma[] = {"damn", "你妈的", "他妈的", "你他妈", "fuck"};

        String Filter (String message){
            int o = coma.length;
            String ostr = null;
            for (int i = 0; i < o; i++) {
                message = message.replace(coma[i], "***");
                ostr = message;
            }
            return ostr;
        }
    }
