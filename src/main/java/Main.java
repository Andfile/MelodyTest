import jm.util.Read;
import jm.util.Write;
import org.apache.commons.lang3.ArrayUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ArrayList<MelodyStageDTO> melodyMap;
        String parcedTxtMelodyFile = "";
        initMenu();
        Scanner scanner = new Scanner(System.in);
        int menuItem = scanner.nextInt();

        switch (menuItem) {
            case 1:
                parcedTxtMelodyFile = convertTxtToString("MelodyGOT.txt");
                break;
            case 2:
                parcedTxtMelodyFile = convertTxtToString("MelodyHappyBirthday.txt");
                break;
            case 3:
                parcedTxtMelodyFile = convertTxtToString("MelodyIJ.txt");
                break;
            case 4:
                parcedTxtMelodyFile = convertTxtToString("MelodySMB.txt");
                break;
        }
        melodyMap = getMelodyMapFromString(parcedTxtMelodyFile);
        float[] melody = buildMelody(melodyMap);
        Write.audio(melody,"src/main/resources/newmelody.wav");
        System.out.println("work is done plz relax with new melody");
    }

    private static File getFile(String s) {
        ClassLoader classLoader = Main.class.getClassLoader();
        File file = new File(classLoader.getResource("accords/" + s + ".wav").getFile());
        return file;
    }

    private static String getFilePath(File file) {
        System.out.println(file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    private static String convertTxtToString(String fileName) {
        StringBuilder result = new StringBuilder("");
        ClassLoader classLoader = Main.class.getClassLoader();
        File file = new File(classLoader.getResource("melody/" +fileName).getFile());

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result.toString());
        return result.toString();
    }

    private static ArrayList<MelodyStageDTO> getMelodyMapFromString(String s) {
        ArrayList<MelodyStageDTO> mangena = new ArrayList<>();
        s = s.replace("\n", " ");
        String[] timed = s.split(" ");
        for (String string : timed) {
            String[] trim2 = string.split(":");
            mangena.add(new MelodyStageDTO(trim2[0], Float.valueOf(trim2[1])));
        }
        for (MelodyStageDTO str : mangena) {

            System.out.println(str.accord + ":" + str.time);
        }
        return mangena;
    }

    private static float[] buildMelody(ArrayList<MelodyStageDTO> list) {
        float[] melody = new float[0];

        for (MelodyStageDTO a : list) {
            File accordFile = getFile(a.accord);
            float[] accordd = Read.audio(getFilePath(accordFile));
            float duration = a.time - getDuration(accordFile);
            melody = ArrayUtils.addAll(melody, accordd);
            melody = ArrayUtils.addAll(melody, getSilentArrayByDuration(accordd.length, duration));
        }

        System.out.println(melody.length);
        return melody;
    }

    private static float[] getSilentArrayByDuration(int accordLength, float duration) {
        float newDuration = duration + 1f;
        float[] silent = new float[(int) (accordLength * newDuration)];
        return silent;
    }

    private static float getDuration(File file) {

        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        double durationInSeconds = (frames + 0.0) / format.getFrameRate();
        return (float) durationInSeconds;
    }

    private static void initMenu() {

        System.out.println("Please choose melody to create file");
        System.out.println("1: MelodyGOT");
        System.out.println("2: MelodyHappyBirthday");
        System.out.println("3: MelodyIJ");
        System.out.println("4: MelodySMB");
    }

}
