/*
 * Created on 2006-8-29
 *
 */
package ormap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * 作者：李北金
 * 
 * 创建日期：2006-8-29
 * 
 * 说明：ORMAPPING。
 */
public class OrMap {
    public static Hashtable mappings = null;

    public static Mapping getMapping(String tableName) {
        if (tableName == null) {
            return null;
        }

        if (mappings == null) {
            mappings = new Hashtable();
        }

        Mapping mapping = (Mapping) mappings.get(tableName);
        if (mapping != null) {
            return mapping;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                OrMap.class.getResourceAsStream("/ormap/" + tableName
                        + ".property")));
        ArrayList fields;
        try {
            String s = br.readLine();
            String[] ss = null;
            fields = new ArrayList();
            MapField field = null;
            while(s != null){
                if(s.replaceAll(" ", "").equals("")){
                    s = br.readLine();
                    continue;
                }
                ss = s.split(":");
                if(ss.length != 3){
                    return null;
                }
                field = new MapField();
                field.setObjField(ss[0]);
                field.setTableField(ss[1]);
                field.setObjType(ss[2]);
                fields.add(field);
                s = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        mapping = new Mapping();
        mapping.setFields(fields);
        
        mappings.put(tableName, mapping);

        return mapping;
    }

    public static void main(String[] args) {
    }
}
