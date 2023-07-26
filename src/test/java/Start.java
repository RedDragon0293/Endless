import net.minecraft.client.main.Main;

import java.util.Arrays;

public class Start {
    /*
    -userType msa
    -uuid
    2dca4d71dc9f4c9ab70e9bc4013c87e7
    -accessToken
    eyJraWQiOiJhYzg0YSIsImFsZyI6IkhTMjU2In0.eyJ4dWlkIjoiMjUzNTQxMzcwNjg0OTIzMiIsImFnZyI6IkFkdWx0Iiwic3ViIjoiODg3MWY4ZDctNzc3Yy00MTQzLThiM2QtOTlmYWZiOWQzNDczIiwiYXV0aCI6IlhCT1giLCJucyI6ImRlZmF1bHQiLCJyb2xlcyI6W10sImlzcyI6ImF1dGhlbnRpY2F0aW9uIiwiZmxhZ3MiOlsidHdvZmFjdG9yYXV0aCIsIm9yZGVyc18yMDIyIl0sInBsYXRmb3JtIjoiVU5LTk9XTiIsInl1aWQiOiIyNGY0Y2IxYzIzNDVkMjQyZTZmMTc0YTA2NDI0YjYxOSIsIm5iZiI6MTY5MDExMjM3NCwiZXhwIjoxNjkwMTk4Nzc0LCJpYXQiOjE2OTAxMTIzNzR9.HKZRsRvVoMdXnfT9C4YGqhVgcwVda8zCYpqYF948XL4
     */
    public static void main(String[] args) {
        Main.main(concat(new String[]{
                "--version", "Endless",
                "--uuid", "2dca4d71dc9f4c9ab70e9bc4013c87e7",
                "--accessToken", "eyJraWQiOiJhYzg0YSIsImFsZyI6IkhTMjU2In0.eyJ4dWlkIjoiMjUzNTQxMzcwNjg0OTIzMiIsImFnZyI6IkFkdWx0Iiwic3ViIjoiODg3MWY4ZDctNzc3Yy00MTQzLThiM2QtOTlmYWZiOWQzNDczIiwiYXV0aCI6IlhCT1giLCJucyI6ImRlZmF1bHQiLCJyb2xlcyI6W10sImlzcyI6ImF1dGhlbnRpY2F0aW9uIiwiZmxhZ3MiOlsidHdvZmFjdG9yYXV0aCIsIm9yZGVyc18yMDIyIl0sInBsYXRmb3JtIjoiVU5LTk9XTiIsInl1aWQiOiIyNGY0Y2IxYzIzNDVkMjQyZTZmMTc0YTA2NDI0YjYxOSIsIm5iZiI6MTY5MDE5OTkxNiwiZXhwIjoxNjkwMjg2MzE2LCJpYXQiOjE2OTAxOTk5MTZ9.Eg4d-TWqK7k8tMLNczf7nIICe3iRNsZOZiZNmXx6JtU",
                "--assetsDir", "assets",
                "--assetIndex", "1.8",
                "--userProperties", "{}",
                "--userType", "msa"
        }, args));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
