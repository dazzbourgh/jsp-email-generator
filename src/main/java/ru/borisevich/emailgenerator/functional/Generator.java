package ru.borisevich.emailgenerator.functional;

import ru.borisevich.emailgenerator.db.mysql.MySQLTemplateDAO;
import ru.borisevich.emailgenerator.model.Address;
import ru.borisevich.emailgenerator.model.Email;
import ru.borisevich.emailgenerator.model.Template;

import java.util.*;

public class Generator{
    public final static String[] KEYWORDS = {
            "TARGET",
            "INFO",
            "NAME",
            "AUTHOR",
            "TITLE",
            "STYLE",
            "BPM",
            "LINK"
    };

    public Generator(){
        
    }
    
    private Map<String, String> processTrackInfo(String trackInfoString) throws NullPointerException, IllegalArgumentException{
        for(String s : KEYWORDS) {
            if (!trackInfoString.contains(s)){
                System.out.println(s + ": " + trackInfoString);
                throw new NullPointerException();
            }
        }
        Map<String, String> returnValue = new LinkedHashMap<String, String>();
        String[] strings = trackInfoString.split(";");

        if(KEYWORDS.length != strings.length){
            throw new IllegalArgumentException("7 arguments required: " +
                    "target, info, author, title, style, bpm, link.");
        }

        for(String s : strings){
            String[] s1 = s.split(": ");
            returnValue.put(s1[0], s1[1]);
        }
        return returnValue;
    }
    
    private Template loadTemplate(){
        return new MySQLTemplateDAO().getRandomTemplate();
    }
    
    public List<Email> generateMails(Address[] addressees, String pTrackInfo) throws NullPointerException{
        Map<String, String> trackInfo = processTrackInfo(pTrackInfo);
        Email[] emails = new Email[addressees.length];
        Template template = loadTemplate();
        List<Email> returnValue = new ArrayList<>();
        for(int i = 0; i < addressees.length; i++){
            String text = new String(template.getText());
            for(Map.Entry<String, String> entry : trackInfo.entrySet()){
                text = text.replace(entry.getKey(), entry.getValue());
            }
            emails[i] = new Email();
            emails[i].setAddress(addressees[i].getAddress());
            emails[i].setText(text);
            emails[i].setDate(new Date());
            returnValue.add(emails[i]);
        }
        return returnValue;
    }
}