package dbsnparser;

import java.util.HashMap;
public class IUPACResolver {

    private HashMap map;

    public IUPACResolver() {
        map = new HashMap();
        map.put("A", "A");
        map.put("C", "C");
        map.put("G", "G");
        map.put("T", "T");
        map.put("U", "U");
        map.put("A/C", "M");
        map.put("A/G", "R");
        map.put("A/T", "W");
        map.put("C/G", "S");
        map.put("C/T", "Y");
        map.put("G/T", "K");
        map.put("A/C/G", "V");
        map.put("A/C/T", "H");
        map.put("A/G/T", "D");
        map.put("C/G/T", "B");
    }
    public String resolve(String alleleSummary) {
        String code = (String)map.get(alleleSummary);
        if(code == null) {
            code = "N";
        }
        return code;
    }
}