package com.example.shared;

import com.badlogic.gdx.math.Vector2;

import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class SharedUtils {

    public static String serverMapToString(Map serverMap) {
            String toSend = "";
        Iterator itr = serverMap.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry e = (Map.Entry)itr.next();
            Long tid = ((Long)e.getKey());
            PlayerPos pos = ((PlayerPos)e.getValue());
            toSend += "TID:"+tid+"-";
            toSend+="POS:"+pos.x+","+pos.y+"";
            toSend+="ENDPOS\n";
        }
        System.out.println("::::ServerMapToString::::\n" + toSend);
        return toSend;
    }

    public static void stringToMap(Map clientMap, String payload) {
        String[] lines = payload.split("\n");
        for(int i = 0; i < lines.length; i++) {
            System.out.println("LINE " + i + ": "+  lines[i]);
        }
    }

}
