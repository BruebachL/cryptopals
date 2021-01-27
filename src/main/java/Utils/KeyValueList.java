package Utils;

import java.util.ArrayList;

public class KeyValueList extends ArrayList<KeyValuePair<String, String>> {
    private static final long serialVersionUID = -6190055145473948917L;

    public KeyValueList(String s){
        super();
        parseString(s);
    }

    public KeyValueList(){
        super();
    }

    public void parseString(String s){
        boolean haveKey = false;
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();
        for(int i = 0; i < s.length(); i++){
            if(!haveKey && s.charAt(i) == '=') {
                haveKey = true;
                continue;
            } else if (s.charAt(i) == '&'){
                this.add(new KeyValuePair<>(currentKey.toString(), currentValue.toString()));
                currentKey = new StringBuilder();
                currentValue = new StringBuilder();
                haveKey = false;
                continue;
            }
            if(!haveKey){
                currentKey.append(s.charAt(i));
            } else {
                currentValue.append(s.charAt(i));
            }
        }
        if(haveKey){
            this.add(new KeyValuePair<>(currentKey.toString(), currentValue.toString()));
        }
    }

    public void add(String key, String value){
        add(new KeyValuePair<>(key, value));
    }

    public String getValue(String key){
        for(KeyValuePair<String, String> pair : this){
            if(pair.getKey().equals(key)){
                return pair.getValue();
            }
        }
        return "";
    }

    public String encode(){
        StringBuilder returnValue = new StringBuilder();
        for(KeyValuePair<String, String> pair : this){
            returnValue.append(pair.getKey()).append("=").append(pair.getValue()).append("&");
        }
        String returnString = returnValue.toString();
        return returnString.substring(0, returnString.length() - 1);
    }

    public String toString(){
        StringBuilder returnValue = new StringBuilder();
        returnValue.append("{\n");
        for(KeyValuePair<String, String> pair : this){
            returnValue.append("\t").append(pair.getKey()).append(": ").append(pair.getValue()).append("\n");
        }
        returnValue.append("}");
        return returnValue.toString();
    }
}
