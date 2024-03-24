package cn.frkovo.rhymaker;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] arg) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean b =false;
        String[] args = new String[3];
        System.out.println("输入 原始文件名");
        args[0] = scanner.next();
        System.out.println("输入 歌曲名");
        args[1] = scanner.next();
        System.out.println("输入 曲师名");
        args[2] = scanner.next();
        File file = new File(args[0]);
        if(!file.exists()){
            throw new FileNotFoundException();
        }
        System.out.println("Converting...");
        File file1 = new File("output/full/assets/minecraft/sounds/mob/horse/");
        if(!file1.exists())file1.mkdirs();
        Runtime.getRuntime().exec("ffmpeg.exe -i "+ args[0] +" -ac 1 -y -map 0:a -map_metadata -1 -acodec libvorbis ./output/full/assets/minecraft/sounds/mob/horse/death.ogg");
        Runtime.getRuntime().exec("ffmpeg.exe -i "+ args[0] + " ./output/full/pack.png");
        File file2 = new File("output/full","pack.mcmeta");
        System.out.println(file2.getAbsolutePath());
        if(!file2.exists())file2.createNewFile();
        FileOutputStream stream = new FileOutputStream(file2);
        String json = "{\n" +
                "  \"pack\": {\n" +
                "    \"pack_format\": 22,\n" +
                "    \"description\": \"§bRhythMC §3韵律方块 §6音乐资源包\\n§c§o"+args[1]+" §f- §e"+args[2]+"\"\n" +
                "  }\n" +
                "}";
        stream.write(json.getBytes(StandardCharsets.UTF_8));
        System.out.println("玩家材质包已制作完成 请打包");
        System.out.println("现在制作谱师材质包~");
        File files = new File("output/charter");
        if(!files.exists())files.mkdirs();
        Process process = Runtime.getRuntime().exec("ffmpeg.exe -i "+args[0]+" -ac 1 -y -map 0:a -map_metadata -1 -acodec libvorbis ./output/charter/temp.ogg");
        process.waitFor();
        System.out.println("分片中.. ");
        Process process2 = Runtime.getRuntime().exec("ffmpeg.exe -i ./output/charter/temp.ogg -f segment -segment_time 15 -c copy ./output/charter/temp%3d.ogg");
        process2.waitFor(10, TimeUnit.SECONDS);
        System.out.println("OK,处理中.. ");
        int i = 0;
        Thread.sleep(1000);
        Queue<Sound> avail_entities = Arrays.stream(Sound.values()).filter(sound -> sound.toString().endsWith("_DEATH") && sound.toString().startsWith("ENTITY_")).collect(Collectors.toCollection(LinkedList::new));
        boolean flag;
        while (!avail_entities.isEmpty()){
            File fileb = new File("output/charter","temp"+(i < 10 ? "00"+i : (i < 100 ? "0"+i : i))+".ogg");
            Sound sb = avail_entities.poll();
            assert sb != null;
            String val = sb.toString().replace("_DEATH","").replace("ENTITY_","").toLowerCase();
            i++;
            flag = fileb.exists();
            if(!flag)break;
            File filec = new File("output/charter/assets/minecraft/sounds/mob/"+val);
            System.out.println(fileb.getPath() + " -> " +filec.getPath() + "\\death.ogg");
            if(!filec.exists())filec.mkdirs();
            File filed = new File(filec,"death.ogg");
            Files.copy(fileb.toPath(), new FileOutputStream(filed));
            fileb.delete();
        }
        File rm = new File("output/charter/temp.ogg");
        rm.delete();
        File file2b = new File("output/charter","pack.mcmeta");
        file2b.createNewFile();
        FileOutputStream streamb = new FileOutputStream(file2b);
        streamb.write(json.getBytes(StandardCharsets.UTF_8));
         System.out.println("OK. Done");
    }
}
