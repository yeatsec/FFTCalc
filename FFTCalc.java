/**
Eric Yeats
July 17, 2017
This program is a test for my first implementation of the FFT algorithm, I will eventually translate it to C/C++
in order to use it on a microcontroller.

I used Isai Damier's 'Reverse Bits of a Byte' code from http://www.geekviewpoint.com/java/bitwise/reverse_bits_byte
in order to implement the initial bit reversal step of the FFT algorithm.
*/

public class FFTCalc
{   
    public static final int SIZE = 256;
    public static final int BITS = (int) (Math.log10(SIZE)/Math.log10(2));
    public static double[] time = new double[SIZE];
    public static double[][] real = new double[BITS + 1][SIZE];
    public static double[][] imag = new double[BITS + 1][SIZE];
    public static double[] r_root = new double[SIZE];
    public static double[] i_root = new double[SIZE];


    public static void main(String[] args)
    {
        // generate time signal
        for (int i = 0; i < SIZE; i++)
        {
            time[i] = Math.cos(Math.PI * i / 4 + 0.1);
        }
        // initialize the real and imaginary roots
        for (int i = 0; i < SIZE; i++)
        {
            r_root[i] = Math.cos(2 * Math.PI * i / SIZE);
            i_root[i] = Math.sin(2 * Math.PI * i / SIZE);
        }
        // convert the time domain into unprocessed frequency spectra
        rearrangeArray();
        // process the frequency spectra into a proper fourier transform
        iterativeFFT();
        // print the data to the console
        System.out.println("\nreal data:\n");
        for (double real_elem: real[BITS])
        {
            System.out.println(real_elem);
        }
        System.out.println("\nimaginary data:\n");
        for (double imag_elem: imag[BITS])
        {
            System.out.println(imag_elem);
        }
        System.out.println("\nmagnitudes:\n");
        for (int i = 0; i < SIZE; i++)
        {
            System.out.println(Math.pow(Math.pow(real[BITS][i],2)+Math.pow(imag[BITS][i],2),0.5));
        }
    }

    public static int bitReorder(int index)
    {
        byte b_index = (byte) index;
        int output = 0;
        for(int i = BITS - 1; i >= 0; i--)
        {
            output += ((b_index&1) << i);
            b_index >>= 1;
        }
        return output;
    }

    public static void rearrangeArray()
    {
        System.out.println("\nBit Reorders\n");
        for (int i = 0; i < SIZE; i++)
        {
            real[0][bitReorder(i)] = time[i];
            System.out.println(i + " to " + bitReorder(i));
        }
    }

    public static void iterativeFFT()
    {
        for (int stage = 0; stage < BITS; stage++)
        {
            int half_frame = (int) Math.pow(2, stage);
            int frame = 2 * half_frame;
            int rootSpacing = SIZE/frame;
            for (int a = 0; a < SIZE; a += frame)
            {
                // butterfly
                for (int b = 0; b < half_frame; b++)
                {
                    // first half of butterfly calculation p + aq
                    int next_stage = stage + 1;
                    int left_butterfly_index = a + b;
                    int right_butterfly_index = left_butterfly_index + half_frame;
                    int left_root_index = b * rootSpacing;
                    int right_root_index = (b + half_frame) * rootSpacing;
                    real[next_stage][left_butterfly_index] = 
                        real[stage][left_butterfly_index] 
                        + (real[stage][right_butterfly_index] * r_root[left_root_index]) 
                        - (imag[stage][right_butterfly_index] * i_root[left_root_index]);
                    imag[next_stage][left_butterfly_index] = 
                        imag[stage][left_butterfly_index]
                        + (real[stage][right_butterfly_index] * i_root[left_root_index])
                        + (imag[stage][right_butterfly_index] * r_root[left_root_index]);
                    // second half of butterfly calculation p - aq
                    real[next_stage][right_butterfly_index] = 
                        real[stage][left_butterfly_index]
                        + (real[stage][right_butterfly_index] * r_root[right_root_index])
                        - (imag[stage][right_butterfly_index] * i_root[right_root_index]);
                    imag[next_stage][right_butterfly_index] = 
                        imag[stage][left_butterfly_index]
                        + (real[stage][right_butterfly_index] * i_root[right_root_index])
                        + (imag[stage][right_butterfly_index] * r_root[right_root_index]);

                }
            }
        }
    }
}