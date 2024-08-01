

import java.util.Random;

public class GridGenerator {
    public static void main(String[] args) {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int randInt = random.nextInt(100 - 1 + 1) + 1;
                if (randInt <= 47) {
                    System.out.print(" ");
                }
                else if (randInt <= 59){
                    System.out.print("*");
                } else {
                    System.out.print("S");
                }
            }
            System.out.println();
        }
    }
}
