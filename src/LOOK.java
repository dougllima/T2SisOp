import java.util.Arrays;

/**
 * @author Douglas Leite
 * Parte do projeto T2SisOp
 * <p>
 * 13/06/2017.
 */
public class LOOK implements DiskScheduler{
    private int[] requestString;
    private int numCilindros;
    private int initCilindro;

    public LOOK(int[] requestString, int numCilindros, int initCilindro) {
        this.requestString = requestString;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    @Override
    public int serviceRequests() {
        int result = 0;

        // Ordenamos o vetor para poder iteragir mais facil sobre ele
        Arrays.sort(requestString);

        for (int i = 0; i < requestString.length - 1; i++) {
            if ((requestString[i] < initCilindro && requestString[i + 1] < initCilindro) ||
                (requestString[i] > initCilindro && requestString[i + 1] > initCilindro))
                result += requestString[i + 1] - requestString[i];
            else {
                result += requestString[0] + requestString[i + 1];
                result += initCilindro - requestString[i];
            }
        }

        return result;
    }

    @Override
    public void printGraph(String filename) {

    }

    public static void main(String[] args) {
        int[] requestString = {98, 183, 37, 122, 14, 124, 65, 67};
        int numCilindros = 200;
        int initCilindro = 53;

        DiskScheduler fcfs = new LOOK(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos: " + fcfs.serviceRequests());
    }
}
