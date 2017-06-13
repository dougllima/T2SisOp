import java.util.Arrays;
import java.util.Collections;

/**
 * @author Douglas Leite
 *         Parte do projeto T2SisOp
 *         <p>
 *         13/06/2017.
 */
public class SCAN implements DiskScheduler {
    private int[] requestString;
    private int numCilindros;
    private int initCilindro;

    public SCAN(int[] requestString, int numCilindros, int initCilindro) {
        this.requestString = requestString;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    @Override
    public int serviceRequests() {
        int result = 0;

        // Ordenamos o vetor para poder iteragir mais facil sobre ele
        Arrays.sort(requestString);

        //Adicionando a distancia entre o começo e o primeiro ponto (primeiro solicitado ou o inicial, o que vier antes)
        result += requestString[0] < initCilindro ? requestString[0] : initCilindro;

        //Adicionando a distancia entre o fim e o ultimo ponto
        result += numCilindros - (requestString[requestString.length - 1] > initCilindro ?
                requestString[requestString.length - 1] : initCilindro);

        for (int i = 0; i < requestString.length - 1; i++) {
            if (requestString[i] < initCilindro && requestString[i + 1] < initCilindro)
                result += requestString[i + 1] - requestString[i];
            else if (requestString[i] > initCilindro && requestString[i + 1] > initCilindro)
                result += requestString[i + 1] - requestString[i];
            else
                result += requestString[i + 1];
        }

        return result;
    }

    @Override
    public void printGraph(String filename) {

    }

    public static void main(String[] args) {
        int[] requestString = {98, 183, 37, 122, 14, 124, 65, 67};
        int numCilindros = 199;
        int initCilindro = 53;

        DiskScheduler fcfs = new SCAN(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos: " + fcfs.serviceRequests());
    }
}
