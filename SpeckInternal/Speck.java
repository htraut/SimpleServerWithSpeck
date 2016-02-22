import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Objects;

/**
 * Created by herbt on 2/11/2016.
 */
public class Speck {

    private int n;
    private int m;
    private int T;

    private int alpha;
    private int beta;

    private int[] l;
    private int[] k;

    Speck(int n){

        // n = word size (16, 24, 32, 48, or 64)
        if(n == 16 || n == 24 || n == 32 || n == 48 || n == 64){
            this.n = n;
        }
        else{
            this.n = -1;
            return;
        }

        /*
        *   m = number of key words must be
        *  4 if n = 16,
        *  3 or 4 if n = 24 or 32,
        *  2 or 3 if n = 48,
        *  2 or 3 or 4 if n = 64
        */

        switch (n){
            case 16:
               m = 4;
               break;
            case 24:
               m = 4;
               break;
            case 32:
                m = 4;
                break;
            case 48:
                m = 3;
                break;
            case 64:
                m = 4;
                break;
            default:
                m = -1;
                return;
        }

        /*
         *  T = number of rounds
         *  22 if n = 16
         *  22 or 23 if n = 24, m = 3 or 4
         *  26 or 27 if n = 32, m = 3 or 4
         *  28 or 29 if n = 48, m = 2 or 3
         *  32, 33, or 34 if n = 64, m = 2, 3, or 4
         */

        switch(n){
            case 16:
                T = 22;
                break;
            case 24:
                T = 23;
                break;
            case 32:
                T = 27;
                break;
            case 48:
                T = 29;
                break;
            case 64:
                T = 34;
                break;
            default:
                T = -1;
                return;
        }

        k = new int[T];
        l = new int[2*T];

        if (n == 16){
            alpha = 7;
            beta = 2;
        }
        else{
            alpha = 8;
            beta = 3;
        }

    }

    public String SHA256(String value){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(value.getBytes());
            byte[] digest = md.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ArrayList<Integer> encrypt(String value){
        //Quick fix for strings with odd lengths
        if((value.length()%2) != 0){
            value += " ";
        }
        ArrayList<Integer> retVal= new ArrayList<Integer>();

        for(int i = 0; i < value.length(); i+=2){
            int x = value.charAt(i);
            int y = value.charAt(i + 1);

            _keyExpansion();
            int[] temp =  _encrypt(x, y);
            retVal.add(temp[0]);
            retVal.add(temp[1]);
        }

        return retVal;
    }

    public ArrayList<Integer> decrypt(int value[]){
        ArrayList<Integer> retVal = new ArrayList<Integer>();

        for(int i = 0; i < value.length; i+=2){
            int x = value[i];
            int y = value[i+1];

            _keyExpansion();
            int[] temp =  _decrypt(x, y);
            retVal.add(temp[0]);
            retVal.add(temp[1]);
        }

        return retVal;
    }

    private void _keyExpansion(){
        for(int i = 0; i < T-2; i++) {
            l[i+m-1] = (k[i] + _rotateRight(l[i], alpha)) ^ i;
            k[i+1] = _rotateLeft(k[i], beta) ^ l[i+m-1];
        }
    }

    private int[] _encrypt(int x, int y){
        for(int i = 0; i <= T-1; i++) {
            x = (_rotateRight(x, alpha) + y) ^ k[i];
            y = _rotateLeft(y, beta) ^ x;
        }
        return new int[]{x, y};
    }

    private int[] _decrypt(int x, int y){
        for(int i = T-1; i >= 0; i--) {
            y = _rotateRight(x ^ y, beta);
            x = _rotateLeft((x ^ k[i]) - y, alpha);
        }
        return new int[]{x, y};
    }

    private int _rotateLeft(int number, int amount) {
        return number << amount | number >>> (32-amount);
    }
    private int _rotateRight(int number, int amount) {
        return number >>> amount | number << (32-amount);
    }


}
