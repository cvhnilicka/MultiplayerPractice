package com.example.shared;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SharedUtils {

    private static final String delimiter = ":DELM:";

    public static String serverMapToString(Map serverMap) {
            String toSend = "";
        Iterator itr = serverMap.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry e = (Map.Entry)itr.next();
            Long tid = ((Long)e.getKey());
            PlayerPos pos = ((PlayerPos)e.getValue());
            toSend += tid + delimiter;
            toSend += pos.x + delimiter + pos.y +delimiter;
            toSend+= "\n";
        }
        return toSend;
    }

    public static void stringToMap(HashMap clientMap, String payload) {
        String[] lines = payload.split("\n");

        for(int i = 0; i < lines.length; i++) {
            String[] posInfo = lines[i].split(delimiter);
            PlayerPos newPos = new PlayerPos();
            newPos.x = Float.valueOf(posInfo[1]);
            newPos.y = Float.valueOf(posInfo[2]);
            if (clientMap.containsKey(Integer.valueOf(posInfo[0]))) {
                //update
                ((PlayerPos)clientMap.get(Integer.valueOf(posInfo[0]))).x = newPos.x;
                ((PlayerPos)clientMap.get(Integer.valueOf(posInfo[0]))).y = newPos.y;
            } else {
                // add
                clientMap.put(Integer.valueOf(posInfo[0]), newPos);
            }


        }
    }

}
