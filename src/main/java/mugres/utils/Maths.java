package mugres.utils;

public class Maths {
    private Maths() {}

    public static int map(int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static long map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static int lcm(int[] numbers) {
        int lcm = 1;
        int divisor = 2;

        while (true) {
            int count = 0;
            boolean divisible = false;

            for (int i = 0; i < numbers.length; i++) {
                if (numbers[i] == 0) {
                    return 0;
                } else if (numbers[i] < 0) {
                    numbers[i] = numbers[i] * -1;
                }
                if (numbers[i] == 1) {
                    count++;
                }
                if (numbers[i] % divisor == 0) {
                    divisible = true;
                    numbers[i] = numbers[i] / divisor;
                }
            }

            if (divisible) {
                lcm = lcm * divisor;
            } else {
                divisor++;
            }

            if (count == numbers.length) {
                return lcm;
            }
        }
    }
}
