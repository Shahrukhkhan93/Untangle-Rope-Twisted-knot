package com.untangleropetwistedknot.app;
public class OrientationPolicyTest {
    static int checks=0;
    static void check(int sw,int w,int h,boolean windowed,OrientationPolicy.Mode expected){
        OrientationPolicy.Mode actual=OrientationPolicy.choose(sw,w,h,windowed);
        if(actual!=expected) throw new AssertionError(sw+"/"+w+"/"+h+": "+actual+" != "+expected);
        checks++;
    }
    public static void main(String[] args){
        var P=OrientationPolicy.Mode.PORTRAIT; var S=OrientationPolicy.Mode.SYSTEM;
        for(int sw:new int[]{280,320,360,393,480,599}){check(sw,sw,900,false,P);check(sw,900,sw,false,P);check(sw,sw,900,true,S);}
        for(int sw:new int[]{600,601,720,768,800,900,1024}){check(sw,sw,1200,false,P);check(sw,1200,sw,false,P);check(sw,sw,1200,true,S);}
        check(0,360,800,false,P);check(0,1000,700,false,P);check(0,0,0,false,P);
        check(390,390,844,false,P);check(720,720,900,false,P);check(390,390,844,false,P);
        System.out.println(checks+" orientation policy cases passed");
    }
}
