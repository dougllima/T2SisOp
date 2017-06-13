/**
 * @author Douglas Leite
 * Parte do projeto T2SisOp
 * <p>
 * 13/06/2017.
 */

import static java.lang.Math.abs;

public class FCFS implements DiskScheduler {
    private int[] requestString;
    private int numCilindros;
    private int initCilindro;

    public FCFS(int[] requestString, int numCilindros, int initCilindro) {
        this.requestString = requestString;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    @Override
    public int serviceRequests() {
        int result;

        result = abs(initCilindro - requestString[0]);
        for (int i = 0; i < requestString.length - 1; i++) {
            result += abs(requestString[i] - requestString[i + 1]);
        }

        return result;
    }

    @Override
    public void printGraph(String filename) {

    }

    public static void main(String[] args) {
        int[] requestString = {95, 180, 34, 119, 11, 123, 62, 64};
        int numCilindros = 199;
        int initCilindro = 50;

        DiskScheduler fcfs = new FCFS(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos: " + fcfs.serviceRequests());
    }
}
